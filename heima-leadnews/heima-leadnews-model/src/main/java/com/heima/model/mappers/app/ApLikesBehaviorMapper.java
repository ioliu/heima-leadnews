package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApLikesBehavior;
import org.apache.ibatis.annotations.Param;

public interface ApLikesBehaviorMapper {
    /**
     * 选择最后一条喜欢按钮
     * @return
     */
    ApLikesBehavior selectLastLike(@Param("burst") String burst,@Param("objectId") Integer objectId,@Param("entryId") Integer entryId,@Param("type") Short type);

    /**
     * 保存
     * @param record
     * @return
     */
    int insert(ApLikesBehavior record);
}