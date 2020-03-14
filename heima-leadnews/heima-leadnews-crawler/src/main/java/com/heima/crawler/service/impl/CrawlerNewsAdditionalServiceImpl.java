package com.heima.crawler.service.impl;

import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.pojos.ClNewsAdditional;
import com.heima.model.mappers.crawerls.ClNewsAdditionalMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@SuppressWarnings("all")
public class CrawlerNewsAdditionalServiceImpl implements CrawlerNewsAdditionalService {

    @Autowired
    private ClNewsAdditionalMapper clNewsAdditionalMapper;

    @Override
    public void saveAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.insertSelective(clNewsAdditional);
    }

    @Override
    public List<ClNewsAdditional> queryListByNeedUpdate(Date currentDate) {
        List<ClNewsAdditional> clNewsAdditionals = clNewsAdditionalMapper.selectListByNeedUpdate(currentDate);
        return clNewsAdditionals;
    }

    @Override
    public List<ClNewsAdditional> queryList(ClNewsAdditional clNewsAdditional) {
        return clNewsAdditionalMapper.selectList(clNewsAdditional);
    }

    @Override
    public boolean checkExist(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> clNewsAdditionals = clNewsAdditionalMapper.selectList(clNewsAdditional);
        if(null!=clNewsAdditionals && !clNewsAdditionals.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public ClNewsAdditional getAdditionalByUrl(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> clNewsAdditionals = clNewsAdditionalMapper.selectList(clNewsAdditional);
        if(null!=clNewsAdditionals && !clNewsAdditionals.isEmpty()){
            return clNewsAdditionals.get(0);
        }
        return null;
    }

    @Override
    public boolean isExistUrl(String url) {
        if(StringUtils.isNotEmpty(url)){
            ClNewsAdditional additionalByUrl = getAdditionalByUrl(url);
            if(null!=additionalByUrl){
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.updateByPrimaryKeySelective(clNewsAdditional);
    }

    @Override
    public List<ParseItem> toParseItem(List<ClNewsAdditional> additionalList) {
        List<ParseItem> parseItemList = new ArrayList<>();
        if(null!=additionalList && !additionalList.isEmpty()){
            for (ClNewsAdditional clNewsAdditional : additionalList) {
                ParseItem parseItem = toParseItem(clNewsAdditional);
                if(parseItem!=null){
                    parseItemList.add(parseItem);
                }
            }
        }
        return parseItemList;
    }

    public ParseItem toParseItem(ClNewsAdditional clNewsAdditional){
        CrawlerParseItem crawlerParseItem = null;
        if(clNewsAdditional != null){
            crawlerParseItem = new CrawlerParseItem();
            crawlerParseItem.setUrl(clNewsAdditional.getUrl());
        }
        return crawlerParseItem;
    }

    @Override
    public List<ParseItem> queryIncrementParseItem(Date currentDate) {
        List<ClNewsAdditional> clNewsAdditionals = queryListByNeedUpdate(currentDate);
        List<ParseItem> parseItemList = toParseItem(clNewsAdditionals);
        return parseItemList;
    }
}
