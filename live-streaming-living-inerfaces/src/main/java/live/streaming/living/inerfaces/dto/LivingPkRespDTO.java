package live.streaming.living.inerfaces.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class LivingPkRespDTO implements Serializable {


    private static final long serialVersionUID = -623271052312880775L;

    private boolean onlineStatus;

    private String msg;

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
