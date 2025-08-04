package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShopCarItemRespDTO implements Serializable {


    private static final long serialVersionUID = -7791496926208813181L;

    private Integer count;

    private SkuInfoDTO skuInfoDTO;
}
