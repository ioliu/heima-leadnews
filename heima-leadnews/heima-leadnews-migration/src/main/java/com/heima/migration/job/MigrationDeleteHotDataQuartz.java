package com.heima.migration.job;

import com.heima.common.quartz.AbstractJob;
import com.heima.migration.service.ApHotArticleService;
import com.heima.model.article.pojos.ApHotArticles;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@DisallowConcurrentExecution
public class MigrationDeleteHotDataQuartz extends AbstractJob {
    @Override
    public String[] triggerCron() {
        return new String[]{"0 30 22 * * ?"};
    }

    @Autowired
    private ApHotArticleService apHotArticleService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始删除数据库过期数据");
        deleteExpireHotData();
        log.info("删除数据库过期数据结束，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
    }

    /**
     * 删除过期数据
     */
    private void deleteExpireHotData() {
        List<ApHotArticles> hotArticlesList = apHotArticleService.selectExpireMonth();
        if(null != hotArticlesList && !hotArticlesList.isEmpty()){
            for (ApHotArticles apHotArticles : hotArticlesList) {
                apHotArticleService.deleteHotData(apHotArticles);
            }
        }
    }
}
