package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.admin.service.ReviewCrawlerArticleService;
import com.heima.common.aliyun.AliyunImageScanRequest;
import com.heima.common.aliyun.AliyunTextScanRequest;
import com.heima.common.common.pojo.EsIndexEntity;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.*;
import com.heima.model.common.constants.ESIndexConstants;
import com.heima.model.crawler.pojos.ClNews;
import com.heima.model.mappers.admin.AdChannelMapper;
import com.heima.model.mappers.app.*;
import com.heima.model.mappers.crawerls.ClNewsMapper;
import com.heima.utils.common.Compute;
import com.heima.utils.common.ZipUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@SuppressWarnings("all")
public class ReviewCrawlerArticleServiceImpl implements ReviewCrawlerArticleService {

    @Autowired
    private ClNewsMapper clNewsMapper;

    @Override
    public void autoReivewArticleByCrawler() throws Exception {
        ClNews clNews = new ClNews();
        clNews.setStatus((byte)1);
        List<ClNews> clNewsList = clNewsMapper.selectList(clNews);
        if(null != clNewsList && !clNewsList.isEmpty()){
            log.info("定时任务自动审核检索未审核数量：{}",clNewsList.size());
            for (ClNews news : clNewsList) {
                autoReivewArticleByCrawler(news);
            }
        }else{
            log.info("定时任务自动审核未检索出数据");
        }
    }

    @Override
    public void autoReivewArticleByCrawler(Integer clNewsId) throws Exception {
        ClNews clNews = new ClNews();
        clNews.setId(clNewsId);
        clNews.setStatus((byte)1);
        ClNews clNews1 = clNewsMapper.selectByIdAndStatus(clNews);
        if(null != clNews1){
            autoReivewArticleByCrawler(clNews1);
        }
    }

    @Autowired
    private AliyunTextScanRequest aliyunTextScanRequest;

    @Autowired
    private AliyunImageScanRequest aliyunImageScanRequest;

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Override
    public void autoReivewArticleByCrawler(ClNews clNews) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始自动审核流程");
        if(null != clNews){
            //审核内容和标题的匹配度
            String content = clNews.getUnCompressContent();
            String title = clNews.getTitle();
            if(content==null || title == null){
                updateClNews(clNews,"文章内容或标题为空");
                return ;
            }
            double degree = Compute.SimilarDegree(content, title);
            if(degree <= 0){
                updateClNews(clNews,"文章和标题不匹配");
                return ;
            }
            log.info("开始文本内容的审核");
            //审核图片和文本
            List<String> images = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            JSONArray jsonArray = JSON.parseArray(content);
            handlerTextAndImages(images,sb,jsonArray);
            //文本审核
            String response = aliyunTextScanRequest.textScanRequest(sb.toString());
            if(null == response || !response.equals("pass")){
                updateClNews(clNews,"文本内容审核失败");
                return ;
            }
            //审核图片
            String imageResponse = aliyunImageScanRequest.imageScanRequest(images);
            if(null == imageResponse || !imageResponse.equals("pass")){
                updateClNews(clNews,"图片内容审核失败");
                return ;
            }
            //保存数据 文章，文章配置，文章内容，作者，标签
            //频道的获取
            Integer channelId = clNews.getChannelId();
            String channelName = "";
            if(null != channelId){
                AdChannel adChannel = adChannelMapper.selectByPrimaryKey(channelId);
                if(null != adChannel){
                    channelName = adChannel.getName();
                }
            }
            //作者
            ApAuthor apAuthor = saveApAuthor(clNews);
            //文章
            ApArticle apArticle = saveApArticleByCrawler(images,channelId,channelName,apAuthor.getId(),clNews);
            //保存标签
            saveApArticleLabel(apArticle);
            //保存文章配置
            ApArticleConfig apArticleConfig = saveApArticleConfig(apArticle);
            //保存文章内容
            saveArArticleContent(content,apArticle);
            //创建索引
            try {
                createEsIndex(apArticle,clNews);
            }catch (Exception e){
                e.printStackTrace();
            }

            //更改状态 为 9
            updateClnewsSuccess(clNews);
        }
        log.info("审核流程结束，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
    }

    /**
     * 审核成功修改状态
     * @param clNews
     */
    private void updateClnewsSuccess(ClNews clNews) {
        clNews.setStatus((byte)9);
        clNewsMapper.updateStatus(clNews);
    }

    @Autowired
    private JestClient jestClient;

    /**
     * 创建索引对象
     * @param apArticle
     * @param clNews
     */
    private void createEsIndex(ApArticle apArticle, ClNews clNews) throws IOException {

        EsIndexEntity esIndexEntity = saveEsIndexEntity(apArticle,clNews);
        Index.Builder builder = new Index.Builder(esIndexEntity);
        builder.id(apArticle.getId().toString());
        builder.refresh(true);
        Index index = builder.index(ESIndexConstants.ARTICLE_INDEX).type(ESIndexConstants.DEFAULT_DOC).build();
        JestResult result = jestClient.execute(index);
        if(result!=null && !result.isSucceeded()){
            throw new RuntimeException(result.getErrorMessage()+"插入更新索引失败!");
        }
    }

