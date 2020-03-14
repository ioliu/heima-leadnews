package com.heima.images.service;

public interface CacheImageService {

    /**
     * 缓存图片到redis
     * @param imgUrl
     * @param isCache
     * @return
     */
    public byte[] cache2Redis(String imgUrl,boolean isCache);

    /**
     * 延长图片缓存
     * @param imageKey
     */
    void resetCache2Redis(String imageKey);
}
