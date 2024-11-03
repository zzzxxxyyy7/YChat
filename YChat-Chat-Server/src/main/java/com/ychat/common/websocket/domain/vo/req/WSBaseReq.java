package com.ychat.common.websocket.domain.vo.req;

import com.ychat.common.websocket.domain.enums.WSReqTypeEnum;
import lombok.Data;

@Data
public class WSBaseReq {

    /**
     * @see WSReqTypeEnum
     */
    private Integer type;

    private String data;
}
