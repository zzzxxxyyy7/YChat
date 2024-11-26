package com.ychat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 切面方法记录类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureInvokeDTO {

    // 类名称
    private String className;

    // 执行方法名称
    private String methodName;

    // 参数类型
    private String parameterTypes;

    // 入参列表
    private String args;

}
