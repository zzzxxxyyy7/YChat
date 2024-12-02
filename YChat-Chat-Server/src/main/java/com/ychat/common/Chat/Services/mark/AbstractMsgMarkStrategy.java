package com.ychat.common.Chat.Services.mark;

import com.ychat.common.Chat.Event.MessageMarkEvent;
import com.ychat.common.Chat.domain.dto.ChatMessageMarkDTO;
import com.ychat.common.Constants.Enums.Impl.MessageMarkActTypeEnum;
import com.ychat.common.Constants.Enums.Impl.MessageMarkTypeEnum;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.Constants.Exception.BusinessException;
import com.ychat.common.User.Dao.MessageMarkDao;
import com.ychat.common.User.Domain.entity.MessageMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * 消息标记抽象类
 */
public abstract class AbstractMsgMarkStrategy {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    // 类型由子类去实现
    protected abstract MessageMarkTypeEnum getTypeEnum();

    // 标记消息
    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }

    // 取消消息标记
    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    // 子类会继承这个方法，并且在初始化后调用自身实现的 getTypeEnum 方法注册自身到 Factory
    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    protected void doMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void doUnMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    protected void exec(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        // 拿到标记类型
        Integer markType = getTypeEnum().getType();
        // 拿到行为类型
        Integer actType = actTypeEnum.getType();
        // 获取给定的旧消息标记记录
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        // 如果没有查到旧记录，标记类型又是取消，说明可能出现异常
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            // 没有就直接跳过操作
            return;
        }
        // 插入一条新消息, 或者修改一条消息
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        // ID 有值就是修改，没有值就是新增
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            // 修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
        }
    }

    private Integer transformAct(Integer actType) {
        if (actType == 1) {
            return YesOrNoEnum.NO.getStatus();
        } else if (actType == 2) {
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BusinessException("动作类型 1确认 2取消");
    }

}
