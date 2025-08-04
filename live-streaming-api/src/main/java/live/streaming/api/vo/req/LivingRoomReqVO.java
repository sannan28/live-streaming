package live.streaming.api.vo.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LivingRoomReqVO {

    private Integer type;

    private int page;

    private int pageSize;

}
