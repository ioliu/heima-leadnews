package com.heima.images.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.common.kafka.messages.app.ApHotArticleMessage;
import com.heima.images.config.InitConfig;
import com.heima.images.service.CacheImageService;
import com.heima.images.service.HotArticleImageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApHotArticles;
import com.heima.model.mappers.app.ApArticleContentMapper;
import com.heima.model.mappers.app.ApArticleMapper;
import com.heima.utils.common.ZipUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@SuppressWarnings("all")
public class HotArticleImageServiceImpl implements HotArticleImageService {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private CacheImageService cacheImageService;

    @Override
    public void handleHotImage(ApHotArticleMessage message) {
        ApHotArticles hotArticles = message.getData();
        log.info("处理热文章图片开始#articleId:{},message:{}",hotArticles.getArticleId(), JSON.toJSONString(hotArticles));

        //内容中的图片进行缓存
        ApArticleContent apArticleContent = apArticleContentMapper.selectByArticleId(hotArticles.getArticleId());
        if(null != apArticleContent){
            String content = ZipUtils.gunzip(apArticleContent.getContent());
            JSONArray array = JSONArray.parseArray(content);
            for(int i = 0;i<array.size();i++){
                JSONObject obj = array.getJSONObject(i);
                if(!"image".equals(obj.getString("type"))){
                    continue;
                }
                String imgUrl = obj.getString("value");
                //不是fastdfs中的图片不进行缓存
                if(!imgUrl.startsWith(InitConfig.PREFIX)){
                    log.info("非站内图片不进行缓存#articleId:{}",hotArticles.getArticleId());
                    continue;
                }
                //缓存图片
                cacheImageService.cache2Redis(imgUrl,true);
            }
        }
        //封面图片进行缓存
        ApArticle apArticle = apArticleMapper.selectById(Long.valueOf(hotArticles.getArticleId()));
        if(apArticle!=null && StringUtils.isNotEmpty(apArticle.getImages())){
            String[] articleImages = apArticle.getImages().split(",");
            for (String img : articleImages) {
                if(!img.startsWith(InitConfig.PREFIX)){
                    log.info("非站内图片不进行缓存#articleId:{}",hotArticles.getArticleId());
                    continue;
                }
                //缓存图片
                cacheImageService.cache2Redis(img,true);
            }
        }
        log.info("处理热文章图片结束#message:{}",JSON.toJSONString(message));
    }
}
