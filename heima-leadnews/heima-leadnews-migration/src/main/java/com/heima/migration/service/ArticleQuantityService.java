package com.heima.migration.service;

import com.heima.migration.entity.ArticleQuantity;

import java.util.List;

public interface ArticleQuantityService {

    /**
     * 获取ArticleQuantity列表
     * @return
     */
    public List<ArticleQuantity> getArticleQuantityList();

    /**
     * 根据文章id查询ArticleQuantity
     * @param id
     * @return
     */
    public ArticleQuantity getArticleQuantityByArticleId(Long id);

    /**
     * 根据文章id从hbase中查询ArticleQuantity
     * @param id
     * @return
     */
    public ArticleQuantity getArticleQuantityByArticleidForHbase(Long id);

    /**
     * 数据库同步到hbase
     */
    public void dbToHbase();

    /**
     * 根据文章id将数据库的数据同步到hbase
     * @param articleId
     */
    public void dbToHbase(Integer articleId);
}
