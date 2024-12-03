package com.ychat.common.Chat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息已读或未读 Vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadResp {

    @ApiModelProperty("已读或者未读的用户uid，交由懒加载实现用户详情展示")
    private Long uid;

}
