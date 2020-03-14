package com.heima.media.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.pojos.WmUser;

public interface UserLoginService {

    /**
     * 登录方法
     */
    public ResponseResult login(WmUser user);
}
