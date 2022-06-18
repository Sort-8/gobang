package com.mobile.fivechess.mapper;

import java.util.List;
import com.mobile.fivechess.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author panghai
 * @date 2022-06-15
 */
@Mapper
public interface UserMapper
{
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
     * 新增用户
     *
     * @param user 用户
     * @return 结果
     */
    public int insertUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    public int updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(String userId);
}
