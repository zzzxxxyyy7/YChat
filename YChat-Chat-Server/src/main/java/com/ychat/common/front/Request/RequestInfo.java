package com.ychat.common.front.Request;

import lombok.Data;

/**
 * web请求信息收集类
 */
@Data
public class RequestInfo {
    private Long uid;
    private String ip;
}