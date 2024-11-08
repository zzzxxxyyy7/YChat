package com.ychat.common.user.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.user.service.WxMsgService;
import com.ychat.common.user.service.adapter.TextBuilder;
import com.ychat.common.user.service.adapter.UserAdapter;
import com.ychat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 存储 openId 和 二维码临时 Code 的关系
     */
    private static final ConcurrentHashMap<String , Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    /**
     * 用户的openId和前端登录场景code的映射关系
     */
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Value("${wx.mp.callback}")
    private String callback;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IUserService userService;

    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {

        String openid = wxMpXmlMessage.getFromUser();
        Integer loginCode = Integer.parseInt(getEventKey(wxMpXmlMessage));

        // 如果已经注册,直接登录成功
        if (ObjectUtil.isEmpty(loginCode)) {
            return null;
        }

        User user = userDao.getByOpenId(openid);

        // 是否已注册（拿到 openId 就可以注册）
        boolean registered = ObjectUtil.isNotEmpty(user);

        // 是否已授权
        boolean authorized = registered && ObjectUtil.isNotEmpty(user.getName());
        if (registered && authorized) {
            // 都有，才是登录成功
            webSocketService.scanLoginSuccess(loginCode, user.getId());
            return TextBuilder.build("您已经成功登录", wxMpXmlMessage);
        }

        // 未注册，注册
        if(!registered) {
            User newUser = UserAdapter.buildUser(openid);
            userService.register(newUser);
        }

        WAIT_AUTHORIZE_MAP.put(openid, loginCode);

        // 传递前端用户扫码成功，请求授权的消息
        webSocketService.waitAuthorize(loginCode);

        // 授权地址
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));

        return TextBuilder.build("请点击链接登录：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage);
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        // 扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace("qrscene_", "");
    }

    /**
     * 用户授权
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());

        // 更新用户信息，防抖，只有首次登录才更新
        if (StringUtils.isEmpty(user.getName())) {
            fillUserInfo(user.getId(), userInfo);
        }

        // 拿到 Code 找到 Channel 进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(user.getOpenId());

        // 拿到 code ，执行登录逻辑
        webSocketService.scanLoginSuccess(code, user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User update = UserAdapter.buildAuthorizeUser(uid, userInfo);
        // 用户名唯一，重试5次
        for (int i = 0; i < 5; i++) {
            try {
                userDao.updateById(update);
                return;
            } catch (DuplicateKeyException e) {
                log.info("fill userInfo duplicate uid:{},info:{}", uid, userInfo);
            } catch (Exception e) {
                log.error("fill userInfo fail uid:{},info:{}", uid, userInfo);
            }
            update.setName("名字重置" + RandomUtil.randomInt(100000));
        }
    }
}
