package com.ychat.common.user.controller;


import com.ychat.common.Enums.RoleEnum;
import com.ychat.common.front.Response.ApiResult;
import com.ychat.common.user.domain.dto.BlackReq;
import com.ychat.common.user.service.IBlackService;
import com.ychat.common.user.service.IRoleService;
import com.ychat.common.utils.Assert.AssertUtil;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 黑名单 前端控制器
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
@Controller
@RequestMapping("/black")
public class BlackController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IBlackService blacksService;

    @PostMapping("/private/blackUid")
    @ApiOperation("拉黑uid")
    public ApiResult<Void> blackUid(@RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasRole = roleService.hasRole(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasRole, "没有权限");
        blacksService.blackUid(req);
        return ApiResult.success();
    }

    @PostMapping("/private/blackIp")
    @ApiOperation("拉黑Ip")
    public ApiResult<Void> blackIp(@RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasRole = roleService.hasRole(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasRole, "没有权限");
        blacksService.BlackIp(req.getIp());
        return ApiResult.success();
    }
}

