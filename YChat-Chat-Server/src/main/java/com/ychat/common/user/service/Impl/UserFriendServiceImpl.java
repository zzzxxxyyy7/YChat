package com.ychat.common.user.service.Impl;

import com.ychat.common.front.Request.CursorPageBaseReq;
import com.ychat.common.front.Request.PageBaseReq;
import com.ychat.common.front.Response.CursorPageBaseResp;
import com.ychat.common.front.Response.PageBaseResp;
import com.ychat.common.user.domain.dto.FriendApplyReq;
import com.ychat.common.user.domain.dto.FriendApproveReq;
import com.ychat.common.user.domain.dto.FriendCheckReq;
import com.ychat.common.user.domain.vo.FriendApplyResp;
import com.ychat.common.user.domain.vo.FriendCheckResp;
import com.ychat.common.user.domain.vo.FriendResp;
import com.ychat.common.user.domain.vo.FriendUnreadResp;
import com.ychat.common.user.service.IUserFriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserFriendServiceImpl implements IUserFriendService {

    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        return null;
    }

    @Override
    public void apply(Long uid, FriendApplyReq request) {

    }

    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        return null;
    }

    @Override
    public FriendUnreadResp unread(Long uid) {
        return null;
    }

    @Override
    public void applyApprove(Long uid, FriendApproveReq request) {

    }

    @Override
    public void deleteFriend(Long uid, Long friendUid) {

    }

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        return null;
    }
}
