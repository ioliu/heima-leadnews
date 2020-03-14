package com.heima.behavior.service.impl;

import com.heima.behavior.service.AppShowBehaviorService;
import com.heima.model.behavior.dtos.ShowBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApShowBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.app.ApBehaviorEntryMapper;
import com.heima.model.mappers.app.ApShowBehaviorMapper;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AppShowBehaviorServiceImpl implements AppShowBehaviorService {

    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Autowired
    private ApShowBehaviorMapper apShowBehaviorMapper;

    @Override
    public ResponseResult saveShowBehavior(ShowBehaviorDto dto) {
        //获取用户信息，获取设备id
        ApUser user = AppThreadLocalUtils.getUser();
        //根据当前的用户信息或设备id查询行为实体 ap_behavior_entry
        if(user==null&&dto.getEquipmentId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        Long userId = null;
        if(user!=null){
            userId=user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipemntId(userId,dto.getEquipmentId());
        if(apBehaviorEntry==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取前台传递过来的文章列表id
//        List<ApArticle> articleIds = dto.getArticleIds();
        Integer[] articleIds = new Integer[dto.getArticleIds().size()];
        for(int i = 0;i<articleIds.length;i++){
            articleIds[i]=dto.getArticleIds().get(i).getId();
        }
        //根据行为实体id和文章列表id查询app行为表  ap_show_behavior
        List<ApShowBehavior> apShowBehaviors = apShowBehaviorMapper.selectListByEntryIdAndArticleIds(apBehaviorEntry.getId(),articleIds);
        //数据的过滤，需要删除表中已经存在的文章id
        List<Integer> integers = new ArrayList<>(Arrays.asList(articleIds));
        if(!apShowBehaviors.isEmpty()){
            apShowBehaviors.forEach(itemm->{
                Integer articleId = itemm.getArticleId();
                integers.remove(articleId);
            });
        }
        //保存操作
        if(!integers.isEmpty()){
            articleIds=new Integer[integers.size()];
            integers.toArray(articleIds);
            apShowBehaviorMapper.saveShowBehavior(articleIds,apBehaviorEntry.getId());
        }


        return ResponseResult.okResult(0);
    }


}
