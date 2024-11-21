package com.ychat.common.config.Redis;

public class RedisKeyBuilder {

    /**
     * 项目通用
     */
    private static final String BASE_KEY = "yChat:";

    /**
     * 用户信息
     */
    public static final String USER_INFO_STRING = "userInfo:uid_%d";

    /**
     * 用户的信息更新时间
     */
    public static final String USER_INFO_MODIFY_STRING = "userInfoModify:uid_%d";

    /**
     * 用户的信息汇总
     */
    public static final String USER_INFO_SUMMARY_STRING = "userSummary:uid_%d";

    /**
     * 用户token存放
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";



    /**
     * 构建 Key
     * @param key
     * @param objects
     * @return
     */
    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }

}
