package com.ychat.common;

import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class WXTest {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private UserDao userDao;


    @Value("${ychat.mysql.ip}")
    private String ip;

    @Test
    public void te() {
        System.out.println(ip);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

    @Test
    public void test2() {
        User user = userDao.getByOpenId("4");
        System.out.println(user);
    }


}
