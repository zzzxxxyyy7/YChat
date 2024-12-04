package com.ychat.common.Chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 撤销管理员请求信息
 */
@Data
public class AdminRevokeReq {

    @NotNull
    @ApiModelProperty("会话ID")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 5)
    @ApiModelProperty("需要撤销管理的列表")
    private List<Long> uidList;

}