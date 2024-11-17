package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.user.domain.entity.Room;
import com.ychat.common.user.mapper.RoomMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-11-17
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

}
