package com.yj.shopmall.mapper;


import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.UserProduct;
import com.yj.shopmall.pojo.orderAndproduct;

import java.util.List;

public interface StockMapper {
    //更新库存
    int updateStock(Product product);

    //向商品、订单表 中间表添加数据
    int addOrderAndProduct(List<orderAndproduct> list);

    //向用户商品表插入
    int addUserProduct(List<UserProduct> list);


}
