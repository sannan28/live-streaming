package live.streaming.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import live.streaming.bank.provider.dao.mapper.PayTopicMapper;
import live.streaming.bank.provider.dao.po.PayTopicPO;
import live.streaming.bank.provider.service.IPayTopicService;
import live.streaming.interfaces.enums.CommonStatusEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PayTopicServiceImpl implements IPayTopicService {

    @Resource
    private PayTopicMapper payTopicMapper;

    @Override
    public PayTopicPO getByCode(Integer code) {
        LambdaQueryWrapper<PayTopicPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayTopicPO::getBizCode, code);
        queryWrapper.eq(PayTopicPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return payTopicMapper.selectOne(queryWrapper);
    }
}
