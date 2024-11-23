package com.ychat.common.chat.service.handler;

import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.utils.Assert.AssertUtil;
import cn.hutool.core.bean.BeanUtil;
import com.ychat.common.chat.domain.dto.ChatMessageReq;
import com.ychat.common.chat.service.adapter.MessageAdapter;
import com.ychat.common.chat.service.factory.MsgHandlerFactory;
import com.ychat.common.user.dao.MessageDao;
import com.ychat.common.user.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * Description: 消息处理器抽象类
 * Req: 处理器处理的消息类型
 */
public abstract class AbstractMsgHandler<Req> {

    @Autowired
    private MessageDao messageDao;

    // 获取泛型 Class 的方法
    private Class<Req> bodyClass;

    @PostConstruct
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型
     */
    abstract MessageTypeEnum getMsgTypeEnum();

    // 可以重写，也可以不重写
    protected void checkMsg(Req body, Long roomId, Long uid) {

    }

    /**
     * 双重事务，默认传播行为即在上一个事务之后继续往下传递
     * @param request
     * @param uid
     * @return
     */
    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid) {
        Req body = this.toBean(request.getBody());
        // 统一校验
        AssertUtil.allCheckValidateThrow(body);
        // 子类扩展校验
        checkMsg(body, request.getRoomId(), uid);
        // 将 Req 的 Body 转换为 Message 进行插入
        Message newMessage = MessageAdapter.buildMsgSave(request, uid);
        // 统一保存
        messageDao.save(newMessage);
        // 子类对先前 Message 做二次扩展自定义保存
        saveMsg(newMessage, body);
        return newMessage.getId();
    }

    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (Req) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    // 子类必须重写
    protected abstract void saveMsg(Message message, Req body);

    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
