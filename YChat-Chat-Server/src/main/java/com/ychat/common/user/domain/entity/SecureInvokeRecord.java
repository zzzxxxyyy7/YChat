package com.ychat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本地消息表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("secure_invoke_record")
public class SecureInvokeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求快照参数json
     */
    @TableField("secure_invoke_json")
    private String secureInvokeJson;

    /**
     * 状态 1待执行 2已失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 下一次重试的时间
     */
    @TableField("next_retry_time")
    private Date nextRetryTime;

    /**
     * 已经重试的次数
     */
    @TableField("retry_times")
    private Integer retryTimes;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_times")
    private Integer maxRetryTimes;

    /**
     * 执行失败的堆栈
     */
    @TableField("fail_reason")
    private String failReason;

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
