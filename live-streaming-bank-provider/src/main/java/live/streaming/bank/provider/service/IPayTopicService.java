package live.streaming.bank.provider.service;


import live.streaming.bank.provider.dao.po.PayTopicPO;

public interface IPayTopicService {

    PayTopicPO getByCode(Integer code);

}
