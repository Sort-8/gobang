package com.mobile.fivechess.service;

import java.util.List;
import com.mobile.fivechess.domain.User;

/**
 * 用户Service接口
 *
 * @author panghai
 * @date 2022-06-15
 */
public interface IUserService
{

    /**
     * 登录验证
     *
     * @param userId 用户名
     * @return 用户
     */
    public User login(String userId);

    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    public User selectUserById(String userId);

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户集合
     */
    public List<User> selectUserList(User user);

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    public int updateUser(User user);
}
