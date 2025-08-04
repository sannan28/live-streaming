package live.streaming.im.provider.rpc;

import live.streaming.im.interfaces.interfaces.ImOnlineRpc;
import live.streaming.im.provider.service.ImOnlineService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {

    @Resource
    private ImOnlineService imOnlineService;

    public boolean isOnline(long userId, int appId) {
        return imOnlineService.isOnline(userId, appId);
    }
}
