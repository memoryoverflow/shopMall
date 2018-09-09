package com.yj.shopmall.service;

import com.github.pagehelper.PageInfo;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.pojo.Logistics;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.ReturnGoods;

public interface OrderServerce {
    /*
     * 商品下单
     * */
    JsonResult addOrders(Order order, String uname, int j) throws Exception;

    //删除订单
    JsonResult delOrderById(String[] ids) throws Exception;


    //订单列表
    PageInfo<Order> findOrder(Order order);
    PageInfo<Order> findReturnOrder(Order order);

    //管理员页面 获取
    ResultLayui findOrderAdmin(Order order);


    //秒杀操作 使用mysql 存储过程
    int addOrderSeckill(Order order);

    //获取订单 ByID
    Order findOrderById(Order order);
    JsonResult updateOrderStatus(Order order);



    JsonResult addLogistics(Logistics logistics);
    JsonResult updateLogistics(Logistics logistics);
    JsonResult delLogistics(String[] ids);
    JsonResult findOne(String id);


    //退款退货
    JsonResult addReturnGoods(ReturnGoods returnGoods);
    JsonResult updateReturnGoods(ReturnGoods logistics);
    JsonResult delReturnGoods(String[] ids);
    JsonResult findReturnGoodsOne(ReturnGoods returnGoods);
    ResultLayui findReturnGoods(ReturnGoods returnGoods);
}


