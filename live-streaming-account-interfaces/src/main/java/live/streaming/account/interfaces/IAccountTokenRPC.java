package live.streaming.account.interfaces;


public interface IAccountTokenRPC {

    // 创建一个登录token
    String createAndSaveLoginToken(Long userId);

    // 校验用户token
    Long getUserIdByToken(String tokenKey);
}
