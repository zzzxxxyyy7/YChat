package com.ychat.common.User.Services.Impl;

import com.ychat.common.Constants.Enums.Impl.ItemEnum;
import com.ychat.common.Constants.Exception.BusinessException;
import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Dao.UserEmojiDao;
import com.ychat.common.User.Domain.dto.req.UserEmojiReq;
import com.ychat.common.User.Domain.entity.UserBackpack;
import com.ychat.common.User.Domain.entity.UserEmoji;
import com.ychat.common.User.Domain.vo.IdRespVO;
import com.ychat.common.User.Domain.vo.UserEmojiResp;
import com.ychat.common.User.Services.IUserEmojiService;
import com.ychat.common.Utils.Assert.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserEmojiServiceImpl implements IUserEmojiService {

    @Autowired
    private UserEmojiDao userEmojiDao;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listByUid(uid).
                stream()
                .map(a -> UserEmojiResp.builder()
                        .id(a.getId())
                        .expressionUrl(a.getExpressionUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ApiResult<IdRespVO> insert(UserEmojiReq emojis, Long uid) {
        RLock lock = redissonClient.getLock("addEmoji:uid:" + uid);
        try {
            // 尝试加锁，设置锁的过期时间为5秒，防止长时间占用
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // 校验表情数量是否超过 30
                int count = userEmojiDao.countByUid(uid);
                AssertUtil.isFalse(count > 30, "最多只能添加30个表情");
                // 校验表情是否存在
                Integer existsCount = userEmojiDao.lambdaQuery()
                        .eq(UserEmoji::getExpressionUrl, emojis.getExpressionUrl())
                        .eq(UserEmoji::getUid, uid)
                        .count();
                AssertUtil.isFalse(existsCount > 0, "已经添加过该表情");
                UserEmoji insert = UserEmoji.builder().uid(uid).expressionUrl(emojis.getExpressionUrl()).build();
                userEmojiDao.save(insert);
                return ApiResult.success(IdRespVO.id(insert.getId()));
            } else {
                throw new BusinessException("添加表情包过于频繁，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断");
        } finally {
            // 确保在操作结束后释放锁
            lock.unlock();
        }
    }

    @Override
    public void remove(Long id, Long uid) {
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "被删除的表情不能为空");
        AssertUtil.equal(userEmoji.getUid(), uid, "小黑子，别人表情不是你能删的");
        userEmojiDao.removeById(id);
    }

}
