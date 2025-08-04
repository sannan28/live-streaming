package live.streaming.im.core.server.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ImOnlineDTO implements Serializable {

    private static final long serialVersionUID = 3106988129341349443L;

    private Long userId;

    private Integer appId;

    private Integer roomId;

    private Long loginTime;

}
