package com.yj.shopmall.mapper;


import com.yj.shopmall.pojo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    //列表
    List<Product> findProduct(Product product);

    //列表
    List<Product> findProductWeb(Product product);
    //ById
    Product findProductById(String productId);

    //更新产品
    int updateProduct(Product product);

    //add
    int addProduct(Product product);
    //添加商品图片

    int addProductImg_llg(List<ImgLLg> list);

    int addProductSize(List<ProductSize> list);
    int addProductColor(List<ProductColor> list);


    int delProductImg_llg(String product_Id);

    int delProductSize(String product_Id);
    int delProductColor(String product_Id);


    int updateImg_llg(ImgLLg imgLLg);

    //删除
    int delProductById(@Param("isdel") int isdel, @Param("ids") String[] ids);

    //根据用户商品Id查询出用户的产品
    List<UserProduct> findUserProductById(String[] ids);


    int updateProductColor(ProductColor color);

    int updateProductImg_lg(ImgLg imgLg);

    int Img_llgDown(ImgLLg imgSm);
    int Img_lgDown(ImgLg imgSm);

    int Img_smDown(ImgSm imgSm);

    int updateProductSize(ProductSize size);

    int findProductCount(Product product);
}
