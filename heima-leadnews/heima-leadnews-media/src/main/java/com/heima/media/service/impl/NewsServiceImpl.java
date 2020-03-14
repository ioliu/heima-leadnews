package com.heima.media.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.common.kafka.messages.SubmitArticleAuthMessage;
import com.heima.common.media.constans.WmMediaConstans;
import com.heima.media.kafka.AdminMessageSender;
import com.heima.media.service.NewsService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.wemedia.WmMaterialMapper;
import com.heima.model.mappers.wemedia.WmNewsMapper;
import com.heima.model.mappers.wemedia.WmNewsMaterialMapper;
import com.heima.model.media.dtos.WmNewsDto;
import com.heima.model.media.dtos.WmNewsPageReqDto;
import com.heima.model.media.pojos.WmMaterial;
import com.heima.model.media.pojos.WmNews;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.mess.admin.SubmitArticleAuto;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class NewsServiceImpl implements NewsService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @Override
    public ResponseResult saveNews(WmNewsDto dto, Short type) {
        //如果用户传递参数为空或文章内容为空返回PARAM_REQUIRE错误
        if(dto==null || StringUtils.isEmpty(dto.getContent())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //如果用户本次为修改操作那么先删除数据库关联数据
        if(dto.getId()!=null){
            wmNewsMaterialMapper.delByNewsId(dto.getId());
        }
        //将用户提交的文章内容解析转为Map结构的数据
        String content = dto.getContent();
        Map<String,Object> materials;
        try {
            List<Map> list = objectMapper.readValue(content, List.class);
            //抽取信息
            Map<String,Object> extractInfo = extractUrlInfo(list);
            //图片map信息
            materials = (Map<String, Object>) extractInfo.get("materials");
            //图片数量
            int countImageNum = (int) extractInfo.get("countImageNum");
            //保存或修改文章的数据
            WmNews wmNews = new WmNews();
            BeanUtils.copyProperties(dto,wmNews);
            if(dto.getType().equals(WmMediaConstans.WM_NEWS_TYPE_AUTO)){
                saveWmNews(wmNews,countImageNum,type);
            }else{
                saveWmNews(wmNews,dto.getType(),type);
            }
            //保存内容中的图片和当前文章的关系
            if(materials.keySet().size()!=0){
                ResponseResult responseResult = saveRelationInfoForContent(materials,wmNews.getId());
                if(responseResult!=null){
                    return responseResult;
                }
            }
            //保存封面图片和当前文章的关系
            ResponseResult responseResult = coverIamgesRelation(dto,materials,wmNews,countImageNum);
            //流程处理完成返回处理结果

        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }

    /**
     * 保存封面图片与文章的关系
     * @param dto
     * @param materials
     * @param wmNews
     * @param countImageNum
     * @return
     */
    private ResponseResult coverIamgesRelation(WmNewsDto dto, Map<String, Object> materials, WmNews wmNews, int countImageNum) {
        List<String> images = dto.getImages();
        if(!WmMediaConstans.WM_NEWS_TYPE_AUTO.equals(dto.getType())&&dto.getType()!=images.size()){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"图文模式不匹配");
        }
        //如果是自动匹配封面
        if(WmMediaConstans.WM_NEWS_TYPE_AUTO.equals(dto.getType())){
            images = new ArrayList<>();
            if(countImageNum==WmMediaConstans.WM_NEWS_SINGLE_IMAGE){
                for (Object value : materials.values()){
                    images.add(String.valueOf(value));
                    break;
                }
            }

            if(countImageNum==WmMediaConstans.WM_NEWS_MANY_IMAGE){
               for(int i = 0;i<WmMediaConstans.WM_NEWS_MANY_IMAGE;i++){
                   images.add((String) materials.get(String.valueOf(i)));
               }
            }

            if(images.size()!=0){
                ResponseResult responseResult = saveRelationInfoForCover(images,wmNews.getId());
                if(responseResult!=null){
                    return responseResult;
                }
            }
        }else if(images!=null && images.size()!=0){
            ResponseResult responseResult = saveRelationInfoForCover(images,wmNews.getId());
            if(responseResult!=null){
                return responseResult;
            }
        }
        //更新文章信息
        if(images!=null){
            wmNews.setImages(StringUtils.join(images.stream().map(s -> s.replace(fileServerUrl,"")).collect(Collectors.toList()), ","));
            wmNewsMapper.updateByPrimaryKey(wmNews);
        }
        return null;
    }

    /**
     * 保存图片关系为封面
     * @param images
     * @param id
     * @return
     */
    private ResponseResult saveRelationInfoForCover(List<String> images, Integer id) {
        Map<String,Object> materials = new HashMap<>();
        for (int i = 0;i<images.size();i++) {
            String fileId = images.get(i);
            materials.put(String.valueOf(i),fileId.replace(fileServerUrl,""));
        }

        return saveRelationInfo(materials,id,WmMediaConstans.WM_IMAGE_REFERENCE);
    }

    /**
     * 保存图片关系---内容里的图片
     * @param materials
     * @param id
     * @return
     */
    private ResponseResult saveRelationInfoForContent(Map<String, Object> materials, Integer newsId) {
        return saveRelationInfo(materials,newsId,WmMediaConstans.WM_CONTENT_REFERENCE);        
    }

    /**
     * 保存关联关系到数据库
     * @param materials
     * @param newsId
     * @param wmContentReference
     * @return
     */
    private ResponseResult saveRelationInfo(Map<String, Object> materials, Integer newsId, Short type) {
        WmUser user = WmThreadLocalUtils.getUser();
        List<WmMaterial> dbMaterialInfos = wmMaterialMapper.findMaterialByUidAndimgUrls(user.getId(), materials.values());
        if(dbMaterialInfos!=null && dbMaterialInfos.size() != 0){
            Map<String, Object> urlIdMap = dbMaterialInfos.stream().collect(Collectors.toMap(WmMaterial::getUrl, WmMaterial::getId));
            for(String key:materials.keySet()){
                String fileId  = String.valueOf(urlIdMap.get(materials.get(key)));//5108
                if("null".equals(fileId)){
                    return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"应用图片失效");
                }
                materials.put(key,fileId);
            }

            //存储关联
            wmNewsMaterialMapper.saveRelationsByContent(materials,newsId,type);
        }
        return null;
    }

    @Autowired
    private AdminMessageSender adminMessageSender;

    /**
     * 保存文章
     * @param wmNews
     * @param countImageNum
     * @param type
     */
    private void saveWmNews(WmNews wmNews, int countImageNum, Short type) {
        if(countImageNum==WmMediaConstans.WM_NEWS_SINGLE_IMAGE){
            wmNews.setType(WmMediaConstans.WM_NEWS_SINGLE_IMAGE);
        }else if(countImageNum >=WmMediaConstans.WM_NEWS_MANY_IMAGE){
            wmNews.setType(WmMediaConstans.WM_NEWS_MANY_IMAGE);
        }else{
            wmNews.setType(WmMediaConstans.WM_NEWS_NONE_IMAGE);
        }
        wmNews.setStatus(type);
        WmUser user = WmThreadLocalUtils.getUser();
        wmNews.setUserId(user.getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short)1);
        int temp = 0;
        if(wmNews.getId()==null){
            temp = wmNewsMapper.insertNewsForEdit(wmNews);
        }else{
            temp = wmNewsMapper.updateByPrimaryKey(wmNews);
        }

        if(temp==1&&WmMediaConstans.WM_NEWS_SUMMIT_STATUS==type){
            SubmitArticleAuthMessage message = new SubmitArticleAuthMessage();
            SubmitArticleAuto submitArticleAuto = new SubmitArticleAuto();
            submitArticleAuto.setArticleId(wmNews.getId());
            submitArticleAuto.setType(SubmitArticleAuto.ArticleType.WEMEDIA);
            message.setData(submitArticleAuto);
            adminMessageSender.sendMessage(message);
        }
    }

    /**
     * 抽取图片信息
     * @param list
     * @return
     */
    private Map<String, Object> extractUrlInfo(List<Map> list) {
        Map<String,Object> materials = new HashMap<>();
        int order = 0;
        int countImageNum = 0;
        for (Map map : list) {
            order++;
            if("image".equals(map.get("type"))){
                countImageNum++;
                String imgUrl = String.valueOf(map.get("value"));
                if(imgUrl.startsWith(fileServerUrl)){
                    materials.put(String.valueOf(order),imgUrl.replace(fileServerUrl,""));
                }
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("materials",materials);
        res.put("countImageNum",countImageNum);
        return res;
    }

    @Override
    public ResponseResult listByUser(WmNewsPageReqDto dto) {
        if(dto==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        Long uid = WmThreadLocalUtils.getUser().getId();
        List<WmNews> datas = wmNewsMapper.selectBySelective(dto, uid);
        int total = wmNewsMapper.countSelectBySelective(dto, uid);
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),total);
        responseResult.setData(datas);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult findWmNewsById(WmNewsDto dto) {
        if(dto == null || dto.getId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE,"文章ID不可缺少");
        }
        WmNews wmNews = wmNewsMapper.selectNewsDetailByPrimaryKey(dto.getId());
        if(wmNews==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        ResponseResult responseResult = ResponseResult.okResult(wmNews);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult delNews(WmNewsDto dto) {
        if(dto ==null || dto.getId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询文章
        WmNews wmNews = wmNewsMapper.selectNewsDetailByPrimaryKey(dto.getId());
        //文章是否存在
        if(wmNews==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        //当前文章是否审核通过  app
        if(WmMediaConstans.WM_NEWS_AUTHED_STATUS.equals(wmNews.getStatus())||WmMediaConstans.WM_NEWS_PUBLISH_STATUS.equals(wmNews.getStatus())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前文章已通过审核不可删除");
        }
        //删除文章与素材的关系
        wmNewsMaterialMapper.delByNewsId(wmNews.getId());
        //删除文章
        wmNewsMapper.deleteByPrimaryKey(wmNews.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
