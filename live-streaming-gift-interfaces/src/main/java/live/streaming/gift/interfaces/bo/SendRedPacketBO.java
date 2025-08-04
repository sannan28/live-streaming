package live.streaming.gift.interfaces.bo;

import live.streaming.gift.interfaces.dto.RedPacketConfigReqDTO;
import lombok.Data;

import java.io.Serializable;

// 用户红包雨抢红包后发送的mq消息体
@Data
public class SendRedPacketBO implements Serializable {


    private static final long serialVersionUID = -3158567005236705963L;
    
    private Integer price;

    private RedPacketConfigReqDTO reqDTO;
}
