package com.mobile.fivechess.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mobile.fivechess.domain.Message;
import com.mobile.fivechess.utils.HttpStatus;
import com.mobile.fivechess.utils.WebsocketEncoder;
import com.mobile.fivechess.vo.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: panghai
 * @Date: 2022/06/18/10:20
 * @Description:
 */
@ServerEndpoint(value = "/play", encoders = {WebsocketEncoder.class})
@Component
public class PlayWebsocket {

    private static Logger log = LoggerFactory.getLogger(PlayWebsocket.class);

    /**
     * 会话池
     */
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 对战池
     */
    private static ConcurrentHashMap<String, String> playMap = new ConcurrentHashMap<>();

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Message m = null;
        try {
            m = JSONObject.parseObject(message, Message.class);
            if (m.getFrom() == null || m.getTo() == null) {
                throw new NullPointerException();
            }
        } catch (JSONException e) {
            log.error("JSON对象转换错误", e);
            sendMessage(session, new AjaxResult(HttpStatus.JSON_ERROR, "JSON对象转换错误"));
            return;
        } catch (NullPointerException e) {
            log.error("缺少发送人或接收人", e);
            sendMessage(session, new AjaxResult(HttpStatus.PARAMS_LACK_ERROR, "缺少发送人或接收人"));
            return;
        }

        /**
         * 获取接收人的session
         * 如果接收人上线则发送消息给接收人
         * 如果接收人还没有上线则告诉发送人发送失败信息
         */
        Session s = sessionMap.get(m.getTo());
        if (s != null) {
            s.getBasicRemote().sendText(JSONObject.toJSONString(m));
            playMap.put(m.getFrom(), m.getTo());
        } else {
            String rivalUserId = playMap.get(m.getTo());
            // 对手掉线或认输
            if (rivalUserId != null) {
                sendMessage(session, new AjaxResult(HttpStatus.RIVAL_GIVE_UP, "对手掉线或认输"));
                sessionMap.remove(m.getFrom());
            }else{
                sendMessage(session, new AjaxResult(HttpStatus.PARAMS_LACK_ERROR, "缺少发送人或接收人"));
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> paramsMap = session.getRequestParameterMap();
        List<String> list = paramsMap.get("userId");
        if (list != null && list.size() > 0) {
            // 连接成功则加入会话池
            sessionMap.put(list.get(0), session);
            sendMessage(session, AjaxResult.success("连接成功"));
            log.info("用户id：" + list.get(0) + "  已连接");
        } else {
            sendMessage(session, new AjaxResult(HttpStatus.PARAMS_LACK_ERROR, "缺少参数"));
        }
    }

    @OnClose
    public void onClose(Session session) {
        Map<String, List<String>> paramsMap = session.getRequestParameterMap();
        List<String> list = paramsMap.get("userId");
        if (list != null && list.size() > 0) {
            log.info("用户id：" + list.get(0) + "  关闭连接");
            log.info("会话池：去除用户id：" + list.get(0));
            sessionMap.remove(list.get(0));
        } else {
            sendMessage(session, new AjaxResult(HttpStatus.PARAMS_LACK_ERROR, "缺少参数"));
        }
    }

    /***
     * 发送消息
     * @param session 会话
     * @param message 消息对象
     */
    private void sendMessage(Session session, AjaxResult message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (EncodeException | IOException e) {
            log.error(session.getId() + " 发送消息失败", e);
        }
    }

}
