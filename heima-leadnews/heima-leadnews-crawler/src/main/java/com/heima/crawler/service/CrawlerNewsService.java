package com.heima.crawler.service;

import com.heima.model.crawler.pojos.ClNews;

import java.util.List;

public interface CrawlerNewsService {

    /**
     * 保存
     * @param clNews
     */
    public void saveNews(ClNews clNews);

    /**
     * 更新
     * @param clNews
     */
    public void updateNews(ClNews clNews);

    /**
     * 删除
     * @param url
     */
    public void deleteByUrl(String url);

    /**
     * 查询
     * @param clNews
     * @return
     */
    public List<ClNews> queryList(ClNews clNews);
}
