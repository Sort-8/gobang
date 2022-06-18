package com.mobile.fivechess.controller;

import com.alibaba.fastjson.JSON;
import com.mobile.fivechess.domain.MatchSuccess;
import com.mobile.fivechess.domain.User;
import com.mobile.fivechess.service.IUserService;
import com.mobile.fivechess.utils.EloUtil;
import com.mobile.fivechess.utils.HttpStatus;
import com.mobile.fivechess.utils.WebsocketEncoder;
import com.mobile.fivechess.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: panghai
 * @Date: 2022/05/16/20:23
 * @Description: 匹配玩家
 */
@ServerEndpoint(value = "/match", encoders = {WebsocketEncoder.class})
@Component
public class MatchWebsocket {

    private static final Logger log = LoggerFactory.getLogger(MatchWebsocket.class);

    /**
     * 会话池
     */
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 匹配池
     */
    private static ConcurrentHashMap<String, User> matchPool = new ConcurrentHashMap<>();

    /**
     * 匹配线程池
     */
    private static ScheduledExecutorService sec = Executors.newScheduledThreadPool(10);

    /**
     * 超时时间（秒）
     */
    private static final int TIMEOUT = 20;

    /**
     * 超过一定时间（秒）
     */
    private static final int LATE_TIME = 10;

    private static IUserService userService;

    @Autowired
    public void setIUserService(IUserService userService) {
        MatchWebsocket.userService = userService;
    }

    @OnMessage
    public void onMessage(String userId, Session session) {
        User user = userService.selectUserById(userId);
        if (user == null) {
            sendMessage(session, new AjaxResult(HttpStatus.NOT_USER_ERROR, "用户不存在"));
            return;
        }
        // 设置匹配时间
        user.setMatchTime(System.currentTimeMillis());
        // 设置用户等级rank
        EloUtil.match(user);
        // 加入会话池
        sessionMap.put(user.getUserId(), session);

        //加入匹配池
        matchPool.put(user.getUserId(), user);

        // 每隔1秒匹配一次
        sec.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (matchPool.size() == 0 && sessionMap.size() == 0) {
                    log.info("任务完成...");
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        log.error("阻塞失败", e);
                    }
                    return;
                }
                log.info("开始匹配..." +
                        "当前会话池人数：" + sessionMap.size() +
                        "当前匹配池人数：" + matchPool.size());
                // 匹配程序入口
                matchProcess();
            }
        }, 1, 1, TimeUnit.SECONDS);

    }

    /**
     * 匹配算法
     * 将匹配池中的玩家进行匹配
     * 匹配规则：
     * 1、剔除匹配超时的玩家
     * 2、将匹配池中的玩家按分数段分布
     * 3、在同分数段中，优先将匹配时间最长玩家进行匹配
     * 4、在匹配时长超过一定时间后，允许跨分数段匹配
     */
    private static void matchProcess() {
        // 按等级分数段划分的数据结构
        ConcurrentHashMap<Integer, TreeSet<User>> samePointMap = new ConcurrentHashMap<>();

        // 超过一定时间的数据结构
        ConcurrentHashMap<Integer, TreeSet<User>> stepPointMap = new ConcurrentHashMap<>();

        // 超时一定时间可以跨分段，所以这里把7个分段划分了三个分段
        int rank1 = 3, rank2 = 5, rank3 = 7;

        // 按等级分数段划分
        for (User user : matchPool.values()) {
            // 超时了就将该玩家从匹配池和会话池剔除
            if ((System.currentTimeMillis() - user.getMatchTime()) / 1000 > TIMEOUT) {
                Session session = sessionMap.get(user.getUserId());
                sendMessage(session, new AjaxResult(HttpStatus.TIMEOUT, "匹配超时"));
                removeUserByPool(user);
                continue;
            }

            // 同分段划分玩家
            TreeSet<User> sameSet = samePointMap.get(user.getRank());
            if (sameSet == null) {
                sameSet = new TreeSet<>();
            }
            sameSet.add(user);
            samePointMap.put(user.getRank(), sameSet);

            // 如果超时一定时间，则扩大匹配范围，即跨分段划分玩家
            if ((System.currentTimeMillis() - user.getMatchTime()) / 1000 >= LATE_TIME) {
                if (user.getRank() <= rank1) {
                    TreeSet<User> stepSet = stepPointMap.get(rank1);
                    if (stepSet == null) {
                        stepSet = new TreeSet<>();
                    }
                    stepSet.add(user);
                    stepPointMap.put(rank1, stepSet);
                } else if (user.getRank() <= rank2) {
                    TreeSet<User> stepSet = stepPointMap.get(rank2);
                    if (stepSet == null) {
                        stepSet = new TreeSet<>();
                    }
                    stepSet.add(user);
                    stepPointMap.put(rank2, stepSet);
                } else if (user.getRank() <= rank3) {
                    TreeSet<User> stepSet = stepPointMap.get(rank3);
                    if (stepSet == null) {
                        stepSet = new TreeSet<>();
                    }
                    stepSet.add(user);
                    stepPointMap.put(rank3, stepSet);
                }
            }
        }

        // 同分数段匹配玩家
        matchPlayer(samePointMap);

        // 跨分数段匹配玩家
        matchPlayer(stepPointMap);

    }

    /**
     * 匹配玩家
     *
     * @param pointMap 划分好的玩家
     */
    private static void matchPlayer(ConcurrentHashMap<Integer, TreeSet<User>> pointMap) {
        Random r = new Random();
        for (TreeSet<User> sc : pointMap.values()) {
            //如果数量小于2，则跳过该分段的匹配
            if (sc.size() <= 1) {
                continue;
            }
            User u1 = null;
            User u2 = null;
            for (User user : sc) {
                if (u1 == null) {
                    u1 = user;
                    continue;
                }
                if (u2 == null) {
                    u2 = user;
                }
                // 找到最早进行匹配的两个玩家，即匹配成功后的操作
                if (u1 != null && u2 != null) {
                    boolean isFirst = r.nextBoolean();
                    sendMessage(sessionMap.get(u1.getUserId()),
                            AjaxResult.success("匹配成功", new MatchSuccess(u2.getUserId(), isFirst)));

                    sendMessage(sessionMap.get(u2.getUserId()),
                            AjaxResult.success("匹配成功", new MatchSuccess(u1.getUserId(), !isFirst)));
                    removeUserByPool(u1);
                    removeUserByPool(u2);
                    sc.remove(u1);
                    sc.remove(u2);
                    log.info(u1.getUserId() + " VS " + u2.getUserId() + "  匹配成功");
                    // 如果数量小于2，则退出该分段的匹配
                    if (sc.size() <= 1) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 从会话池剔除用户
     * 从匹配池剔除用户
     *
     * @param user 用户
     */
    private static void removeUserByPool(User user) {
        matchPool.remove(user.getUserId());
        sessionMap.remove(user.getUserId());
    }

    /***
     * 发送消息
     * @param session 会话
     * @param message 消息对象
     */
    private static void sendMessage(Session session, AjaxResult message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (EncodeException | IOException e) {
            log.error(session.getId() + " 发送消息失败", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> paramsMap = session.getRequestParameterMap();
        List<String> list = paramsMap.get("userId");
        if (list != null && list.size() > 0) {
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

}
