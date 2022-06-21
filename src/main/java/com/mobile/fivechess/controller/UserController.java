package com.mobile.fivechess.controller;

import com.github.pagehelper.PageHelper;
import com.mobile.fivechess.domain.TableDataInfo;
import com.mobile.fivechess.domain.User;
import com.mobile.fivechess.service.IUserService;
import com.mobile.fivechess.utils.EloUtil;
import com.mobile.fivechess.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 *
 * @author panghai
 * @date 2022-06-15
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    private String prefix = "/user";

    @Autowired
    private IUserService userService;

    @GetMapping()
    public String user() {
        return prefix + "/user";
    }

    /**
     * 登录方法
     *
     * @param u 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ResponseBody
    public AjaxResult login(@RequestBody User u) {
        User user = userService.login(u.getUserId());
        if (user == null) {
            return AjaxResult.error("登陆失败");
        }
        return AjaxResult.success("登陆成功", user);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    @ResponseBody
    public AjaxResult getInfo(String userId) {
        User user = userService.selectUserById(userId);
        if (user != null) {
            return AjaxResult.success(user);
        }
        return AjaxResult.error("没有该用户");
    }

    /**
     * 查询用户列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(@RequestBody User user) {
        PageHelper.startPage(1, 10).setReasonable(true);
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 比赛结算积分、等级分
     *
     * @param u 用户
     * @return 是否结算成功
     */
    @PutMapping("/playRes")
    @ResponseBody
    public AjaxResult playRes(@RequestBody User u) {
        User user = userService.selectUserById(u.getUserId());
        User rivalUser = userService.selectUserById(u.getRivalUserId());
        if (user == null) {
            return AjaxResult.error("没有该用户");
        }
        if (rivalUser == null) {
            return AjaxResult.error("没有对手");
        }
        // 比赛结果
        int playRes = u.getPlayRes();
        // 用户等级分
        double rating = user.getRating();
        // 对手等级分
        double rivalRating = rivalUser.getRating();

        // 比赛结果依次是胜、平、负
        if (playRes == 1) {
            user.setIntegral(user.getIntegral() + 1);
            user.setRating(EloUtil.calculate(rating, rivalRating, EloUtil.Win));
            user.setGameNumber(user.getGameNumber() + 1);
            user.setWinNumber(user.getWinNumber() + 1);

            if (rivalUser.getIntegral() > 0) {
                rivalUser.setIntegral(rivalUser.getIntegral() - 1);
            }
            if (rivalUser.getRating() > 0) {
                rivalUser.setRating(EloUtil.calculate(rivalRating, rating, EloUtil.Loss));
            }
            rivalUser.setGameNumber(rivalUser.getGameNumber() + 1);

        } else if (playRes == 0) {
            user.setRating(EloUtil.calculate(rating, rivalRating, EloUtil.Draw));
            user.setGameNumber(user.getGameNumber() + 1);
            rivalUser.setRating(EloUtil.calculate(rating, rivalRating, EloUtil.Draw));
            rivalUser.setGameNumber(rivalUser.getGameNumber() + 1);

        } else if (playRes == -1) {
            rivalUser.setIntegral(rivalUser.getIntegral() + 1);
            rivalUser.setRating(EloUtil.calculate(rivalRating, rating, EloUtil.Win));
            rivalUser.setGameNumber(rivalUser.getGameNumber() + 1);
            rivalUser.setWinNumber(rivalUser.getWinNumber() + 1);

            if (user.getIntegral() > 0) {
                user.setIntegral(user.getIntegral() - 1);
            }
            if (user.getRating() > 0) {
                user.setRating(EloUtil.calculate(rivalRating, rating, EloUtil.Loss));
            }
            user.setGameNumber(user.getGameNumber() + 1);

        } else {
            return AjaxResult.error("没有比赛结果");
        }

        EloUtil.match(user);
        EloUtil.match(rivalUser);
        userService.updateUser(rivalUser);
        return toAjax(userService.updateUser(user));
    }

    /**
     * 修改保存用户
     */
    @PutMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@RequestBody User user) {
        return toAjax(userService.updateUser(user));
    }

}
