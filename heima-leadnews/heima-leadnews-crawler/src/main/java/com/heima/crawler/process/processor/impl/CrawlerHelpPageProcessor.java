package com.heima.crawler.process.processor.impl;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.entity.CrawlerConfigProperty;
import com.heima.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class CrawlerHelpPageProcessor extends AbstractCrawlerPageProcessor {

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    @Autowired
    private CrawlerHelper crawlerHelper;

    /**
     * 处理数据
     * @param page
     */
    @Override
    public void handelPage(Page page) {
        //获取类型
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        long currentTime = System.currentTimeMillis();
        String requestUrl = page.getUrl().get();
        log.info("开始解析帮助页，url:{},handelType:{}",requestUrl,handelType);
        //获取配置的抓取规则
        String helpCrawlerXpath = crawlerConfigProperty.getHelpCrawlerXpath();
        Integer crawlerHelpNextPagingSize = crawlerConfigProperty.getCrawlerHelpNextPagingSize();
        List<String> helpUrlList = page.getHtml().xpath(helpCrawlerXpath).links().all();
        if(null!=crawlerHelpNextPagingSize&&crawlerHelpNextPagingSize>1){
            //分页逻辑处理
            List<String> docPageUrlList = getDocPageUrlList(requestUrl,crawlerHelpNextPagingSize);
            if(null!=docPageUrlList && !docPageUrlList.isEmpty()){
                helpUrlList.addAll(docPageUrlList);
            }
        }
        addSpiderRequest(helpUrlList,page.getRequest(),CrawlerEnum.DocumentType.PAGE);
        log.info("解析帮助页数据完成，url:{},handelType:{},耗时:{}",page.getUrl(),handelType,System.currentTimeMillis()-currentTime);

    }

    private final String helpUrlSuffix = "?utm_source=feed";

    private final String helpPageSuffix = "/article/list/";

    /**
     * 获取分页后的数据
     * @param url
     * @param pageSize
     * @return
     */
    private List<String> getDocPageUrlList(String url, Integer pageSize) {
        List<String> docPagePaingUrlList = null;
        if(url.endsWith(helpUrlSuffix)){
            //分页的url
            List<String> pagePagingUrlList = generateHelpPagingUrl(url,pageSize);
            //获取分页数据中的目标url
            docPagePaingUrlList = getHelpPagingDocUrl(pagePagingUrlList);
        }
        return docPagePaingUrlList;
    }

    /**
     * 获取分页后的url(文章的url列表)
     * @param pagePagingUrlList
     * @return
     */
    private List<String> getHelpPagingDocUrl(List<String> pagePagingUrlList) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始进行分页抓取doc页面");
        List<String> docUrlList = new ArrayList<>();
        int failCount=0;
        if(!pagePagingUrlList.isEmpty()){
            for (String url : pagePagingUrlList) {
                log.info("开始进行help页面分页处理，url:",url);
                String htmlData = getOriginalRequestHtmlData(url, null);
                boolean validate = crawlerHelper.getDataValidateCallBack().validate(htmlData);
                if(validate){
                    List<String> urlList = new Html(htmlData).xpath(crawlerConfigProperty.getHelpCrawlerXpath()).links().all();
                    if(!urlList.isEmpty()){
                        docUrlList.addAll(urlList);
                    }else {
                        failCount++;
                        if(failCount>2){
                            break;
                        }
                    }
                }
            }
        }
        log.info("分页抓取doc页面完成，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
        return docUrlList;
    }

    /**
     * 生成分页的url
     * @param url
     * @param pageSize
     * @return
     */
    private List<String> generateHelpPagingUrl(String url, Integer pageSize) {
        String pageUrl = url.replace(helpUrlSuffix,helpPageSuffix);
        List<String> pagePagingUrlList = new ArrayList<>();
        for(int i = 2;i<pageSize;i++){
            pagePagingUrlList.add(pageUrl+i);
        }
        return pagePagingUrlList;
    }

    /**
     * 处理的爬虫类型
     * 正向
     * @param handelType
     * @return
     */
    @Override
    public boolean isNeedHandelType(String handelType) {
        return CrawlerEnum.HandelType.FORWARD.name().equals(handelType);
    }

    /**
     * 处理的文档类型
     * @param documentType
     * @return
     */
    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.HELP.name().equals(documentType);
    }

    /**
     * 优先级
     * @return
     */
    @Override
    public int getPriority() {
        return 110;
    }
}
