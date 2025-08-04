package live.streaming.bank.interfaces.constants;

public enum PayProductTypeEnum {

    LIVE_COIN(0, "直播间充值-旗鱼虚拟币产品");

    int code;

    String desc;

    PayProductTypeEnum(int code, String desc) {
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
