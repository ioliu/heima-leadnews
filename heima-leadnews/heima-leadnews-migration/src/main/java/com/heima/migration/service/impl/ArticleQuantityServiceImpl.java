package com.heima.migration.service.impl;

import com.heima.common.hbase.HBaseStorageClient;
import com.heima.common.hbase.constants.HBaseConstants;
import com.heima.common.hbase.entity.HBaseStorage;
import com.heima.migration.entity.ArticleHBaseInvok;
import com.heima.migration.entity.ArticleQuantity;
import com.heima.migration.service.*;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.utils.common.DataConvertUtils;
import com.sun.accessibility.internal.resources.accessibility;
import com.sun.tools.javac.code.Attribute;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ArticleQuantityServiceImpl implements ArticleQuantityService {

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @Autowired
    private ApArticleContentService apArticleContentService;

    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ApAuthorService apAuthorService;

    @Autowired
    private HBaseStorageClient storageClient;

    @Override
    public List<ArticleQuantity> getArticleQuantityList() {
        log.info("生成ArticleQuantity列表");
        List<ApArticle> unSyncArticleList = apArticleService.getUnSyncArticleList();
        //获取文章id列表
        List<String> apArticleIdList = unSyncArticleList.stream().map(apArticle -> String.valueOf(apArticle.getId())).collect(Collectors.toList());
        //获取文章中的作者id列表
        List<Integer> apAuthorIdList = unSyncArticleList.stream().map(apArticle -> apArticle.getAuthorId() == null ? null : apArticle.getAuthorId().intValue()).filter(x -> x != null).collect(Collectors.toList());
        //根据文章id列表查询文章配置列表和文章内容列表
        List<ApArticleConfig> apArticleConfigList = apArticleConfigService.queryByArticleIds(apArticleIdList);
        List<ApArticleContent> apArticleContentList = apArticleContentService.queryByArticleIds(apArticleIdList);
        //根据作者id列表查询作者列表
        List<ApAuthor> apAuthorList = apAuthorService.queryByIds(apAuthorIdList);
        //综合ArticleQuantity  --- list
        List<ArticleQuantity> articleQuantityList = unSyncArticleList.stream().map(apArticle -> {
            return new ArticleQuantity(){{
                setApArticle(apArticle);
                //根据文章的id过滤出符合要求的内容对象
                List<ApArticleContent> apArticleContents = apArticleContentList.stream().filter(x -> x.getArticleId().equals(apArticle.getId())).collect(Collectors.toList());
                if(null != apArticleContents && !apArticleContents.isEmpty()){
                    setApArticleContent(apArticleContents.get(0));
                }
                List<ApArticleConfig> apArticleConfigs = apArticleConfigList.stream().filter(x -> x.getArticleId().equals(apArticle.getId())).collect(Collectors.toList());
                if(null != apArticleConfigs && !apArticleConfigs.isEmpty()){
                    setApArticleConfig(apArticleConfigs.get(0));
                }
                List<ApAuthor> apAuthors = apAuthorList.stream().filter(x -> x.getId().equals(apArticle.getAuthorId().intValue())).collect(Collectors.toList());
                if(null != apAuthors && !apAuthors.isEmpty()){
                    setApAuthor(apAuthors.get(0));
                }
                //设置回调方法，用户方法的回调，用于修改同步的状态，查询hbase，成功后同步状态修改
                setHBaseInvok(new ArticleHBaseInvok(apArticle,(x)->apArticleService.updateSyncStatus(x)));
            }};
        }).collect(Collectors.toList());

        if(null != articleQuantityList && !articleQuantityList.isEmpty()){
            log.info("生成articleQuantity列表完成，size:{}",articleQuantityList.size());
        }else {
            log.info("生成articleQuantity列表完成，size:{}",0);
        }
        return articleQuantityList;
    }

    @Override
    public ArticleQuantity getArticleQuantityByArticleId(Long id) {
        if(null == id){
            return  null;
        }
        ArticleQuantity articleQuantity = null;
        ApArticle apArticle = apArticleService.getById(id);
        if(null != apArticle){
            articleQuantity = new ArticleQuantity();
            articleQuantity.setApArticle(apArticle);
            ApArticleContent apArticleContent = apArticleContentService.getByArticleId(apArticle.getId());
            articleQuantity.setApArticleContent(apArticleContent);
            ApArticleConfig apArticleConfig = apArticleConfigService.getByArticleId(apArticle.getId());
            articleQuantity.setApArticleConfig(apArticleConfig);
            ApAuthor apAuthor = apAuthorService.getById(apArticle.getAuthorId());
            articleQuantity.setApAuthor(apAuthor);
        }
        return articleQuantity;
    }

    @Override
    public ArticleQuantity getArticleQuantityByArticleidForHbase(Long id) {
        if(null == id){
            return null;
        }
        ArticleQuantity articleQuantity = null;
        List<Class> typeList = Arrays.asList(ApArticle.class,ApArticleConfig.class,ApArticleContent.class,ApAuthor.class);
        List<Object> objectList = storageClient.getStorageDataEntityList(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, DataConvertUtils.toString(id), typeList);
        if(null != objectList && !objectList.isEmpty()){
            articleQuantity = new ArticleQuantity();
            for (Object value : objectList) {
                if(value instanceof ApArticle){
                    articleQuantity.setApArticle((ApArticle) value);
                }else if(value instanceof  ApArticleContent){
                    articleQuantity.setApArticleContent((ApArticleContent) value);
                }else if(value instanceof ApArticleConfig){
                    articleQuantity.setApArticleConfig((ApArticleConfig) value);
                }else if(value instanceof ApAuthor){
                    articleQuantity.setApAuthor((ApAuthor) value);
                }
            }
        }
        return articleQuantity;
    }

    @Override
    public void dbToHbase() {
        long currentTimeMillis = System.currentTimeMillis();
        List<ArticleQuantity> articleQuantityList = getArticleQuantityList();
        if(null != articleQuantityList&& !articleQuantityList.isEmpty()){
            log.info("开始进行定时数据库到hbase同步，帅选出未同步的数据量:{}",articleQuantityList.size());
            List<HBaseStorage> hBaseStorageList = articleQuantityList.stream().map(ArticleQuantity::getHbaseStorage).collect(Collectors.toList());
            storageClient.addHBaseStorage(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME,hBaseStorageList);
        }else {
            log.info("定时数据库到hbase中，没有找到数据");
        }
        log.info("定时数据库到hbase同步结束,耗时:{}",System.currentTimeMillis()-currentTimeMillis);

    }

    @Override
    public void dbToHbase(Integer articleId) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始进行异步数据库到hbase同步，articleId:{}",articleId);
        if(null != articleId){
            ArticleQuantity articleQuantity = getArticleQuantityByArticleId(articleId.longValue());
            if(null!=articleQuantity){
                HBaseStorage hbaseStorage = articleQuantity.getHbaseStorage();
                storageClient.addHBaseStorage(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME,hbaseStorage);
            }
        }
        log.info("异步数据库到hbase同步完成，articleId:{},耗时:{}",articleId,System.currentTimeMillis()-currentTimeMillis);
    }
}
