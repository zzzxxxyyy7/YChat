package com.ychat.common.config.Redis;

public class RedisKeyBuilder {

    /**
     * 项目
     */
    private static final String BASE_KEY = "ychat:";

    /**
     * 用户token存放
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }

}
