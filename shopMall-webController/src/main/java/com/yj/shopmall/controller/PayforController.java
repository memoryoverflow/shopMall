package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.OrderServerce;
import com.yj.shopmall.service.SeconkillServerce;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.Seckill;
import com.yj.shopmall.pojo.UserProduct;
import com.yj.shopmall.redis.SeckillRedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.yj.shopmall.constant.Constant.*;

@Controller
@RequestMapping("/payFor")
public class PayforController {

    private Logger logger = LoggerFactory.getLogger(PayforController.class);

    @Reference
    OrderServerce orderServerce;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Reference
    SeconkillServerce seconkillServerce;


    @Autowired
    SeckillRedisLock seckillRedisLock;


    /*
     * 付款页面
     * */
    @RequestMapping("/money/{orderId}")
    @Mylog(description = "准备支付")
    public String payFor(@PathVariable("orderId") String orderID, Model model) {
        logger.info("获取到orderId=[{}]", orderID);

        Order o = new Order();
        o.setOrderId(orderID);

        Order order = orderServerce.findOrderById(o);
//       String key=ORDER+"["+orderID+"-"+BaseController.getUserId()+"]";
//       String s = stringRedisTemplate.opsForValue().get(key);
//       Order order = JsonUtils.jsonToPojo(s, Order.class);

        logger.info("根据订单id获取订单信息：[{}]", order);
        model.addAttribute("orderMsg", order);

        return "commodity/payfor";
    }

    @RequestMapping("/seckillMoney/{orderId}")
    @Mylog(description = "秒杀支付")
    public String SeckillpayFor(@PathVariable("orderId") String orderID, Model model) {
        logger.info("获取到orderId=[{}]", orderID);

        String key = ORDER + "[" + orderID + "-" + BaseController.getUserId() + "]";
        String s = stringRedisTemplate.opsForValue().get(key);
        Order order = JsonUtils.jsonToPojo(s, Order.class);

        logger.info("根据订单id获取订单信息：[{}]", order);
        model.addAttribute("orderMsg", order);


        List<UserProduct> userProducts = order.getUserProducts();
        for (UserProduct userProduct : userProducts) {
            String up_productId = userProduct.getUp_productId();
            if (up_productId != null) {
                model.addAttribute("productId", up_productId);
                break;
            }
        }
        return "commodity/SeckillPayfor";
    }

    /*
     * 订单支付更改订单状态
     * */
    @RequestMapping("money/paying")
    @ResponseBody
    @Mylog(description = "进行支付")
    public JsonResult paying(Order order) {
        JsonResult jsonResult = orderServerce.updateOrderStatus(order);
        return jsonResult;
    }


    /*
     * 秒杀支付订单
     * */
    @RequestMapping("seckillMoney/paying")
    @ResponseBody
    public JsonResult seckillPayFoying(String orderId) {
        String userId = BaseController.getUserId();
        String key = ORDER + "[" + orderId + "-" + BaseController.getUserId() + "]";
        String orderMsg = stringRedisTemplate.opsForValue().get(key);
        try {
            //支付订单 进队
            orderInQueue(orderMsg);
        } catch (Exception e) {
            return JsonResult.errorMsg("下单异常");
        }
        return JsonResult.ok();
    }

    //秒杀的订单消息入队
    public void orderInQueue(String orderMsg) throws Exception {
        try {
            rabbitTemplate.convertAndSend(SECKILL_EXCHANGE, SECKILL_KEY, orderMsg);
            logger.info("订单消息进队成功:" + orderMsg + " " + new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss").format(new Date()));
        } catch (Exception e) {
            logger.info("订单入队列失败，order=[{}]", orderMsg);
            throw new Exception(e);
        }
    }


    //秒杀支付超时，跳转到支付页面
    @RequestMapping("/false/{productId}")
    @Mylog(description = "查看秒杀")
    public String toDetail(Model model, @PathVariable("productId") String pid) {
        logger.info("秒杀支付超时，即跳转秒杀页面");
        Seckill productById = seconkillServerce.findProductById(pid);
        model.addAttribute("product", productById);

        //将库存恢复
        String key = SECKILL_PRODUCT_STOCK + pid;
        boolean b = seckillRedisLock.tryLock(pid);
        if (b) {
            stringRedisTemplate.opsForValue().increment(key, 1L);
        }
        seckillRedisLock.releaseLock(pid);

        return "commodity/SeckillDetails";
    }


}
