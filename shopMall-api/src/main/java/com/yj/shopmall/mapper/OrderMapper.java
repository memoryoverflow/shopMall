package com.yj.shopmall.mapper;

import com.yj.shopmall.pojo.*;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OrderMapper {
    /*
    * 商品下单
    * */
    int addOrder(Order order);

    //删除订单
    int updateOrderById(String id);
    /*
     * 删除用户商品表商品
     * */
    int delUserProductById(String[] list);

    //向用户商品表插入
    int addUserProduct(List<UserProduct> list);

    //删除订单
    int delOrderById(String[] list);

    //获取订单列表前台
    List<Order> findOrder(Order order);
    List<Order> findReturnOrder(Order order);

    //后台查询订单
    List<Order> findOrderAdmin(Order order);

    //秒杀操作 使用mysql 存储过程
    int addOrderSeckill(Order order);

    //检查库存
    Product checkStock(String productId);

    //获取订单 ByID
    Order findOrderById(Order order);

   //更改订单支付状态
    @Update("update tb_order set isPayfor=#{isPayfor} where orderId=#{orderId}")
    int updateOrderStatus(Order order);

    //总条数 分页用
    int findOrderCount(Order order);




    //物流
    int addLogistics(Logistics logistics);
    int updateLogistics(Logistics logistics);
    int delLogistics(String[] ids);
    Logistics findOne(String id);

    //退款退货
    int addReturnGoods(ReturnGoods returnGoods);
    int updateReturnGoods(ReturnGoods logistics);
    int delReturnGoods(String[] ids);
    ReturnGoods findReturnGoodsOne (ReturnGoods returnGoods);
    int findReturnGoodsCount(ReturnGoods returnGoods);
    List<ReturnGoods> findReturnGoods (ReturnGoods returnGoods);

}

