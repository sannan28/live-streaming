package live.streaming.im.provider.service;


public interface ImOnlineService {

    // 判断用户是否在线
    boolean isOnline(long userId, int appId);
}
