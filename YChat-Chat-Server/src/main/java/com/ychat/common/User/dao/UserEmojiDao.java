package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.User.Domain.entity.UserEmoji;
import com.ychat.common.User.Mapper.UserEmojiMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 用户表情包 服务实现类
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {

    /**
     * 获取用户的表情包列表
     * @param uid
     * @return
     */
    public List<UserEmoji> listByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).list();
    }

    /**
     * 获取用户拥有表情包的数量
     * @param uid
     * @return
     */
    public int countByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).count();
    }

}
