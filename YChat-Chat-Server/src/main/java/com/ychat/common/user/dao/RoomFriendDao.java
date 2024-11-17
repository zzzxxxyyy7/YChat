package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import Constants.Enums.NormalOrNoEnum;
import com.ychat.common.user.domain.entity.RoomFriend;
import com.ychat.common.user.mapper.RoomFriendMapper;
import org.springframework.stereotype.Service;

/**
 * 单聊会话表 服务实现类
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend getByKey(String key) {
        return lambdaQuery().eq(RoomFriend::getRoomKey, key).one();
    }

    /**
     * 重启会话
     * @param id
     */
    public void restoreRoom(Long id) {
        lambdaUpdate()
                .eq(RoomFriend::getId, id)
                .set(RoomFriend::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .update();
    }
}
