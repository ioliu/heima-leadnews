package com.heima.login.service;

import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppFollowBehaviorService {
    /**
     * 存储关注行为数据
     */
    public ResponseResult saveFollowBehavior(FollowBehaviorDto dto);
}
