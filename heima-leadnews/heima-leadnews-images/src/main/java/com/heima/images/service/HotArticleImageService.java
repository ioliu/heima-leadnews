package com.heima.images.service;

import com.heima.common.kafka.messages.app.ApHotArticleMessage;

public interface HotArticleImageService {

    /**
     * 处理热点文章图片
     * @param message
     */
    public void handleHotImage(ApHotArticleMessage message);
}
