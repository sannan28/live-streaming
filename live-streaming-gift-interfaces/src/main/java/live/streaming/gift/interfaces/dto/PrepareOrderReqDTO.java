package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrepareOrderReqDTO implements Serializable {


    private static final long serialVersionUID = 8331914389303968864L;

    private Long userId;
    private Integer roomId;
}
