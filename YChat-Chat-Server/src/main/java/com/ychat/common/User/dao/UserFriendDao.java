package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.User.Domain.entity.UserFriend;
import com.ychat.common.User.Mapper.UserFriendMapper;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Utils.Request.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-17
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    /**
     * 根据好友列表查询好友信息
     * 拿到当前用户 uid 的 friendUid 判断是否在给定的 uidList 列表中，如果有，就代表是好友
     * @param uid
     * @param uidList
     * @return
     */
    public List<UserFriend> checkIsMyFriends(Long uid, List<Long> uidList) {
        return lambdaQuery().eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();
    }


    /**
     * 判断跟这个人是否有好友关系，上面是列表，下面是单个
     * @param uid
     * @param targetUid
     * @return
     */
    public UserFriend checkIsMyFriend(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .one();
    }

    public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq,
                wrapper -> wrapper.eq(UserFriend::getUid, uid), UserFriend::getId);
    }

    /**
     * 拿到我和这个好友的id
     * @param uid
     * @param friendUid
     * @return
     */
    public List<UserFriend> getUserFriend(Long uid, Long friendUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, friendUid)
                .or()
                .eq(UserFriend::getFriendUid, uid)
                .eq(UserFriend::getUid, friendUid)
                .select(UserFriend::getId)
                .list();
    }

}
