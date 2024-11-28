package com.ychat.common.User.Services.handler;

import com.ychat.common.User.Services.WxMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 扫码事件处理器
 */
@Component
public class ScanHandler extends AbstractHandler {

    @Autowired
    private WxMsgService wxMsgService;

    /**
     * 用户扫描二维码后，微信会把扫描结果推送到开发者服务器，开发者服务器需要解析这个xml消息，然后返回一个xml响应给微信服务器
     * @param wxMpXmlMessage
     * @param map
     * @param wxMpService
     * @param wxSessionManager
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {

        // 扫码事件处理
        return wxMsgService.scan(wxMpService, wxMpXmlMessage);
    }

}
