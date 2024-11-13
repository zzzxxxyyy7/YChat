package com.ychat.common.user.service.Impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ychat.common.front.Response.ApiResult;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.IpDetail;
import com.ychat.common.user.domain.entity.IpInfo;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService {

    @Autowired
    private UserDao userDao;

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false));

    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (null == ipInfo) return ;
            String ip = ipInfo.needRefreshIp();
            if (StringUtils.isBlank(ip)) return;
            IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes(ip);
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip, uid);
            }
        });
    }

    private IpDetail TryGetIpDetailOrNullTreeTimes(String ip) {
        // 频控、尝试三次获取Ip
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                // 休眠俩秒再尝试获取
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("IpServiceImpl.TryGetIpDetailOrNullTreeTimes error", e);
            }
        }
        return null;
    }

    private IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            ApiResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<ApiResult<IpDetail>>() {
            }, false);
            return result.getData();
        } catch (Exception ignored) {}
        return null;
    }
}
