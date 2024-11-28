package com.ychat.common.Websocket.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 记录和前端连接的一些映射信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {

    /**
     * uid 如果为空，则未登录，代表游客
     * 登录了，记录 uid
     */
    private Long uid;

}