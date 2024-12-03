package com.ychat.common.User.Dao;

import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.User.Domain.entity.Contact;
import com.ychat.common.User.Mapper.ContactMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Utils.Request.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会话列表
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 更新所有人的会话时间，没有就直接插入
     */
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }

    /**
     * 查找该用户 UID 在这个会话 RoomID 下的会话记录
     * @param uid
     * @param roomId
     * @return
     */
    public Contact get(Long uid, Long roomId) {
        return lambdaQuery()
                .eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    /**
     * 获取用户会话列表
     */
    public CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Contact::getUid, uid);
        }, Contact::getActiveTime);
    }

    /**
     * 获取这个用户在会话中的记录
     * @param roomIds
     * @param uid
     * @return
     */
    public List<Contact> getByRoomIds(List<Long> roomIds, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId, roomIds)
                .eq(Contact::getUid, uid)
                .list();
    }

}
