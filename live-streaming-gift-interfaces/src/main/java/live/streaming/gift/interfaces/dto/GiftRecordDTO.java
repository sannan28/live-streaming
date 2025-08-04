package live.streaming.gift.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class GiftRecordDTO implements Serializable {

    private static final long serialVersionUID = 6203075885741716420L;

    private Long id;

    private Long userId;

    private Long objectId;

    private Integer source;

    private Integer price;

    private Integer priceUnit;

    private Integer giftId;

    private Date sendTime;
}
