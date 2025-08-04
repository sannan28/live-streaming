package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedPacketConfigRespDTO implements Serializable {


    private static final long serialVersionUID = 2468868660521334819L;

    private Long anchorId;

    private Integer totalPrice;

    private Integer totalCount;

    private String configCode;

    private String remark;
}
