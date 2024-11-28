package com.ychat.common.User.Services.Impl;

import com.ychat.common.Chat.Constant.MQConstant;
import com.ychat.common.Chat.domain.dto.PushMessageDTO;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSBaseResp;
import com.ychat.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推送到用户服务类
 */
@Service
public class PushService {

    @Autowired
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }

}
