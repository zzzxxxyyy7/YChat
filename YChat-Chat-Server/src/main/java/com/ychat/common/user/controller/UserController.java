package com.ychat.common.user.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关")
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public UserInfoVo userInfo(@RequestParam Long id) {
        User userInfo = userDao.getById(id);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(userInfo , userInfoVo);
        return userInfoVo;
    }
}

