package com.yj.shopmall.mapper;


public interface RedisMapper {
    //添加购物车或者增加多条
    public String addShop(String username, String productId);
    //清空购物车
    public String delShopAll(String username);
    //删除购物车中的某个或多个商品
    public String delshop(String username, String[] productIdS);
    //获取购物车的商品
    public String getShop(String username);

    //判断是否在购物车
    public boolean IsHsahKey(String key);



}
