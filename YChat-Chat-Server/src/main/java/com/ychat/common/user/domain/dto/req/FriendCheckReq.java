package com.ychat.common.user.domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Description: 判断是否是自己好友DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendCheckReq {

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty("校验好友的uid")
    private List<Long> uidList;

}
