package com.heima.crawler.process.scheduler;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.ProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.enums.CrawlerEnum;
import com.mongodb.DB;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Log4j2
public class DbAndRedisScheduler extends RedisScheduler implements ProcessFlow {

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    @Autowired
    private CrawlerHelper crawlerHelper;

    public DbAndRedisScheduler(String host){
        super(host);
    }

    public DbAndRedisScheduler(JedisPool pool){
        super(pool);
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        String handelType = crawlerHelper.getHandelType(request);
        boolean isExist=false;
        //只有正向爬取的时候才会排重
        if(CrawlerEnum.HandelType.FORWARD.name().equals(handelType)){
            log.info("url排重开始，url:{},handelType:{}",request.getUrl(),handelType);
            //redis排重
            isExist = super.isDuplicate(request,task);
            //数据库进行排重
            if(!isExist){
                isExist = crawlerNewsAdditionalService.isExistUrl(request.getUrl());
            }
            log.info("url排重结束，url:{},handelType:{},isExist:{}",request.getUrl(),handelType,isExist);
        }else{
            log.info("反向爬取，不进行url排重");
        }
        return isExist;
    }

    @Override
    public void handel(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.SCHEDULER;
    }

    @Override
    public int getPriority() {
        return 123;
    }
}
