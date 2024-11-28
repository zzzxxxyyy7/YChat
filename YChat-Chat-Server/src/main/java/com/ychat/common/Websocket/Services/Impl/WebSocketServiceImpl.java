package com.ychat.common.Websocket.Services.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ychat.common.Constants.Enums.Impl.RoleEnum;
import com.ychat.common.Config.ThreadPool.ThreadPoolConfig;
import com.ychat.common.User.Event.UserOfflineEvent;
import com.ychat.common.User.Event.UserOnlineEvent;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.IRoleService;
import com.ychat.common.User.Services.LoginService;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.Websocket.NettyUtils;
import com.ychat.common.Websocket.Config.SafeSnowflake;
import com.ychat.common.Websocket.Domain.Dto.WSAuthorize;
import com.ychat.common.Websocket.Domain.Dto.WSChannelExtraDTO;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSBaseResp;
import com.ychat.common.Websocket.Services.WebSocketService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserCache userCache;

    /**
     * 引入线程池优化推送群体消息
     */
    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 缓存一分钟
    private static final Duration EXPIRE_TIME = Duration.ofMinutes(5);
    private static final Long MAX_MUM_SIZE = 10000L;

    /**
     * 待登录映射、客户端发起扫码请求申请二维码时生成 临时场景值和 Channel 的唯一关系
     * 登录成功后，通过该关系，将临时场景值对应的 Channel 关联到登录态的 Channel
     * 定期清理该关系，避免内存泄露
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();

    /**
     * 管理所有客户端连接，包括登录态、游客态
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 所有在线的用户和对应的socket
     * 维护 UID 和 UID 对应的所有 WebSocket List
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel ctx) {
        // 雪花算法生成临时场景值
        Integer code = generateLoginCode();
        // 缓存待登录关系
        WAIT_LOGIN_MAP.asMap().put(code, ctx);
        // 获取二维码，有效期五分钟
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        // 请求登录，返回 WX 授权码 URL 地址
        sendUrlMsg(ctx, WebSocketAdapter.getRespLoginUrl(wxMpQrCodeTicket));
    }

    private Integer generateLoginCode() {
        return safeSnowflake.nextId();
    }

    /**
     * 处理所有ws连接的事件
     *
     * @param ctx
     */
    @Override
    public void connect(Channel ctx) {
        ONLINE_WS_MAP.put(ctx, new WSChannelExtraDTO());
        log.info("新连接建立，channel: {}", ctx);
    }

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    @Override
    public void removed(Channel channel) {
        // 尝试从在线 MAP 中拿到对应的 user 信息
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) { // 已登录用户断连, 并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            // 发送用户离线的信息
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    /**
     * 校验这次 webSocket 握手连接是否携带有效 token
     * @param ctx
     * @param wsAuthorize
     */
    @Override
    public void authorize(Channel ctx,  WSAuthorize wsAuthorize) {
        // 校验token
        Long validUid = loginService.getValidUid(wsAuthorize.getToken());
        if (Objects.nonNull(validUid)) {
            // token 有效
            User user = userDao.getById(validUid);
            // 发送 token 有效，重新建立登录状态的消息
            putLoginSuccessMessage(ctx, user, wsAuthorize.getToken());
        } else {
            // token 失效
            sendMsg(ctx, WebSocketAdapter.getRespLoginFail());
        }
    }

    /**
     * 登录成功后，更新相关状态并推送成功消息
     * @param ctx
     * @param user
     * @param message
     */
    public void putLoginSuccessMessage(Channel ctx, User user, String message) {
        // 更新上线列表
        online(ctx, user.getId());
        // 往 Channel 写入用户上线消息
        boolean hasRole = roleService.hasRole(user.getId(), RoleEnum.CHAT_MANAGER);
        sendMsg(ctx, WebSocketAdapter.getRespLoginSuccess(user , message, hasRole));
        // 发送用户上线成功事件
        boolean online = userCache.isOnline(user.getId());
        if (!online) { // 多条 Channel 只会发送一次用户上线的消息
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtils.getAttr(ctx, NettyUtils.USER_IP));
            appEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        // 更新 channel -> null --> channel -> uid 的关系, 保存用户登录成功的状态
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        // 把当前这个 Channel 加入到 Uid 对应的 List
        ONLINE_UID_MAP.get(uid).add(channel);
        // 往这条 Channel 绑定 Uid 属性
        NettyUtils.setAttr(channel, NettyUtils.USER_UID, uid);
    }

    /**
     * 用户下线，移除缓存
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            // 拿到这个 Uid 对应的所有 Channel 连接
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
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

    // 传递前端用户扫码成功，请求授权的消息
    @Override
    public void waitAuthorize(Integer loginCode) {
        // 确认连接是否在这个服务节点上
        Channel ctx = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (null == ctx) return;
        sendMsg(ctx, WebSocketAdapter.getRespLoginSuccess());
    }

    /**
     * 如果在线列表不存在，就先把该channel放进在线列表 --> 防御性编程
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        /**
         * 当连接建立时，自动存入 channel -> null 的关系
         * @see NettyWebSocketServerHandler.channelActive
         */
        // 查看用户是否在线
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /**
     * 发送消息给所有用户，单机才可以这么做，集群需要加一层路由服务
     * @param msg
     */
    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        // 线程池批量推送优化
        ONLINE_WS_MAP.forEach((ctx, ext) -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(ctx, msg));
        });
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    private void sendUrlMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
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

}
