package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.Contact;
import com.ychat.common.user.mapper.ContactMapper;
import com.ychat.common.user.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会话列表
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 更新所有人的会话时间，没有就直接插入
     */
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }

}
