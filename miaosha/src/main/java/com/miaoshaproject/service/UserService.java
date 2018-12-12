package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

/**
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/12/10 0010 17:39
 * @Description:
 */
public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /**
     * @param telphone       用户手机号
     * @param encrptPassword 用户加密密码
     */
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;

}
