package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApReadBehavior;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

public interface ApReadBehaviorMapper {
    int insert(ApReadBehavior record);
    int update(ApReadBehavior record);
    ApReadBehavior selectByEntryId(@Param("burst") String burst,@Param("entryId") Integer entryId,@Param("articleId") Integer articleId);
}