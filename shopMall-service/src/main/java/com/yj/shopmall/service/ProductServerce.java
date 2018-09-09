package com.yj.shopmall.service;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.pojo.ImgLLg;
import com.yj.shopmall.pojo.Product;

import java.util.List;

public interface ProductServerce {
    //列表
    ResultLayui<Product> findProduct(Product product);
    //前台显示
    List<Product> findProductWeb(Product product);

    //ById
    Product findProductById(String productId);

    //更新产品
    Product updateProduct(Product product);

    //更改图片
    Product updateImg_llg(ImgLLg imgLLg);

    //添加商品
    JsonResult addProduct(Product product);

    //删除
    int delProductById(String[] ids, int temp);

    //List<UserProduct> findUserProductById(String []ids);
}
