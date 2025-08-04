package live.streaming.gift.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RedPacketReceiveDTO implements Serializable {


    private static final long serialVersionUID = 2172309462959529804L;

    private Integer price;

    private String notifyMsg;
}
