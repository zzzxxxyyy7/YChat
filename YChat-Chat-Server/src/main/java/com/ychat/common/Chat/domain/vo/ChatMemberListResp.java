package com.ychat.common.Chat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天窗口 @ 专用 - 展示会话成员列表的成员信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberListResp {

    @ApiModelProperty("uid")
    private Long uid;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("头像")
    private String avatar;

}
