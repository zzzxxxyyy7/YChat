package com.ychat.common.Chat.Services;

import com.ychat.common.Chat.domain.dto.*;
import com.ychat.common.Chat.domain.vo.ChatMemberListResp;
import com.ychat.common.Chat.domain.vo.ChatRoomResp;
import com.ychat.common.Chat.domain.vo.MemberResp;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberResp;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 会话列表处理接口
 */
public interface RoomAppService {

    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(@Valid CursorPageBaseReq request, Long uid);


    ChatRoomResp getContactDetail(Long uid, @NotNull Long id);


    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);

    /**
     * 获取群组信息
     */
    MemberResp getGroupDetail(Long uid, @NotNull long id);

    /**
     * 获取群组群成员列表
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(@Valid MemberReq request);

    /**
     * @ 获取所有可以访问的群成员列表
     * @param request
     * @return
     */
    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    /**
     * 移除群成员
     * @param uid
     * @param request
     */
    void delMember(Long uid, MemberDelReq request);

    /**
     * 新建群聊
     * @param uid
     * @param request
     * @return
     */
    Long addGroup(Long uid, GroupAddReq request);

    /**
     * 邀请好友进群
     * @param uid
     * @param request
     */
    void addMember(Long uid, MemberAddReq request);

}
