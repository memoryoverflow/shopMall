package com.yj.shopmall.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.SeconkillServerce;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.redis.SeckillRedis;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    private String responseMsg = "";

    @Reference
    SeconkillServerce seconkillServerce;

    @Autowired
    Sid sid;

    @Autowired
    SeckillRedis seckillRedis;
    /*
     * 订单秒杀页面
     * */
    @RequestMapping("/{productId}")
    @Mylog(description = "查看秒杀")
    public String toDetail(Model model, @PathVariable("productId") String pid) {
        logger.info("订单秒杀详情页面");
        Seckill productById = seconkillServerce.findProductById(pid);
        logger.info(JsonUtils.objectToJson(productById));
        model.addAttribute("product", productById);
        return "commodity/SeckillDetails";
    }



    /*
    * 执行秒杀
    * */
    @RequestMapping("/seckilling")
    @ResponseBody
    @Mylog(description = "执行秒杀")
    public JsonResult seckill(Order order,HttpSession session){

        //User user = (User) session.getAttribute("user_Session");
        order.setUserId(BaseController.getUserId());//用户Id
        order.setOrderId(sid.nextShort());//订单id 随机
        order.setCreateTime(new Date());//下单时间

        //用集合的原因 每笔订单中 会有多个商品 将多个商品批量入库
        String productId="";
        List<UserProduct> userProducts = order.getUserProducts();
        for (UserProduct userProduct:userProducts) {
            userProduct.setUp_order_Id(order.getOrderId());
            userProduct.setUp_userId(order.getUserId());
            productId = userProduct.getUp_productId();
        }
        JsonResult jsonResult = seconkillServerce.reduceStock(order,productId);


        return jsonResult;
    }



    /**
     * 执行秒杀入口
     */
//    @RequestMapping("/seckilling/{productId}")
//    @ResponseBody
//    public JsonResult test(@PathVariable("productId") String pId, HttpSession session) {
//        logger.info("秒杀请求");
//        //User user_session = (User) session.getAttribute("user_Session");
//        UserProduct uP=new UserProduct();
//
//        //随机生成用户id,模拟并发
//        UUID uuid = UUID.randomUUID();
//        String uid =  uuid.toString().substring(0,15).replace("-","");
//
//        Order order=new Order();
//
//        order.setOrderId(sid.nextShort());
//        order.setTotalPrice(10.0);
//        order.setAddress_Id("1");
//
//
//        //生成订单消息，模拟前台传过来的订单
//        Map<String, Object> map = new HashMap();
//
//        uP.setUp_name("1");
//        uP.setUp_price(10.0);
//        uP.setUp_color("红色");
//        uP.setUp_img("/images/product-slide/1-1-lg.jpg");
//        uP.setUp_totalprice(10.0);
//
//        uP.setUp_userId(uid);
//
//        uP.setUp_size(40.5);
//        uP.setUp_num(1);
//        uP.setUp_productId(pId);
//
//        order.setUserProduct(uP);
//
//        String up_productId = order.getUserProduct().getUp_productId();
//        String orderMsg = JsonUtils.objectToJson(order);
//
//        JsonResult jsonResult = seconkillServerce.reduceStock(order,pId);
//
//
//        return jsonResult;
//    }



    /*
    * 是否开始秒杀
    * */
    @RequestMapping("/isStart/{id}")
    @ResponseBody
    public JsonResult isStart(@PathVariable("id") String productId){
        JsonResult startSecKill = seconkillServerce.isStartSecKill(productId);
        return startSecKill;
    }


    /*
    * 获取系统时间
    * */
    @RequestMapping("/time/now")
    @ResponseBody
    public JsonResult time(){
        Date nowTime = new Date();
        JsonResult jsonResult = seconkillServerce.selectKillId();
        Object data = jsonResult.getData();
        return JsonResult.build(200,data.toString(),nowTime.getTime());
    }


    /*
    * 获取抢购商品入口
    * */
    @RequestMapping("/getProductId")
    @ResponseBody
    public JsonResult getKillProductId(){
        JsonResult jsonResult = seconkillServerce.selectKillId();
        return jsonResult;
    }

    //获取秒杀封面的信息
    @RequestMapping("/getSeckillExplainMsg")
    @ResponseBody
    public JsonResult<SeckillExplain> getSeckillExplainMsg() {
        JsonResult explain = seconkillServerce.findExplain();
        return explain;
    }

}
