package com.ychat.common.websocket.service.adapter;

import com.ychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.ychat.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class webSocketAdapter {

    /**
     * 获取登录二维码
     * @param wxMpQrCodeTicket
     * @return
     */
    public static WSBaseResp<?> getRespLoginUrl(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        return resp;
    }
}
