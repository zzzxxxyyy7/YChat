package com.ychat.common.User.Services.Impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.IpDetail;
import com.ychat.common.User.Domain.entity.IpInfo;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.IpService;
import com.ychat.common.User.Services.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService , DisposableBean {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false));

    /**
     * 优雅停机，线程回调，销毁线程池
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        EXECUTOR.shutdown();
        if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }

    /**
     * 异步更新用户 Ip Detail
     * @param uid
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() -> {
            log.info("开始执行 IpDetail 刷新工作");
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
                userCache.userInfoChange(uid);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip, uid);
            }
        });
    }

    private static IpDetail TryGetIpDetailOrNullTreeTimes(String ip) {
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

    private static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            ApiResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<ApiResult<IpDetail>>() {
            }, false);
            return result.getData();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 吞吐量测试
     * 第100次成功，且耗时：176534
     * @param args
     */
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 1 ; i <= 100 ; ++i) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes("118.85.133.4");
                if (Objects.nonNull(ipDetail)) {
                    Date end = new Date();
                    System.out.printf("第%d次成功，且耗时：%s%n", finalI, end.getTime() - begin.getTime());
                    System.out.println(ipDetail);
                }
            });
        }
    }

}
