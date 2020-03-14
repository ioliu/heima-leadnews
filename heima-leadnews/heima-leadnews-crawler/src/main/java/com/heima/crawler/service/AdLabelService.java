package com.heima.crawler.service;

public interface AdLabelService {

    /**
     * @param labels   从页面爬取的标签  多个的时候，以逗号分隔
     * @return  标签id  多个以逗号分隔
     */
    public String getLabelIds(String labels);

    /**
     *
     * @param labels  标签id  多个以逗号分隔
     * @return  频道id  找不到频道，默认给0  0是未分类
     */
    public Integer getAdChannelByLabelIds(String labels);
}
