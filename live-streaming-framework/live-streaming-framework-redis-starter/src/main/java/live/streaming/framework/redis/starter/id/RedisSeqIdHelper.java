package live.streaming.framework.redis.starter.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

// 使用Redis自定义自增ID生成器
public class RedisSeqIdHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 开始时间戳 2025年1月1号
    public static final long BEGIN_TIMESTAMP = 1735660800L;
    // 序列号的位数
    public static final int COUNT_BITS = 32;

    @Value("${spring.application.name}")
    private String applicationName;


    public long nextId(String keyPrefix) {
        // 1 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2 生成序列号
        // 2.1 获取当前日期，精确到天，每天一个key，方便统计
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 2.2 自增长
        long count = stringRedisTemplate.opsForValue().increment(applicationName + ":icr:" + keyPrefix + ":" + date);

        // 拼接并返回
        return timestamp << COUNT_BITS | count;
    }


}
