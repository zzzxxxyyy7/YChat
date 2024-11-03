package com.ychat.common.websocket.domain.vo.resp;

import com.ychat.common.websocket.domain.enums.WSRespTypeEnum;
import lombok.Data;

@Data
public class WSBaseResp<T> {

    /**
     * @see WSRespTypeEnum
     */
    private Integer type;

    private T data;
}
