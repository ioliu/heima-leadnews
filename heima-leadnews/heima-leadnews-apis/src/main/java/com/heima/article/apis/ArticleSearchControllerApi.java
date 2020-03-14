package com.heima.article.apis;

import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleSearchControllerApi {

    /**
     * 查询搜索历史
     * @param dto
     * @return
     */
    public ResponseResult findUserSearch(UserSearchDto dto);

    /**
     * 删除搜索历史
     * @param dto
     * @return
     */
    public ResponseResult delUserSearch(UserSearchDto dto);

    /**
     * 清空搜索历史记录
     * @param dto
     * @return
     */
    public ResponseResult clearUserSearch(UserSearchDto dto);

    /**
     * 今日热词
     * @param dto
     * @return
     */
    public ResponseResult hotkeywords(UserSearchDto dto);

    /**
     * 联想词查询
     * @param dto
     * @return
     */
    public ResponseResult searchassociate(UserSearchDto dto);

    /**
     * es文章分页查询
     * @param dto
     * @return
     */
    public ResponseResult esArticleSearch(UserSearchDto dto);
}
