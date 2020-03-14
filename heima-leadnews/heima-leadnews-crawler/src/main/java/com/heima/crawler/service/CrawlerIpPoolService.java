package com.heima.crawler.service;

import com.heima.model.crawler.core.proxy.CrawlerProxy;
import com.heima.model.crawler.pojos.ClIpPool;

import java.util.List;

public interface CrawlerIpPoolService {

    /**
     * 保存
     * @param clIpPool
     */
    public void saveCrawlerIpPool(ClIpPool clIpPool);

    /**
     * 检查ip是否存在
     * @return
     */
    public boolean checkExist(String host,int port);

    /**
     * 更新方法
     * @param clIpPool
     */
    public void updateCrawlerIpPool(ClIpPool clIpPool);

    /**
     * 查询所有数据
     * @param clIpPool
     * @return
     */
    public List<ClIpPool> queryList(ClIpPool clIpPool);

    /**
     * 查询可用的ip列表
     * @param clIpPool
     * @return
     */
    public List<ClIpPool> queryAvailabelList(ClIpPool clIpPool);

    /**
     * 删除
     * @param clIpPool
     */
    public void delete(ClIpPool clIpPool);

    /**
     * 设置某个ip不可用
     * @param proxy
     * @param errorMsg
     */
    public void unAvailabelProxy(CrawlerProxy proxy,String errorMsg);
}
