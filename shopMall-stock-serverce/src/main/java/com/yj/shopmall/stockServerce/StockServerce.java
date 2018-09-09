package com.yj.shopmall.stockServerce;

import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.orderAndproduct;

import java.util.List;

public interface StockServerce {
    //更新库存
    int updateStock (Product product);
    //向商品、订单表 中间表添加数据
    int addOrderAndProduct(List<orderAndproduct> list);

}
