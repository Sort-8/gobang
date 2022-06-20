package com.mobile.fivechess.utils;

/**
 * 返回状态码
 *
 * @author ruoyi
 */
public class HttpStatus
{

    /**
     * 匹配成功
     */
    public static final int MATCH_SUCCESS = 0;

    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;

    /**
     * 系统内部错误
     */
    public static final int ERROR = 500;

    /**
     * 超时
     */
    public static final int TIMEOUT = 10000;

    /**
     * JSON对象转换错误
     */
    public static final int JSON_ERROR = 10001;

    /**
     * 缺少参数
     */
    public static final int PARAMS_LACK_ERROR = 10002;

    /**
     * 用户不存在
     */
    public static final int NOT_USER_ERROR = 10003;
}
