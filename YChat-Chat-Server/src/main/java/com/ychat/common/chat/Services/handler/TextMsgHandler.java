package com.ychat.common.Chat.Services.handler;

import com.ychat.common.Constants.Enums.Impl.MessageStatusEnum;
import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.Constants.Enums.Impl.RoleEnum;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.Domain.Dto.UrlInfo;
import com.ychat.common.SensitiveWord.SensitiveWordBootStrap;
import com.ychat.common.Utils.Assert.AssertUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ychat.common.Chat.domain.entity.msg.MessageExtra;
import com.ychat.common.Chat.domain.vo.TextMsgResp;
import com.ychat.common.Chat.Services.adapter.MessageAdapter;
import com.ychat.common.Chat.Services.cache.MsgCache;
import com.ychat.common.Chat.Services.factory.MsgHandlerFactory;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.Chat.domain.dto.TextMsgReq;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.IRoleService;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.User.Services.cache.UserInfoCache;
import com.ychat.common.Utils.Discover.PrioritizedUrlDiscover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Description: 普通文本消息
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MsgCache msgCache;

    @Autowired
    private UserCache userCache;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private SensitiveWordBootStrap sensitiveWordBootStrap;

    // URL 责任链
    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {

        // 校验该条消息是否是回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Message replyMsg = messageDao.getById(body.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), roomId, "只能回复相同会话内的消息");
        }

        // 校验该条消息是否存在 @ 对象
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {

            // 前端传入的@用户列表可能会重复，需要去重
            List<Long> atUidList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            Map<Long, User> userInfoMaps = userInfoCache.getBatch(atUidList);

            // 如果@用户不存在，userInfoCache 返回的map中依然存在该key，但是value为null，需要过滤掉再校验
            long batchCount = userInfoMaps.values().stream().filter(Objects::nonNull).count();
            AssertUtil.equal((long)atUidList.size(), batchCount, "被艾特的用户不存在");
            // 判断是否艾特了所有的群成员
            boolean atAll = body.getAtUidList().contains(0L);
            if (atAll) {
                // 艾特所有群成员下，校验是否有管理员权限
                AssertUtil.isTrue(iRoleService.hasRole(uid, RoleEnum.CHAT_MANAGER), "没有权限");
            }
        }

    }

    @Override
    public void saveMsg(Message msg, TextMsgReq body) {
        // 插入文本内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message updateMessage = new Message();
        updateMessage.setId(msg.getId());
        // 发送消息敏感词过滤
        updateMessage.setContent(sensitiveWordBootStrap.filter(body.getContent()));
        updateMessage.setExtra(extra);

        // 如果有回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(msg.getRoomId(), body.getReplyMsgId(), msg.getId());
            updateMessage.setGapCount(gapCount);
            // 设置被回复消息的 ID
            updateMessage.setReplyMsgId(body.getReplyMsgId());
        }

        // 判断消息url跳转
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);

        // 艾特功能
        if (CollectionUtil.isNotEmpty(body.getAtUidList())) {
            extra.setAtUidList(body.getAtUidList());
        }

        messageDao.updateById(updateMessage);
    }

    /**
     * 规定文本消息的展示格式
     * @param msg
     * @return
     */
    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlContentMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlContentMap).orElse(null));
        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));

        // 拿到回复的消息
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgCache::getMsg)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        if (reply.isPresent()) {
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMsgVO.setType(replyMessage.getType());
            // 这条回复消息具体的展示样式
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userCache.getUserInfo(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            // 消息是否可跳转，仅间隔在 100 条内才可跳转
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }

        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }

}
