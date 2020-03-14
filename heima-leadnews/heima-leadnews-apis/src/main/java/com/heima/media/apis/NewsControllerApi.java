package com.heima.media.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;
import com.heima.model.media.dtos.WmNewsPageReqDto;

public interface NewsControllerApi {

    /**
     * 保存文章
     * @param dto
     * @return
     */
    public ResponseResult submitNews(WmNewsDto dto);

    /**
     * 保存草稿
     * @return
     */
    public ResponseResult saveDraftNews(WmNewsDto dto);

    /**
     * 用户查询文章列表
     * @param dto
     * @return
     */
    public ResponseResult listByUser(WmNewsPageReqDto dto);

    /**
     * 根据id获取文章信息
     * @param dto
     * @return
     */
    public ResponseResult wmNews(WmNewsDto dto);

    /**
     * 删除文章
     * @param dto
     * @return
     */
    public ResponseResult delNews(WmNewsDto dto);
}
