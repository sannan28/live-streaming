package live.streaming.living.inerfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

// 直播间相关请求DTO
@Data
@ToString
public class LivingRoomRespDTO implements Serializable {


    private static final long serialVersionUID = -1345771744945581082L;

    private Integer id;
    private Long anchorId;
    private String roomName;
    private String covertImg;
    private Integer type;
    private Integer watchNum;
    private Integer goodNum;
    private Long pkObjId;
}
