package com.ychat.common.user.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 修改用户名
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoVo {

    @ApiModelProperty(value = "徽章id")
    private Long itemId;

    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;

    @ApiModelProperty("徽章图像")
    private String img;

    @ApiModelProperty("徽章说明")
    private String describe;

    public static ItemInfoVo skip(Long itemId) {
        ItemInfoVo dto = new ItemInfoVo();
        dto.setItemId(itemId);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }
}
