package com.heima.migration.service.impl;

import com.heima.common.common.storage.StorageData;
import com.heima.common.hbase.HBaseStorageClient;
import com.heima.common.hbase.constants.HBaseConstants;
import com.heima.common.mongo.entity.MongoStorageEntity;
import com.heima.migration.entity.ArticleQuantity;
import com.heima.migration.service.ApHotArticleService;
import com.heima.migration.service.ArticleQuantityService;
import com.heima.model.article.pojos.ApHotArticles;
import com.heima.model.mappers.app.ApHotArticlesMapper;
import com.heima.utils.common.DataConvertUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@SuppressWarnings("all")
public class ApHotArticleServiceImpl implements ApHotArticleService {

    @Autowired
    private ApHotArticlesMapper apHotArticlesMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HBaseStorageClient hBaseStorageClient;

    @Autowired
    private ArticleQuantityService articleQuantityService;

    @Override
    public List<ApHotArticles> selectList(ApHotArticles apHotArticles) {
        return apHotArticlesMapper.selectList(apHotArticles);
    }

    @Override
    public void insert(ApHotArticles apHotArticles) {
        apHotArticlesMapper.insert(apHotArticles);
    }

    @Override
    public void hotApArticleSync(Integer articleId) {
        //根据文章id查询数据
        log.info("开始热数据同步，文章id:{}", articleId);
        ArticleQuantity articleQuantity = getHotArticleQuantity(articleId);
        if (articleQuantity != null) {
            //同步热点数据到数据库
            hotApArticleToDBSync(articleQuantity);
            //同步数据到mongo
            hotApArticleToMongoSync(articleQuantity);
            log.info("热数据同步完成，articleId:{}", articleId);
        } else {
            log.info("找不到对应的数据，articleId:{}", articleId);
        }

    }

    /**
     * 同步到数据库
     *
     * @param articleQuantity
     */
    private void hotApArticleToDBSync(ArticleQuantity articleQuantity) {
        //新通过文章id查询，数据库是否存在
        Integer apArticleId = articleQuantity.getApArticleId();
        log.info("开始将热数据同步到mysql,apArticleId:{}", apArticleId);
        if (null == apArticleId) {
            log.info("apArticleId不存在，无法进行同步");
            return;
        }
        ApHotArticles apHotArticles = new ApHotArticles();
        apHotArticles.setArticleId(apArticleId);
        List<ApHotArticles> hotArticlesList = apHotArticlesMapper.selectList(apHotArticles);
        if (null != hotArticlesList && !hotArticlesList.isEmpty()) {
            //存在  则不需要保存
            log.info("mysql数据已同步，不需要再次同步，apArticleId:{}", apArticleId);
        } else {
            //不存在，新增热点数据
            ApHotArticles temp = articleQuantity.getApHotArticles();
            apHotArticlesMapper.insert(temp);
        }


    }

    /**
     * 同步到mongo
     *
     * @param articleQuantity
     */
    private void hotApArticleToMongoSync(ArticleQuantity articleQuantity) {
        Integer apArticleId = articleQuantity.getApArticleId();
        log.info("开始将热数据同步到mongo,apArticleId:{}", apArticleId);
        if (null == apArticleId) {
            log.info("apArticleId不存在，无法进行同步");
            return;
        }
        String rowKey = DataConvertUtils.toString(apArticleId);
        MongoStorageEntity mongoStorageEntity = mongoTemplate.findById(rowKey, MongoStorageEntity.class);
        if (null != mongoStorageEntity) {
            log.info("Mongo数据已同步，不需要再次同步，apArticleId:{}", apArticleId);
        } else {
            List<StorageData> storageDataList = articleQuantity.getStorageDataList();
            if(null != storageDataList && !storageDataList.isEmpty()){
                mongoStorageEntity = new MongoStorageEntity();
                mongoStorageEntity.setRowKey(rowKey);
                mongoStorageEntity.setDataList(storageDataList);
                mongoTemplate.insert(mongoStorageEntity);
            }
        }
        log.info("将数据同步到mongo完成，apArticleId:{}",apArticleId);
    }

    /**
     * 获取热数据的articleQuantity
     *
     * @param articleId
     * @return
     */
    private ArticleQuantity getHotArticleQuantity(Integer articleId) {
        ArticleQuantity articleQuantity = articleQuantityService.getArticleQuantityByArticleId(articleId.longValue());
        if (null == articleQuantity) {
            articleQuantity = articleQuantityService.getArticleQuantityByArticleidForHbase(articleId.longValue());
        }
        return articleQuantity;
    }

    @Override
    public void deleteById(Integer id) {
        apHotArticlesMapper.deleteById(id);
    }

    @Override
    public List<ApHotArticles> selectExpireMonth() {
        return apHotArticlesMapper.selectExpireMonth();
    }

    @Override
    public void deleteHotData(ApHotArticles apHotArticles) {
        deleteById(apHotArticles.getId());
        String rowKey = DataConvertUtils.toString(apHotArticles.getArticleId());
        hBaseStorageClient.gethBaseClent().deleteRow(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME,rowKey);
        MongoStorageEntity storageEntity = mongoTemplate.findById(rowKey, MongoStorageEntity.class);
        if(null!=storageEntity){
            mongoTemplate.remove(storageEntity);
        }
    }
}
