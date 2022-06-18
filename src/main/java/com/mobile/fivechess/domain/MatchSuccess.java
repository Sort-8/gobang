package com.mobile.fivechess.domain;

/**
 * @Author: panghai
 * @Date: 2022/06/18/18:23
 * @Description: 匹配成功实体
 */
public class MatchSuccess {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 是否先手
     */
    private boolean isFirst;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public MatchSuccess(){}

    public MatchSuccess(String userId, boolean isFirst) {
        this.userId = userId;
        this.isFirst = isFirst;
    }
}
