package com.heima.crawler.job;

import com.heima.common.quartz.AbstractJob;
import com.heima.crawler.proxy.ProxyIpManager;
import com.heima.model.crawler.core.proxy.CrawlerProxyProvider;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@Log4j2
public class ProxyIpUpdateQuartz extends AbstractJob {
    @Override
    public String[] triggerCron() {
        return new String[]{"0 0/1 * * * ?"};
    }

    @Autowired
    private ProxyIpManager proxyIpManager;

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始更新代理ip");
        proxyIpManager.updateProxyIp();
        crawlerProxyProvider.updateProxy();
        log.info("更新代理ip完成，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
    }
}
