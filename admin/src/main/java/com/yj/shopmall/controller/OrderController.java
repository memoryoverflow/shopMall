package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.Logistics;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.ReturnGoods;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.yj.shopmall.constant.Constant.ORDER_HAVESEND;

@RestController
@RequestMapping("/mall")
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Reference
    OrderServerce orderServerce;

    @Autowired
    Sid sid;

    /*
    订单列表
    */
    @RequestMapping("/orderList")
    @Mylog(description = "查询订单列表")
    public ResultLayui<Order> orderList(Order order) {

        ResultLayui orderAdmin = new ResultLayui();
        logger.info(order.toString());
        /*if (order == null) {
             orderAdmin = orderServerce.findOrderAdmin(new Order());
        } else {*/
        orderAdmin = orderServerce.findOrderAdmin(order);
        /*}*/
        logger.info(orderAdmin.toString());
        return orderAdmin;
    }


    /*
     * findOrderById
     * */
    @RequestMapping("/findOrderById")
    @Mylog(description = "查询指定订单")
    public JsonResult findOrderById(Order order) {
        ResultLayui orderAdmin = orderServerce.findOrderAdmin(order);
        return JsonResult.build(200, "OK", orderAdmin.getData());
    }


    /*
     * 管理员删除订单
     * */
    @RequestMapping("/delOrdersById")
    @Mylog(description = "删除订单")
    public JsonResult delOrdersById(String[] ids) throws Exception {
//        for (String i : ids) {
//            logger.info(i);
//        }
//        JsonResult jsonResult = orderServerce.delOrderById(ids);
//        //return JsonResult.ok();
//        return jsonResult;
        return JsonResult.ok();
    }


    @RequestMapping("/addLogistics")
    @Mylog(description = "发货")
    @ResponseBody
    public JsonResult addLogistics(Logistics logistics) {
        logistics.setId(sid.nextShort());
        logistics.setcTime(new Date());
        orderServerce.addLogistics(logistics);
        Order order = new Order();
        order.setOrderId(logistics.getO_Id());
        order.setIsPayfor(ORDER_HAVESEND);
        orderServerce.updateOrderStatus(order);
        return JsonResult.ok();
    }


    //    退货订单信息详情

    @RequestMapping("/returnOrderDetail")
    @ResponseBody
    public JsonResult returnOrderDetail(ReturnGoods returnGoods) {
        return orderServerce.findReturnGoodsOne(returnGoods);
    }


    //    是否同意退货
    @RequestMapping("/IsAgreeReturnOrder")
    @ResponseBody
    public JsonResult IsAgreeReturnOrder(ReturnGoods returnGoods) {
        return orderServerce.updateReturnGoods(returnGoods);
    }

}
