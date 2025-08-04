package live.streaming.api.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class LivingRoomPageRespVO {

    private List<LivingRoomRespVO> list;

    private boolean hasNext;
}