    /**
     * 构建索引对象
     * @param apArticle
     * @param clNews
     * @return
     */
    private EsIndexEntity saveEsIndexEntity(ApArticle apArticle, ClNews clNews) {
        EsIndexEntity esIndexEntity = new EsIndexEntity();
        esIndexEntity.setId(new Long(apArticle.getId()));
        esIndexEntity.setContent(clNews.getUnCompressContent());
        esIndexEntity.setStatus(new Long(1));
        esIndexEntity.setTitle(clNews.getTitle());
        esIndexEntity.setPublishTime(new Date());
        if(apArticle.getChannelId()!=null){
            esIndexEntity.setChannelId(new Long(apArticle.getChannelId()));
        }
        return esIndexEntity;

    }

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    /**
     * 保存文章内容
     * @param content
     * @param apArticle
     */
    private void saveArArticleContent(String content, ApArticle apArticle) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(ZipUtils.gzip(content));
        apArticleContentMapper.insert(apArticleContent);
    }

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    /**
     * 保存文章配置信息
     * @param apArticle
     * @return
     */
    private ApArticleConfig saveApArticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsForward(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsComment(true);
        apArticleConfig.setIsDown(false);
        apArticleConfigMapper.insert(apArticleConfig);
        return apArticleConfig;
    }

    @Autowired
    private ApArticleLabelMapper apArticleLabelMapper;

    /**
     * 保存标签
     * @param apArticle
     */
    private void saveApArticleLabel(ApArticle apArticle) {
       if(apArticle!=null && StringUtils.isNotEmpty(apArticle.getLabels())){
           String[] labelArray = apArticle.getLabels().split(",");
           for (String labelId : labelArray) {
               ApArticleLabel apArticleLabel = new ApArticleLabel(apArticle.getId(),Integer.parseInt(labelId));
               List<ApArticleLabel> apArticleLabels = apArticleLabelMapper.selectList(apArticleLabel);
               if(null != apArticleLabels && !apArticleLabels.isEmpty()){
                   ApArticleLabel apArticleLabel1 = apArticleLabels.get(0);
                   apArticleLabel1.setCount(apArticleLabel1.getCount()+1);
                   apArticleLabelMapper.updateByPrimaryKeySelective(apArticleLabel1);
               }else{
                   apArticleLabel.setCount(1);
                   apArticleLabelMapper.insert(apArticleLabel);
               }
           }
       }
    }

    @Autowired
    private ApArticleMapper apArticleMapper;

    /**
     * 保存文章
     * @param images
     * @param channelId
     * @param channelName
     * @param id
     * @param clNews
     * @return
     */
    private ApArticle saveApArticleByCrawler(List<String> images, Integer channelId, String channelName, Integer authorId, ClNews clNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setChannelId(channelId);
        apArticle.setChannelName(channelName);
        apArticle.setAuthorId(new Long(authorId));
        apArticle.setAuthorName(clNews.getName());
        apArticle.setLayout((short)clNews.getType());
        apArticle.setPublishTime(clNews.getPublishTime());
        apArticle.setTitle(clNews.getTitle());
        apArticle.setOrigin(false);
        apArticle.setCreatedTime(new Date());
        StringBuilder sb = new StringBuilder();
        if(null != images && !images.isEmpty()){
            for(int i = 0;i<images.size() && i< 3;i++){
                if(i!=0){
                    sb.append(",");
                }
                sb.append(images.get(i));
            }
        }
        apArticle.setImages(sb.toString());
        apArticleMapper.insert(apArticle);
        return apArticle;
    }

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    /**
     * 获取作者信息
     * @param clNews
     * @return
     */
    private ApAuthor saveApAuthor(ClNews clNews) {
        ApAuthor apAuthor = apAuthorMapper.selectByAuthorName(clNews.getName());
        if(apAuthor ==null || apAuthor.getId() == null){
            apAuthor = new ApAuthor();
            apAuthor.setCreatedTime(new Date());
            apAuthor.setName(clNews.getName());
            apAuthor.setType(0);
            apAuthorMapper.insert(apAuthor);
        }
        return apAuthor;
    }

    /**
     * 解析文本和图片
     * @param images
     * @param sb
     * @param jsonArray
     */
    private void handlerTextAndImages(List<String> images, StringBuilder sb, JSONArray jsonArray) {
        if(null != jsonArray){
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String type = (String) jsonObject.get("type");
                if("image".equals(type)){
                    String value = (String) jsonObject.get("value");
                    images.add(value);
                }

                if("text".equals(type)){
                    String value = (String) jsonObject.get("value");
                    sb.append(value);
                }
            }
        }
    }

    /**
     * 修改文章信息
     * @param clNews
     * @param 文章内容或标题为空
     */
    private void updateClNews(ClNews clNews, String message) {
        clNews.setStatus((byte)2);
        clNews.setReason(message);
        clNewsMapper.updateStatus(clNews);
    }
}
