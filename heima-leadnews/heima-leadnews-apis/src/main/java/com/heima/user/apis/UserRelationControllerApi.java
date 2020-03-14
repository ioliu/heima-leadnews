package com.heima.user.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

public interface UserRelationControllerApi {

    /**
     * 关注或取消关注
     */
    ResponseResult follow(UserRelationDto dto);
}
