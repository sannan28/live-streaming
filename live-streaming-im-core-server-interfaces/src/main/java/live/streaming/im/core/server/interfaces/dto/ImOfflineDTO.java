package live.streaming.im.core.server.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ImOfflineDTO implements Serializable {

    private static final long serialVersionUID = 5851822157081659742L;

    private Long userId;

    private Integer appId;

    private Integer roomId;

    private Long loginTime;
}
