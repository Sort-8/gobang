package com.mobile.fivechess.service.impl;

import java.util.List;

import com.mobile.fivechess.service.IUserService;
import com.mobile.fivechess.utils.EloUtil;
import com.mobile.fivechess.utils.RandomName;
import com.mobile.fivechess.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mobile.fivechess.mapper.UserMapper;
import com.mobile.fivechess.domain.User;

/**
 * 用户Service业务层处理
 *
 * @author panghai
 * @date 2022-06-15
 */
@Service
public class UserServiceImpl implements IUserService
{
    @Autowired
    private UserMapper userMapper;

    /**
     * 登录验证
     *
     * @param userId 用户名
     * @return 用户
     */
    @Override
    public User login(String userId)
    {
        User user = userMapper.selectUserById(userId);
        if (user == null){
            user = new User();
            user.setUserId(userId);
            user.setNickname(RandomName.randomName());
            user.setRating(1500);
            user.setIntegral(0);
            EloUtil.match(user);
            userMapper.insertUser(user);
        }
        return user;
    }

    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    @Override
    public User selectUserById(String userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户
     */
    @Override
    public List<User> selectUserList(User user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        return userMapper.updateUser(user);
    }

}
