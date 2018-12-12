package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/12/11 0011 15:53
 * @Description:
 */
@RestController
@RequestMapping("item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") //跨域注解
public class ItemController extends BaseController {


    @Autowired
    private ItemService itemService;


    /**
     * 创建商品
     *
     * @param title
     * @param price
     * @param stock
     * @param description
     * @param imgUrl
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") Double price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "imgUrl") String imgUrl
    ) throws BusinessException {
        //封装Service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel itenModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itenModelForReturn);
        //System.out.println(itemVO);
        return CommonReturnType.create(itemVO);
    }

    /**
     * 商品详情页浏览
     *
     * @param id
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) throws BusinessException {

        ItemModel itemModel = itemService.getItemById(id);
        System.out.println(itemModel.getPrice());
        ItemVO itemVO = convertVOFromModel(itemModel);
        System.out.println(itemVO.getPrice());
        return CommonReturnType.create(itemVO);
    }


    /**
     * 商品详情列表浏览
     *
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getlistItem() throws BusinessException {
        List<ItemModel> itemModelList = itemService.listItem();
        //使用stream api将list内的itemModel转换为ItemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }


    /**
     * ItemModel 转换为 ItemVO
     *
     * @param itemModel
     * @return
     */
    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }

}
