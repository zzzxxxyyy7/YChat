package com.ychat.common.User.Domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.*;

/**
 * 会话成员表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群主id
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 成员uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 成员角色 1群主 2管理员 3普通成员
     */
    @TableField("role")
    private Integer role;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}
