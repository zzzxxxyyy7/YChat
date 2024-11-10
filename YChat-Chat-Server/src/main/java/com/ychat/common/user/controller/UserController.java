package com.ychat.common.user.controller;


import com.ychat.common.front.Response.ApiResult;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.vo.UserInfoVo;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private UserDao userDao;

    @GetMapping("/private/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoVo> userInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }
}

