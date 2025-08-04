package org.live.streaming.provider.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import live.streaming.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.live.streaming.interfaces.constants.UserTagFieldNameConstants;
import org.live.streaming.interfaces.constants.UserTagsEnum;
import org.live.streaming.interfaces.utils.TagInfoUtils;
import org.live.streaming.provider.dao.po.UserTagPo;
import org.live.streaming.provider.mapper.IUserTagMapper;
import org.live.streaming.provider.service.IUserTagService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;


    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean updateStatus = baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if (updateStatus) { // 为true说明是有记录且是第一次设置（我们的sql语句是当前没有设置该tag才进行设置，即第一次设置）
            return true;
        }
        // 没成功：说明是没此行记录，或者重复设置
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo != null) { // 重复设置，直接返回false
            return false;
        }
        // 无记录，插入
        // 使用Redis的setnx命令构建分布式锁（目前有很多缺陷）
        String lockKey = userProviderCacheKeyBuilder.buildTagLockKey(userId);
        try {
            Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "-1", Duration.ofSeconds(3L));
            if (BooleanUtil.isFalse(isLock)) { // 说明有其他线程正在进行插入
                return false;
            }
            userTagPo = new UserTagPo();
            userTagPo.setUserId(userId);
            baseMapper.insert(userTagPo);
        } finally {
            redisTemplate.delete(lockKey);
        }
        System.out.println("设置标签册成功！");
        // 插入后再修改返回
        return baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }



    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return baseMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }

    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if (userTagPo == null) {
            return false;
        }
        String fieldName = userTagsEnum.getFieldName();
        if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_01)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo01(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_02)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo02(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_03)) {
            return TagInfoUtils.isContain(userTagPo.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;
    }

}
