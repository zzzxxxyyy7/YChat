package com.ychat.common.Config.WX;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * WeChat mp properties
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    /**
     * 是否使用 redis 存储 access token
     */
    private boolean useRedis;

    /**
     * 多个公众号配置信息
     */
    private List<MpConfig> configs;

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的 appid
         */
        private String appId;

        /**
         * 设置微信公众号的 app secret
         */
        private String secret;

        /**
         * 设置微信公众号的 token
         */
        private String token;

        /**
         * 设置微信公众号的 EncodingAESKey
         */
        private String aesKey;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
