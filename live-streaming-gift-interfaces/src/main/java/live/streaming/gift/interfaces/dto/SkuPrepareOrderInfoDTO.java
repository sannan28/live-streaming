package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuPrepareOrderInfoDTO implements Serializable {


    private static final long serialVersionUID = 7475881542571804606L;

    private Integer totalPrice;

    private List<ShopCarItemRespDTO> skuPrepareOrderItemInfoDTOS;

}
