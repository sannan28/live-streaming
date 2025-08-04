package live.streaming.im.provider.rpc;

import live.streaming.im.interfaces.interfaces.ImTokenRpc;
import live.streaming.im.provider.service.ImTokenService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {

    @Resource
    private ImTokenService imTokenService;

    public String createImLoginToken(long userId, int appId) {
        return imTokenService.createImLoginToken(userId, appId);
    }

    public Long getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
