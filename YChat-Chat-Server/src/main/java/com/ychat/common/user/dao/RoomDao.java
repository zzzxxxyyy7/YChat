package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.user.domain.entity.Room;
import com.ychat.common.user.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 会话表
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

    /**
     * 更新这个会话最新的一条消息记录
     * @param roomId
     * @param msgId
     * @param msgTime
     */
    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                .eq(Room::getId, roomId)
                .lt(Room::getLastMsgId, msgId) // 最后一条消息 ID 必须要小于刷新使用的这条消息 ID，避免因为消费时间差异导致数据被覆盖，相应的新建会话的初始数据也要是 0，避免发生 Null 导致判断错误
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }

}
