package com.ychat.common.Chat.domain.vo;

import com.ychat.common.Chat.Enum.GroupRoleAPPEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResp {

    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("群名称")
    private String groupName;

    @ApiModelProperty("群头像")
    private String avatar;

    @ApiModelProperty("在线人数")
    private Long onlineNum;//在线人数

    /**
     * @see GroupRoleAPPEnum
     */
    @ApiModelProperty("成员角色 1群主 2管理员 3普通成员 4已被踢出群聊")
    private Integer role;

}