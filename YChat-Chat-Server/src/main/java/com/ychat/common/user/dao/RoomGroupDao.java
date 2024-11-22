package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.RoomGroup;
import com.ychat.common.user.mapper.RoomGroupMapper;
import com.ychat.common.user.service.IRoomGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群聊会话表 服务实现类
 *
 * @author ${author}
 * @since 2024-11-23
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }
}
