package com.ychat.common.Websocket.Domain.Vo.Req;

import com.ychat.common.Websocket.Domain.Enums.WSReqTypeEnum;
import lombok.Data;

@Data
public class WSBaseReq {

    /**
     * @see WSReqTypeEnum
     */
    private Integer type;

    private String token;
}
