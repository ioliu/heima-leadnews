package com.heima.login.service.impl;

import com.heima.login.service.ValidateService;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.DESUtils;
import com.heima.utils.common.MD5Utils;
import org.springframework.stereotype.Service;

@Service
public class ValidateServiceImpl implements ValidateService {
    @Override
    public boolean validateDES(ApUser user, ApUser db) {
        if(db.getPassword().equals(DESUtils.encode(user.getPassword()))){
            return true;
        }
        return false;
    }

    @Override
    public boolean validateMD5(ApUser user, ApUser db) {
        if(db.getPassword().equals(MD5Utils.encode(user.getPassword()))){
            return true;
        }
        return false;
    }

    @Override
    public boolean validateWithSalt(ApUser user, ApUser db) {
        if(db.getPassword().equals(MD5Utils.encodeWithSalt(user.getPassword(),db.getSalt()))){
            return true;
        }
        return false;
    }
}
