package com.heima.admin.service;

public interface ReviewMediaArticleService {

    /**
     * 自媒体端发布文章自动审核
     * @param newsId
     */
    public void autoReviewArticleByMedia(Integer newsId);
}
