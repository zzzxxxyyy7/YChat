package com.ychat.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.domain.entity.SecureInvokeRecord;
import com.ychat.mapper.SecureInvokeRecordMapper;
import com.ychat.service.SecureInvokeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 本地消息表 服务实现类
 */
@Service
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

    public List<SecureInvokeRecord> getWaitRetryRecords() {
        Date now = new Date();
        // 只查 30 秒前的失败数据, 避免刚入库的数据被查出来
        DateTime afterTime = DateUtil.offsetSecond(now, - (int) SecureInvokeService.RETRY_SEARCH_INTERVAL_SECONDS);
        return lambdaQuery()
                // 状态为等待
                .eq(SecureInvokeRecord::getStatus, SecureInvokeRecord.STATUS_WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, new Date())
                .lt(SecureInvokeRecord::getCreateTime, afterTime)
                .list();
    }

}
