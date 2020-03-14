package com.heima.behavior.service;

import com.heima.model.behavior.dtos.ShowBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppShowBehaviorService {
    ResponseResult saveShowBehavior(ShowBehaviorDto dto);
}
