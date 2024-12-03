package com.ychat.common.User.Services;


import com.ychat.common.Chat.domain.vo.MsgReadInfoDTO;
import com.ychat.common.User.Domain.entity.Message;

import java.util.List;
import java.util.Map;

/**
 * 会话列表 服务类
 */
public interface IContactService {

    /**
     * 查询并封装消息已读未读总数
     * @param messages
     * @return
     */
    Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages);

}
