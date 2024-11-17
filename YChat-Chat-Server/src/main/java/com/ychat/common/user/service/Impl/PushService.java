package com.ychat.common.user.service.Impl;

import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
import org.springframework.stereotype.Service;

@Service
public class PushService {
//
//    @Autowired
//    private MQProducer mqProducer;
//
//    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
//        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg));
//    }
//
//    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
//        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
//    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        // TODO 推送有新的好友申请记录到客户端
    }
}
