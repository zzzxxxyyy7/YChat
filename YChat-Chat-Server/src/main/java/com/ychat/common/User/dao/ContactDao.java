package com.ychat.common.User.Dao;

import com.ychat.common.Chat.domain.dto.ChatMessageReadReq;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.User.Domain.entity.Contact;
import com.ychat.common.User.Domain.entity.Message;
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

    /**
     * 查询这个 RoomId 下的记录总数（即加入这个会话的总人数）
     * @param roomId
     * @return
     */
    public Integer getTotalCount(Long roomId) {
        return lambdaQuery()
                .eq(Contact::getRoomId, roomId)
                .count();
    }

    /**
     * 查询消息已读数量
     * @param message
     * @return
     */
    public Integer getReadCount(Message message) {
        return lambdaQuery()
                .eq(Contact::getRoomId, message.getRoomId())
                .ne(Contact::getUid, message.getFromUid()) // 不需要查询自己是否已读
                .ge(Contact::getReadTime, message.getCreateTime()) // 阅读时间大于这条消息的创建时间就算已读
                .count();
    }

    public CursorPageBaseResp<Contact> getReadPage(Message message, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
            wrapper.ne(Contact::getUid, message.getFromUid()); // 不需要查询出自己
            wrapper.ge(Contact::getReadTime, message.getCreateTime()); // 已读时间大于等于消息发送时间
        }, Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> getUnReadPage(Message message, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
            wrapper.ne(Contact::getUid, message.getFromUid()); // 不需要查询出自己
            wrapper.lt(Contact::getReadTime, message.getCreateTime()); // 已读时间小于消息发送时间
        }, Contact::getReadTime);
    }

}
