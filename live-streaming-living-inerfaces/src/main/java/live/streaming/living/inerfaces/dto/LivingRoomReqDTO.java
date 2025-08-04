package live.streaming.living.inerfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

// 直播间相关请求DTO
@Data
@ToString
public class LivingRoomReqDTO implements Serializable {


    private static final long serialVersionUID = -5609215193964499482L;

    private Integer id;
    private Long anchorId;
    private Long pkObjId;
    private String roomName;
    private Integer roomId;
    private String covertImg;
    private Integer type;
    private Integer appId;
    private int page;
    private int pageSize;
}
