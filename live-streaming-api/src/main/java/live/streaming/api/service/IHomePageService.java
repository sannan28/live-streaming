package live.streaming.api.service;

import live.streaming.api.vo.HomePageVO;

public interface IHomePageService {

    // 初始化页面获取的信息
    HomePageVO initPage(Long userId);
}
