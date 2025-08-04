package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopCarRespDTO implements Serializable {


    private static final long serialVersionUID = 5546308503681034976L;

    private Long userId;

    private Integer roomId;

    private List<ShopCarItemRespDTO> skuCarItemRespDTODTOS;
}
