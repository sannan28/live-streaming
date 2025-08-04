package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedPacketConfigReqDTO implements Serializable {


    private static final long serialVersionUID = 4490673059338955147L;

    private Integer id;

    private Integer roomId;

    private Integer status;

    private Long userId;

    private String redPacketConfigCode;

    private Integer totalPrice;

    private Integer totalCount;

    private String remark;
}
