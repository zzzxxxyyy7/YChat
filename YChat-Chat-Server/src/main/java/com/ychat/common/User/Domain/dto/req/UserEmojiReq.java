package com.ychat.common.User.Domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Description: 添加表情包请求类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiReq {

    /**
     * 表情地址
     */
    @ApiModelProperty(value = "新增的表情url")
    private String expressionUrl;

}
