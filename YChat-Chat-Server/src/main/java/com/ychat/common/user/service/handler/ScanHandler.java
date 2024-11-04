package com.ychat.common.user.service.handler;

import com.ychat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 扫码事件处理器
 */
@Component
public class ScanHandler extends AbstractHandler {

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
        /**
         * 拿到二维码的临时场景值和扫码用户openId
         */
        String eventKey = wxMpXmlMessage.getEventKey();
        String openId = wxMpXmlMessage.getFromUser();

        // 扫码事件处理
        return TextBuilder.build("扫码事件处理，你好", wxMpXmlMessage);
    }

}
