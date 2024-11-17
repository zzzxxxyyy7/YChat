package com.ychat.common.user.controller;


import Constants.front.Response.ApiResult;
import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.dto.WearingBadgeReq;
import com.ychat.common.user.domain.vo.BadgeResp;
import com.ychat.common.user.domain.vo.UserInfoVo;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户表 前端控制器
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/private/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoVo> userInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PostMapping("/private/modifyName")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @GetMapping("/private/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PostMapping("/private/wearBadge")
    @ApiOperation("佩戴徽章")
    public ApiResult<List<BadgeResp>> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req.getItemId());
        return ApiResult.success();
    }
}

