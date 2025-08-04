package live.streaming.account.provider.service.impl;

import live.streaming.account.provider.service.IAccountTokenService;
import live.streaming.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountTokenServiceImpl implements IAccountTokenService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;


    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), String.valueOf(userId), 30, TimeUnit.DAYS);
        return token;
    }

    public Long getUserIdByToken(String tokenKey) {
        String redisKey = cacheKeyBuilder.buildUserLoginTokenKey(tokenKey);

        String userIdStr = stringRedisTemplate.opsForValue().get(redisKey);

        return userIdStr == null ? null : Long.valueOf(userIdStr);
    }
}
