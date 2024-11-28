package com.ychat.common.TestApi.Controller.LocalMessageTest;

import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Domain.vo.IdRespVO;
import com.ychat.service.MQProducer;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/localMessage")
public class LocalMessageTestController {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MQProducer mqProducer;

    @PostMapping("/secureInvoke")
    @ApiOperation("本地消息表")
    @Transactional
    public ApiResult<IdRespVO> secureInvoke(String msg) {
        Message build = Message.builder()
                .fromUid(11000L)
                .type(1)
                .content(msg)
                .roomId(1L)
                .status(0)
                .build();

        messageDao.save(build);
        mqProducer.sendSecureMsg("test-topic" , msg , msg);
        return ApiResult.success();
    }

}
