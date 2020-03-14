package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import org.apache.ibatis.annotations.Param;

public interface ApUnlikesBehaviorMapper {
    /**
     * 选择最后一条不喜欢数据
     * @return
     */
    ApUnlikesBehavior selectLastUnLike(@Param("entryId") Integer entryId,@Param("articleId")  Integer articleId);

    int insert(ApUnlikesBehavior record);
}