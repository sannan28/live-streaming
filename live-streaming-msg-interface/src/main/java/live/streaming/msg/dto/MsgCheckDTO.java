package live.streaming.msg.dto;


import java.io.Serializable;

public class MsgCheckDTO implements Serializable {

    private boolean checkStatus;
    private String desc;


    public MsgCheckDTO(boolean checkStatus, String desc) {
        this.checkStatus = checkStatus;
        this.desc = desc;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MsgCheckDTO{" +
                "checkStatus=" + checkStatus +
                ", desc='" + desc + '\'' +
                '}';
    }
}
