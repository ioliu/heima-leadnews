package com.heima.crawler.job;

import com.heima.common.quartz.AbstractJob;
import com.heima.crawler.manager.ProcessingFlowManager;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@Log4j2
public class CrawlerReverseQuartz extends AbstractJob {
    @Override
    public String[] triggerCron() {
        return new String[]{"0 0 0/1 * * ?"};
    }

    @Autowired
    private ProcessingFlowManager processingFlowManager;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始反向抓取");
//        processingFlowManager.reverseHandel();
        log.info("反向抓取结束,耗时：", System.currentTimeMillis() - cutrrentTime);
    }
}
