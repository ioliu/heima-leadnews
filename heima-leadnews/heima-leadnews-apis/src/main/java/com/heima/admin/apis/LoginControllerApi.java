package com.heima.admin.apis;

import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

public interface LoginControllerApi {

    /**
     * 登录
     * @param user
     * @return
     */
    public ResponseResult login(AdUser user);
}
