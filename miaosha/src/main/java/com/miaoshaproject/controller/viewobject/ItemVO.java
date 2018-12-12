package com.miaoshaproject.controller.viewobject;


import java.math.BigDecimal;

/**
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/12/11 0011 15:53
 * @Description:
 */
public class ItemVO {


    private Integer id;

    private String title; //名称

    private Double price; //价格

    private Integer stock; //库存

    private String description; //描述

    private Integer sales;//销量

    private String imgUrl; //商品图片


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
