package com.ychat.common.user.service;

import com.ychat.common.user.service.adapter.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * Description: 处理与微信api的交互逻辑
 */
@Service
@Slf4j
public class WxMsgService {

    /**
     * 用户的openId和前端登录场景code的映射关系
     */
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Value("${wx.mp.callback}")
    private String callback;

    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {

        // 授权地址
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));

        return TextBuilder.build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage);
    }

}
