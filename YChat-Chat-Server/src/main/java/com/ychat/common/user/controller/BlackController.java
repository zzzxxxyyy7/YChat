package com.ychat.common.user.controller;

import com.ychat.common.Enums.RoleEnum;
import com.ychat.common.front.Response.ApiResult;
import com.ychat.common.user.domain.dto.BlackReq;
import com.ychat.common.user.service.IBlackService;
import com.ychat.common.user.service.IRoleService;
import com.ychat.common.utils.Assert.AssertUtil;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户表 前端控制器
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/black")
@Api(tags = "用户相关")
public class BlackController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IBlackService blacksService;

    @GetMapping("/private/test")
    @ApiOperation("测试")
    public ApiResult<Void> test() {
        return ApiResult.success();
    }

    @PostMapping("/private/blackUid")
    @ApiOperation("拉黑uid")
    public ApiResult<?> blackUid(@RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasRole = roleService.hasRole(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasRole, "没有权限");
        blacksService.blackUid(req);
        return ApiResult.success("拉黑成功");
    }

    @PostMapping("/private/blackIp")
    @ApiOperation("拉黑Ip")
    public ApiResult<?> blackIp(@RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasRole = roleService.hasRole(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasRole, "没有权限");
        blacksService.BlackIp(req.getIp());
        return ApiResult.success("拉黑成功");
    }

}

