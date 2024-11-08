package com.ychat.common.websocket.service.adapter;

import com.ychat.common.user.domain.entity.User;
import com.ychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.ychat.common.websocket.domain.vo.resp.WSLoginSuccess;
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

    /**
     * 获取登录成功响应信息
     * @param user
     * @param token
     * @return
     */
    public static WSBaseResp<?> getRespLoginSuccess(User user, String token) {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    public static WSBaseResp<?> getRespLoginSuccess() {
        WSBaseResp<String> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        resp.setData("扫码成功，请授权登录");
        return resp;
    }

    public static WSBaseResp<?> getRespLoginFail() {
        WSBaseResp<String> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        resp.setData("Token 无效或者已过期，请重新登录");
        return resp;
    }
}
