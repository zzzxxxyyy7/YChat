package com.ychat.common.user.controller;


import com.ychat.common.front.Request.CursorPageBaseReq;
import com.ychat.common.front.Request.PageBaseReq;
import com.ychat.common.front.Response.ApiResult;
import com.ychat.common.front.Response.CursorPageBaseResp;
import com.ychat.common.front.Response.PageBaseResp;
import com.ychat.common.user.domain.dto.FriendApplyReq;
import com.ychat.common.user.domain.dto.FriendApproveReq;
import com.ychat.common.user.domain.dto.FriendCheckReq;
import com.ychat.common.user.domain.dto.FriendDeleteReq;
import com.ychat.common.user.domain.vo.FriendApplyResp;
import com.ychat.common.user.domain.vo.FriendCheckResp;
import com.ychat.common.user.domain.vo.FriendResp;
import com.ychat.common.user.domain.vo.FriendUnreadResp;
import com.ychat.common.user.service.IUserFriendService;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/userFriend")
@Api(tags = "好友相关接口")
@Slf4j
public class UserFriendController {

    @Resource
    private IUserFriendService userFriendService;

    @GetMapping("/check")
    @ApiOperation("批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.check(uid, request));
    }

    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.apply(uid, request);
        return ApiResult.success();
    }

    @DeleteMapping()
    @ApiOperation("删除好友")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq request) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.deleteFriend(uid, request.getTargetUid());
        return ApiResult.success();
    }

    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.pageApplyFriend(uid, request));
    }

    @GetMapping("/apply/unread")
    @ApiOperation("申请未读数")
    public ApiResult<FriendUnreadResp> unread() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.unread(uid));
    }

    @PutMapping("/apply")
    @ApiOperation("审批同意")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq request) {
        userFriendService.applyApprove(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @GetMapping("/page")
    @ApiOperation("联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.friendList(uid, request));
    }
}

