package com.ychat.common.Websocket.Services.Adapter;

import cn.hutool.core.bean.BeanUtil;
import com.ychat.common.Chat.domain.dto.ChatMessageMarkDTO;
import com.ychat.common.Chat.domain.dto.ChatMsgRecallDTO;
import com.ychat.common.Constants.Enums.Impl.ChatActiveStatusEnum;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.Websocket.Domain.Enums.WSRespTypeEnum;
import com.ychat.common.Websocket.Domain.Vo.Resp.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WebSocketAdapter {

    @Autowired
    private ChatService chatService;

    /**
     * 获取登录二维码
     * @param wxMpQrCodeTicket
     * @return
     */
    public static WSBaseResp<?> getRespLoginUrl(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        return resp;
    }

    /**
     * 获取登录成功响应信息
     * @param user
     * @param token
     * @return
     */
    public static WSBaseResp<?> getRespLoginSuccess(User user, String token, boolean roleIsAdmin) {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(roleIsAdmin ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    public static WSBaseResp<?> getRespLoginSuccess() {
        WSBaseResp<String> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        resp.setData("扫码成功，请授权登录");
        return resp;
    }

    public static WSBaseResp<?> getRespLoginFail() {
        WSBaseResp<String> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        resp.setData("Token 无效或者已过期，请重新登录");
        return resp;
    }

    /**
     * 返回用户加入黑名单消息到 Channel
     * @param user
     * @return
     */
    public static WSBaseResp<?> buildBlack(User user) {
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = WSBlack.builder()
                .uid(user.getId())
                .build();
        resp.setData(wsBlack);
        return resp;
    }

    /**
     * 当发生一条好友申请的时候，返回好友申请消息到 Channel
     * @param resp
     * @return
     */
    public static WSBaseResp<WSFriendApply> buildApplySend(WSFriendApply resp) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.APPLY.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }

    /**
     * 封装新消息返回
     * @param msgResp
     * @return
     */
    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    private static ChatMemberResp buildOnlineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        info.setLastOptTime(user.getLastOptTime());
        return info;
    }

    /**
     * 封装消息撤回信息
     * @param recallDTO
     * @return
     */
    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

    public static WSBaseResp<WSMsgMark> buildMsgMarkSend(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(dto, item);
        item.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark mark = new WSMsgMark();
        mark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(mark);
        return wsBaseResp;
    }

    public WSBaseResp<?> buildOnlineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify onlineOfflineNotify = new WSOnlineOfflineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOnlineInfo(user)));
        assembleNum(onlineOfflineNotify);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    private void assembleNum(WSOnlineOfflineNotify onlineOfflineNotify) {
        ChatMemberStatisticResp memberStatistic = chatService.getMemberStatistic();
        onlineOfflineNotify.setOnlineNum(memberStatistic.getOnlineNum());
    }

    public WSBaseResp<?> buildOfflineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify onlineOfflineNotify = new WSOnlineOfflineNotify();
        onlineOfflineNotify.setChangeList(Collections.singletonList(buildOfflineInfo(user)));
        assembleNum(onlineOfflineNotify);
        wsBaseResp.setData(onlineOfflineNotify);
        return wsBaseResp;
    }

    private static ChatMemberResp buildOfflineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        info.setLastOptTime(user.getLastOptTime());
        return info;
    }

}
