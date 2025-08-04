package live.streaming.bank.provider.rpc;

import live.streaming.bank.interfaces.dto.AccountTradeReqDTO;
import live.streaming.bank.interfaces.dto.AccountTradeRespDTO;
import live.streaming.bank.interfaces.dto.LiveCurrencyAccountDTO;
import live.streaming.bank.interfaces.rpc.LiveCurrencyAccountRpc;
import live.streaming.bank.provider.service.ILiveCurrencyAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class LiveCurrencyAccountRpcImpl implements LiveCurrencyAccountRpc {

    @Resource
    private ILiveCurrencyAccountService liveCurrencyAccountService;

    @Override
    public boolean insertOne(Long userId) {
        return liveCurrencyAccountService.insertOne(userId);
    }

    @Override
    public void incr(Long userId, int num) {
        liveCurrencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(Long userId, int num) {
        liveCurrencyAccountService.decr(userId, num);
    }

    @Override
    public LiveCurrencyAccountDTO getByUserId(Long userId) {
        return liveCurrencyAccountService.getByUserId(userId);
    }

    @Override
    public Integer getBalance(Long userId) {
        return liveCurrencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return liveCurrencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        return liveCurrencyAccountService.consume(accountTradeReqDTO);
    }
}
