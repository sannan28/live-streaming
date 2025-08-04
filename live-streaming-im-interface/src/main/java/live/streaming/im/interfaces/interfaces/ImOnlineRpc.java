package live.streaming.im.interfaces.interfaces;

// 判断用户是否在线rpc
public interface ImOnlineRpc {
    // 判断用户是否在线
    boolean isOnline(long userId, int appId);
}
