package live.streaming.gift.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCarReqDTO implements Serializable {


    private static final long serialVersionUID = 8861018768398651350L;

    private Long userId;

    private Long skuId;

    private Integer roomId;
}
