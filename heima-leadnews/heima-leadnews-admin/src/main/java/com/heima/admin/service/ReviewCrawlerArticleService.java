package com.heima.admin.service;

import com.heima.model.crawler.pojos.ClNews;

public interface ReviewCrawlerArticleService {

    /**
     * 爬虫端发布文章审核
     * @throws Exception
     */
    public void autoReivewArticleByCrawler(ClNews clNews) throws  Exception;

    public void autoReivewArticleByCrawler() throws  Exception;

    public void autoReivewArticleByCrawler(Integer clNewsId) throws  Exception;
}
