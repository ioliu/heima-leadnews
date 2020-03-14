package com.heima.migration.service;

import com.heima.model.article.pojos.ApHotArticles;

import java.util.List;

public interface ApHotArticleService {

    public List<ApHotArticles> selectList(ApHotArticles apHotArticles);

    void insert(ApHotArticles apHotArticles);

    /**
     * 热数据 hbase 同步
     * @param articleId
     */
    public void hotApArticleSync(Integer articleId);

    public void deleteById(Integer id);

    /**
     * 查询过期的数据
     * @return
     */
    public List<ApHotArticles> selectExpireMonth();

    public void deleteHotData(ApHotArticles apHotArticles);


}
