package com.heima.media.service.impl;

import com.heima.common.fastdfs.FastDfsClient;
import com.heima.media.service.MaterialService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.wemedia.WmMaterialMapper;
import com.heima.model.mappers.wemedia.WmNewsMaterialMapper;
import com.heima.model.media.dtos.WmMaterialDto;
import com.heima.model.media.dtos.WmMaterialListDto;
import com.heima.model.media.pojos.WmMaterial;
import com.heima.model.media.pojos.WmUser;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private FastDfsClient fastDfsClient;

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //获取当前登录用户
        WmUser wmUser = WmThreadLocalUtils.getUser();
        if(wmUser==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //验证参数
        if(multipartFile==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //上传图片到fastdfs
        String originalFilename = multipartFile.getOriginalFilename();//aa.jpg
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        if(!extName.matches("(gif|png|jpg|jpeg)")){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }
        String fileId = null;
        try {
            fileId = fastDfsClient.uploadFile(multipartFile.getBytes(), extName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} upload file {} to fastDfs error,error info:n", wmUser.getId(),originalFilename,e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //上传成功后，保存一条数据wm_material
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setType((short)0);
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setUserId(wmUser.getId());
        wmMaterialMapper.insert(wmMaterial);
        wmMaterial.setUrl(fileServerUrl+fileId);
        return ResponseResult.okResult(wmMaterial);
    }

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult delPicture(WmMaterialDto dto) {
        WmUser user = WmThreadLocalUtils.getUser();
        if(dto==null || dto.getId() ==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //删除fastdfs中的图片 先去判断当前图片有没有关联
        WmMaterial wmMaterial = wmMaterialMapper.selectByPrimaryKey(dto.getId());
        if(wmMaterial==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        int count = wmNewsMaterialMapper.countByMid(wmMaterial.getId());
        if(count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前图片被引用");
        }
        String fileId = wmMaterial.getUrl().replace(fileServerUrl,"");
        try {
            fastDfsClient.delFile(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} delete file {} from fastDfs error,error info :n",user.getId(),fileId,e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //删除 wm_material表中的数据
        wmMaterialMapper.deleteByPrimaryKey(dto.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult findList(WmMaterialListDto dto) {
        //验证参数
        dto.checkParam();
        //获取用户
        Long uid = WmThreadLocalUtils.getUser().getId();
        //查询
        List<WmMaterial> datas = wmMaterialMapper.findListByUidAndStatus(dto, uid);
        datas = datas.stream().map((item)->{
            item.setUrl(fileServerUrl+item.getUrl());
            return item;
        }).collect(Collectors.toList());
        int total = wmMaterialMapper.countListByUidAndStatus(dto, uid);
        //返回
        Map<String,Object> resDatas = new HashMap<>();
        resDatas.put("curPage",dto.getPage());
        resDatas.put("size",dto.getSize());
        resDatas.put("list",datas);
        resDatas.put("total",total);
        return ResponseResult.okResult(resDatas);
    }

    @Override
    public ResponseResult changeUserMaterialStatus(WmMaterialDto dto, Short type) {
        if(dto ==null || dto.getId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取用户信息
        WmUser user = WmThreadLocalUtils.getUser();
        wmMaterialMapper.updateStatusByUidAndId(dto.getId(),user.getId(),type);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
