package com.mobile.fivechess.domain;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * 用户对象 user
 *
 * @author panghai
 * @date 2022-06-15
 */
public class User implements Comparable<User> {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 等级分
     */
    private double rating;

    /**
     * 积分
     */
    private int integral;

    /**
     * 对战局数
     */
    private int gameNumber;

    /**
     * 胜利局数
     */
    private int winNumber;

    /**
     * 段位
     */
    private int rank;

    /**
     * 段位名称
     */
    private String rankName;

    /**
     * 开始匹配时间
     */
    private Long matchTime;

    /**
     * 比赛结果
     * 胜 1
     * 平 0
     * 负 -1
     */
    private int playRes;

    /**
     * 对手id
     */
    private String rivalUserId;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public int getWinNumber() {
        return winNumber;
    }

    public void setWinNumber(int winNumber) {
        this.winNumber = winNumber;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public Long getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Long matchTime) {
        this.matchTime = matchTime;
    }

    public int getPlayRes() {
        return playRes;
    }

    public void setPlayRes(int playRes) {
        this.playRes = playRes;
    }

    public String getRivalUserId() {
        return rivalUserId;
    }

    public void setRivalUserId(String rivalUserId) {
        this.rivalUserId = rivalUserId;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", rating=" + rating +
                ", integral=" + integral +
                ", gameNumber=" + gameNumber +
                ", winNumber=" + winNumber +
                ", rank=" + rank +
                ", rankName='" + rankName + '\'' +
                ", matchTime=" + matchTime +
                ", playRes=" + playRes +
                ", rivalUserId='" + rivalUserId + '\'' +
                ", isFirst=" + isFirst +
                '}';
    }

    @Override
    public int compareTo(User u) {
        return (int) (this.getMatchTime() - u.getMatchTime());
    }

}
