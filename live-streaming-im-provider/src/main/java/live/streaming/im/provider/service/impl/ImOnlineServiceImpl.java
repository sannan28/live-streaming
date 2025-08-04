package live.streaming.im.provider.service.impl;

import live.streaming.im.core.server.interfaces.constants.ImCoreServerConstants;
import live.streaming.im.provider.service.ImOnlineService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public boolean isOnline(long userId, int appId) {

        return redisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
    }
}
