package com.ychat.common.User.Controller;


import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Domain.dto.SummeryInfoDTO;
import com.ychat.common.User.Domain.dto.req.ItemInfoReq;
import com.ychat.common.User.Domain.dto.req.ModifyNameReq;
import com.ychat.common.User.Domain.dto.req.SummeryInfoReq;
import com.ychat.common.User.Domain.dto.req.WearingBadgeReq;
import com.ychat.common.User.Domain.vo.BadgeResp;
import com.ychat.common.User.Domain.vo.ItemInfoVo;
import com.ychat.common.User.Domain.vo.UserInfoVo;
import com.ychat.common.User.Services.IUserService;
import com.ychat.common.Utils.Request.RequestHolder;
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
@RequestMapping("/capi/user")
@Api(tags = "用户相关")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoVo> userInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PostMapping("/public/summary/userInfo/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummeryInfoDTO>> getSummeryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummeryUserInfo(req));
    }

    @PostMapping("/public/badges/batch")
    @ApiOperation("徽章聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoVo>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }

    @PostMapping("/modifyName")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PostMapping("/wearBadge")
    @ApiOperation("佩戴徽章")
    public ApiResult<List<BadgeResp>> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req.getItemId());
        return ApiResult.success();
    }

}

