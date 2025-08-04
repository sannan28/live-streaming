package live.streaming.im.interfaces.enums;

public enum AppIdEnum {

    LIVE_BIZ(10001, "直播业务");

    int code;

    String desc;

    AppIdEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
