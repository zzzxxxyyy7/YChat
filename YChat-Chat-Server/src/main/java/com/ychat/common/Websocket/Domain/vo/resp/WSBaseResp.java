package com.ychat.common.Websocket.Domain.Vo.Resp;

import com.ychat.common.Websocket.Domain.Enums.WSRespTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {

    /**
     * @see WSRespTypeEnum
     */
    private Integer type;

    private T data;

}
