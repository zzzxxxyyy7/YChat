package com.ychat.common.User.Dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.Chat.domain.dto.ChatMessagePageReq;
import com.ychat.common.Constants.Enums.Impl.MessageStatusEnum;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Mapper.MessageMapper;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Utils.Request.CursorUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 消息表 服务实现类
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    /**
     * 计算当前会话下，本条消息和被回复的消息之间的消息数量差值
     * @param roomId
     * @param fromId
     * @param toId
     * @return
     */
    public Integer getGapCount(Long roomId, Long fromId, Long toId) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, fromId) // 大于被回复的消息
                .le(Message::getId, toId) // 小于新插入的消息
                .count();
    }

    /**
     * 游标翻页查询查询会话消息记录列表
     * @param roomId
     * @param request
     * @param lastMsgId
     * @return
     */
    public CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        }, Message::getId);
    }

    /**
     * 查询某个会话下的未读消息总数
     * @param roomId
     * @param readTime
     * @return
     */
    public Integer getUnReadCount(Long roomId, Date readTime) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count();
    }

    /**
     * 根据房间ID逻辑删除消息
     *
     * @param roomId  房间ID
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByRoomId(Long roomId, List<Long> uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaUpdateWrapper<Message> wrapper = new UpdateWrapper<Message>().lambda()
                    .eq(Message::getRoomId, roomId)
                    .in(Message::getFromUid, uidList)
                    .set(Message::getStatus, MessageStatusEnum.DELETE.getStatus());
            return this.update(wrapper);
        }
        return false;
    }








}
