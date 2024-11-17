package com.ychat.common.user.service;

import Constants.front.Request.CursorPageBaseReq;
import Constants.front.Request.PageBaseReq;
import com.ychat.common.utils.Request.CursorPageBaseResp;
import Constants.front.Response.PageBaseResp;
import com.ychat.common.user.domain.dto.FriendApplyReq;
import com.ychat.common.user.domain.dto.FriendApproveReq;
import com.ychat.common.user.domain.dto.FriendCheckReq;
import com.ychat.common.user.domain.vo.FriendApplyResp;
import com.ychat.common.user.domain.vo.FriendCheckResp;
import com.ychat.common.user.domain.vo.FriendResp;
import com.ychat.common.user.domain.vo.FriendUnreadResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-17
 */
public interface IUserFriendService {

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param request 请求
     * @param uid     uid
     * @return {@link FriendCheckResp}
     */
    FriendCheckResp check(Long uid, FriendCheckReq request);

    /**
     * 应用
     * 申请好友
     *
     * @param request 请求
     * @param uid     uid
     */
    void apply(Long uid, FriendApplyReq request);

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    FriendUnreadResp unread(Long uid);

    /**
     * 同意好友申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void applyApprove(Long uid, FriendApproveReq request);

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    void deleteFriend(Long uid, Long friendUid);

    /**
     * 好友列表
     *
     * @param uid
     * @param request
     * @return
     */
    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

}
