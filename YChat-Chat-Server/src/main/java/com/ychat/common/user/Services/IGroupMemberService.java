package com.ychat.common.User.Services;


import com.ychat.common.Chat.domain.dto.AdminAddReq;
import com.ychat.common.Chat.domain.dto.AdminRevokeReq;
import com.ychat.common.Chat.domain.dto.MemberExitReq;

import javax.validation.Valid;

/**
 * 会话成员表 服务类
 */
public interface IGroupMemberService {

    /**
     * 退出群聊
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void exitGroup(Long uid, MemberExitReq request);

    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void revokeAdmin(Long uid, AdminRevokeReq request);

}
