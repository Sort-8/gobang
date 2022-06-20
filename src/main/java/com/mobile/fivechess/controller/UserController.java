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
     * @param user 用户
     * @return 是否结算成功
     */
    @PutMapping("/playRes")
    @ResponseBody
    public AjaxResult playRes(@RequestBody User user) {
        User u = userService.selectUserById(user.getUserId());
        int playRes = user.getPlayRes();
        double rivalRating = user.getRivalRating();
        if (u == null) {
            return AjaxResult.error("没有该用户");
        }
        // 比赛结果依次是胜、平、负
        if (playRes == 1) {
            u.setIntegral(u.getIntegral() + 1);
            u.setRating(EloUtil.calculate(u.getRating(), rivalRating, EloUtil.Win));
        } else if (playRes == -1) {
            if (u.getIntegral() - 1 > 0){
                u.setIntegral(u.getIntegral() - 1);
            }
            u.setRating(EloUtil.calculate(u.getRating(), rivalRating, EloUtil.Loss));
        } else if (playRes == 0) {
            u.setRating(EloUtil.calculate(u.getRating(), rivalRating, EloUtil.Draw));
        } else {
            return AjaxResult.error("没有比赛结果");
        }
        EloUtil.match(u);
        return toAjax(userService.updateUser(u));
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
