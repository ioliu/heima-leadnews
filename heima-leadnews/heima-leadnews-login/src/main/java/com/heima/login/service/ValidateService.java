package com.heima.login.service;

import com.heima.model.user.pojos.ApUser;

/**
 * 对称加密  DES  AES
 * 散列算法 MD5  加盐 salt
 */
public interface ValidateService {

    /**
     * DES验证
     * @param user
     * @param db
     * @return
     */
    public boolean validateDES(ApUser user,ApUser db);

    /**
     * MD5验证
     * @param user
     * @param db
     * @return
     */
    public boolean validateMD5(ApUser user,ApUser db);

    /**
     * MD5加盐验证
     * @param user
     * @param db
     * @return
     */
    public boolean validateWithSalt(ApUser user,ApUser db);
}
