package com.ychat.common.User.Dao;

import com.ychat.common.User.Domain.entity.RoomGroup;
import com.ychat.common.User.Mapper.RoomGroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群聊会话表 服务实现类
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    /**
     * 根据 ID 批量查询群聊会话
     * @param roomIds
     * @return
     */
    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }

    /**
     * 根据 ID 查询群聊会话
     * @param roomId
     * @return
     */
    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

}
