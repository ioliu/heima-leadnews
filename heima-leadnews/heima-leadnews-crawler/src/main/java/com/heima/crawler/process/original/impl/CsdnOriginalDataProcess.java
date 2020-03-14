package com.heima.crawler.process.original.impl;

import com.heima.crawler.config.CrawlerConfig;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.process.original.AbstractOriginalDataProcess;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsdnOriginalDataProcess extends AbstractOriginalDataProcess {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Override
    public List<ParseItem> parseOriginalRequestData(ProcessFlowData processFlowData) {
        List<ParseItem> parseItemList = null;
        List<String> initCrawlerUrlList = crawlerConfig.getInitCrawlerUrlList();
        if(initCrawlerUrlList!=null && !initCrawlerUrlList.isEmpty()){
            parseItemList = initCrawlerUrlList.stream().map(url->{
                CrawlerParseItem parseItem = new CrawlerParseItem();
                url = url+"?rnd="+System.currentTimeMillis();
                parseItem.setUrl(url);
                parseItem.setDocumentType(CrawlerEnum.DocumentType.INIT.name());
                parseItem.setHandelType(processFlowData.getHandelType().name());
                return parseItem;
            }).collect(Collectors.toList());
        }
        return parseItemList;
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
