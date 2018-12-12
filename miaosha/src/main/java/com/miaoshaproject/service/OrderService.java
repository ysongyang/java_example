package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/12/11 0011 17:22
 * @Description:  订单交易
 */
public interface OrderService {

    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BusinessException;

}
