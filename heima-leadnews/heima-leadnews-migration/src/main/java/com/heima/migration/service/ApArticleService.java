package com.heima.migration.service;

import com.heima.model.article.pojos.ApArticle;

import java.util.List;

public interface ApArticleService  {

    public ApArticle getById(Long id);


    /**
     * 查询未同步的数据
     * @return
     */
    public List<ApArticle> getUnSyncArticleList();

    /**
     * 更新同步状态
     * @param apArticle
     */
    public void updateSyncStatus(ApArticle apArticle);
}
