package com.ychat.common.Chat.Enum;

import com.ychat.common.Constants.Enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 群聊会话操作状态码，异常码
 */
@AllArgsConstructor
@Getter
public enum GroupErrorEnum implements ErrorEnum {

    GROUP_NOT_EXIST(9001, "该群不存在"),
    NOT_ALLOWED_OPERATION(9002, "您无权操作"),
    MANAGE_COUNT_EXCEED(9003, "群管理员数量达到上限，请先删除后再操作"),
    USER_NOT_IN_GROUP(9004, "您不在该群聊中"),
    NOT_ALLOWED_FOR_REMOVE(9005, "非法操作，您没有移除该成员的权限"),
    NOT_ALLOWED_FOR_EXIT_GROUP(9006, "该群聊不允许退出"),
    NOT_Manager(9007, "该用户不是管理员"),
    ;

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }

}
