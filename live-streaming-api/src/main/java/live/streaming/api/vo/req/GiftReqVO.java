package live.streaming.api.vo.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GiftReqVO {

    private int giftId;

    private Integer roomId;

    private Long senderUserId;

    private Long receiverId;

    private int type;

}
