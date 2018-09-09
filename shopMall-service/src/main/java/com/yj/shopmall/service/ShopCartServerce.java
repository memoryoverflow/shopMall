package com.yj.shopmall.service;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.pojo.UserProduct;

import java.util.List;

public interface ShopCartServerce {
    public JsonResult addShop(String username, UserProduct uproduct);
    public JsonResult delShopAll(String username);
    public JsonResult delshop(String username, List<UserProduct> productList);
    public List<UserProduct> getShop(String username) ;
}
