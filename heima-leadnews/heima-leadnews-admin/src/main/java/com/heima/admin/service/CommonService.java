package com.heima.admin.service;

import com.heima.model.admin.dtos.CommonDto;
import com.heima.model.common.dtos.ResponseResult;

public interface CommonService {

    /**
     * 列表查询-->无条件查询，无条件统计    有条件的查询 有条件的统计
     * @param dto
     * @return
     */
    public ResponseResult list(CommonDto dto);

    /**
     * 通过dto中的model来判断，选择使用新增或修改
     * @param dto
     * @return
     */
    public ResponseResult update(CommonDto dto);

    /**
     * 通用的删除
     * @param dto
     * @return
     */
    public ResponseResult delete(CommonDto dto);
}
