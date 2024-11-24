package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.SecureInvokeRecord;
import com.ychat.common.user.mapper.SecureInvokeRecordMapper;
import com.ychat.common.user.service.ISecureInvokeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 本地消息表 服务实现类
 */
@Service
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

}
