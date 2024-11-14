package com.ychat.common.websocket.service.Impl;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ychat.common.Enums.RoleEnum;
import com.ychat.common.user.Event.UserOnlineEvent;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IRoleService;
import com.ychat.common.user.service.LoginService;
import com.ychat.common.websocket.NettyUtils;
import com.ychat.common.websocket.config.SafeSnowflake;
import com.ychat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.ychat.common.websocket.service.WebSocketService;
import com.ychat.common.websocket.service.adapter.webSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 专门管理 websocket 逻辑，包括推拉流、连接建立等等
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    public SafeSnowflake safeSnowflake;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApplicationEventPublisher appEventPublisher;

    @Autowired
    private IRoleService roleService;

    // 缓存五分钟
    private static final Duration EXPIRE_TIME = Duration.ofMinutes(5);
    private static final Long MAX_MUM_SIZE = 10000L;

    /**
     * 管理所有客户端连接，包括登录态、游客态
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 待登录映射、客户端发起扫码请求申请二维码时生成 临时场景值和 Channel 的唯一关系
     * 登录成功后，通过该关系，将临时场景值对应的 Channel 关联到登录态的 Channel
     * 定期清理该关系，避免内存泄露
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();

    @Override
    public void saveChannel(Channel ctx) {
        // 记录连接
        ONLINE_WS_MAP.put(ctx, new WSChannelExtraDTO());
        log.info("新连接建立，channel: {}", ctx);
    }

    @Override
    public void handleLoginReq(Channel ctx) throws WxErrorException {
        // 雪花算法生成临时场景值
        Integer code = generateLoginCode();
        // 缓存待登录关系
        WAIT_LOGIN_MAP.asMap().put(code, ctx);
        // 获取二维码，有效期五分钟
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        sendUrlMsg(ctx, webSocketAdapter.getRespLoginUrl(wxMpQrCodeTicket));
    }

    private void sendUrlMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode() {
        return safeSnowflake.nextId();
    }

    /**
     * 用户下线，移除缓存
     * @param ctx
     */
    @Override
    public void offline(Channel ctx) {
        ONLINE_WS_MAP.remove(ctx);
    }

    @Override
    public void scanLoginSuccess(Integer loginCode, Long uid) {
        // 确认连接是否在这个服务节点上
        Channel ctx = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (null == ctx) return;

        WAIT_LOGIN_MAP.invalidate(loginCode);
        User user = userDao.getById(uid);

        String token = loginService.login(uid);
        putLoginSuccessMessage(ctx, user, token);
    }

    /**
     * 给本地 channel 发送消息
     *
     * @param channel
     * @param wsBaseResp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    // 传递前端用户扫码成功，请求授权的消息
    @Override
    public void waitAuthorize(Integer loginCode) {
        // 确认连接是否在这个服务节点上
        Channel ctx = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (null == ctx) return;
        sendMsg(ctx, webSocketAdapter.getRespLoginSuccess());
    }

    /**
     * 校验这次 webSocket 握手连接是否携带有效 token
     * @param ctx
     * @param token
     */
    @Override
    public void authorize(Channel ctx, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // token 有效
            User user = userDao.getById(validUid);
            // 发送 token 有效，重新建立登录状态的消息
            putLoginSuccessMessage(ctx, user, token);
        } else {
            // token 失效
            sendMsg(ctx, webSocketAdapter.getRespLoginFail());
        }
    }

    public void putLoginSuccessMessage(Channel ctx, User user, String message) {
        // 保存 channel 的 uid
        /**
         * 当连接建立时，自动存入 channel -> null 的关系
         * @see NettyWebSocketServerHandler.channelActive
         */
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(ctx);
        // 更新 channel -> null --> channel -> uid 的关系, 保存用户登录成功的状态
        wsChannelExtraDTO.setUid(user.getId());
        // 往 Channel 写入用户上线消息
        sendMsg(ctx, webSocketAdapter.getRespLoginSuccess(user , message, roleService.hasRole(user.getId(), RoleEnum.CHAT_MANAGER)));
        // 发送用户上线成功事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtils.getAttr(ctx, NettyUtils.USER_IP));
        appEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

}
