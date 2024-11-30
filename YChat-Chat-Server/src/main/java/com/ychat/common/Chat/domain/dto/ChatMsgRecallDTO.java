package com.ychat.common.Chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:消息撤回的推送类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {

    // 撤回的消息 ID
    private Long msgId;

    // 撤回的消息所在会话 ID
    private Long roomId;

    // 发起撤回行为的用户 UID
    private Long recallUid;

}
