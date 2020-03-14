package com.heima.crawler.manager;

import com.heima.crawler.config.CrawlerConfig;
import com.heima.crawler.process.ProcessFlow;
import com.heima.crawler.process.entity.CrawlerComponent;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
@Log4j2
public class ProcessingFlowManager {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Resource
    private List<ProcessFlow> processFlowList;


    /**
     * spring启动的时候初始化方法
     * 通过子类的优先级进行排序
     * 初始化spider
     */
    @PostConstruct
    public void initProcessingFlow(){
        if(null != processFlowList && !processFlowList.isEmpty()){
            processFlowList.sort(new Comparator<ProcessFlow>() {
                @Override
                public int compare(ProcessFlow o1, ProcessFlow o2) {
                    if(o1.getPriority() > o2.getPriority()){
                        return 1;
                    }else if(o1.getPriority() < o2.getPriority()){
                        return -1;
                    }
                    return 0;
                }
            });
        }
        Spider spider = configSpider();
        crawlerConfig.setSpider(spider);
    }

    private Spider configSpider() {
        Spider spider = initSpider();
        spider.thread(5);
        return spider;
    }

    /**
     * 根据ProcessFlow接口的getComponentType接口类型生成spider对象
     * @return
     */
    private Spider initSpider() {
        Spider spider = null;
        CrawlerComponent component = getComponent(processFlowList);
        if(null != component){
            PageProcessor pageProcessor = component.getPageProcessor();
            if(pageProcessor!=null){
                spider=Spider.create(pageProcessor);
            }
            if(null!=spider && null != component.getScheduler()){
                spider.setScheduler(component.getScheduler());
            }
            if(null != spider && null != component.getDownloader()){
                spider.setDownloader(component.getDownloader());
            }
            List<Pipeline> pipelineList = component.getPipelineList();
            if(null != spider && null != pipelineList && !pipelineList.isEmpty()){
                for (Pipeline pipeline : pipelineList) {
                    spider.addPipeline(pipeline);
                }
            }
        }
        return spider;
    }

    /**
     * 抓取组件的封装
     * @param processFlowList
     * @return
     */
    private CrawlerComponent getComponent(List<ProcessFlow> processFlowList) {
        CrawlerComponent component = new CrawlerComponent();
        for (ProcessFlow processFlow : processFlowList) {
            if(processFlow.getComponentType() == CrawlerEnum.ComponentType.PAGEPROCESSOR){
                component.setPageProcessor((PageProcessor) processFlow);
            }else if(processFlow.getComponentType() == CrawlerEnum.ComponentType.PIPELINE){
                component.addPipeline((Pipeline) processFlow);
            }else if(processFlow.getComponentType() == CrawlerEnum.ComponentType.DOWNLOAD){
                component.setDownloader((Downloader) processFlow);
            }else if(processFlow.getComponentType() == CrawlerEnum.ComponentType.SCHEDULER){
                component.setScheduler((Scheduler) processFlow);
            }
        }
        return component;
    }



    /**
     * 开始处理爬虫任务
     * @param parseItemList
     * @param handelType
     */
    public void startTask(List<ParseItem> parseItemList,CrawlerEnum.HandelType handelType){
        ProcessFlowData processFlowData = new ProcessFlowData();
        processFlowData.setHandelType(handelType);
        processFlowData.setParseItemList(parseItemList);
        for (ProcessFlow processFlow : processFlowList) {
            processFlow.handel(processFlowData);
        }
        crawlerConfig.getSpider().start();
    }

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    /**
     * 逆向处理
     */
    public void reverseHandel(){
        List<ParseItem> parseItemList = crawlerNewsAdditionalService.queryIncrementParseItem(new Date());
        handelReverseData(parseItemList);
        log.info("开始进行数据逆向更新，增量数据数量为：{}",parseItemList.size());
        if(null != parseItemList && !parseItemList.isEmpty()){
            startTask(parseItemList,CrawlerEnum.HandelType.REVERSE);
        }else{
            log.info("增量数据为空不能进行数据更新");
        }
    }

    /**
     * 逆向同步数据
     * @param parseItemList
     */
    private void handelReverseData(List<ParseItem> parseItemList) {
        if(null != parseItemList && !parseItemList.isEmpty()){
            for (ParseItem parseItem : parseItemList) {
                parseItem.setDocumentType(CrawlerEnum.DocumentType.PAGE.name());
                parseItem.setHandelType(CrawlerEnum.HandelType.REVERSE.name());
            }
        }
    }

    /**
     * 正向爬取
     */
    public void handel(){
        startTask(null,CrawlerEnum.HandelType.FORWARD);
    }
}
