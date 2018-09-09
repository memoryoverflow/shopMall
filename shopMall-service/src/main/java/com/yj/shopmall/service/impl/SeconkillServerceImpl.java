package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.mapper.SeckillMapper;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.redis.SeckillRedis;
import com.yj.shopmall.service.SeconkillServerce;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yj.shopmall.constant.Constant.*;

@Component
@Service
public class SeconkillServerceImpl implements SeconkillServerce {

    @Autowired
    SeckillMapper seckillMapper;

    @Autowired
    SeckillRedis seckillRedis;

    private static final Logger logger = LoggerFactory.getLogger(SeconkillServerceImpl.class);
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;


    //秒杀的订单消息入队
    public void orderInQueue(String orderMsg) {
        try {
            rabbitTemplate.convertAndSend(SECKILL_EXCHANGE, SECKILL_KEY, orderMsg);
            logger.info("订单消息进队成功:" + orderMsg + " " + new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss").format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("消息如队列失败");
        }
    }

    /*
     * 秒杀减redis库存
     * */
    public JsonResult reduceStock(Order order, String productId) {

        String up_productId = productId;
        String userId = order.getUserId();
        String orderId = order.getOrderId();
        String orderMsg = JsonUtils.objectToJson(order);

        //减库存
        Long num = null;
        try {
            num = seckillRedis.reduceStock(order, userId, productId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("num为：" + num + " " + new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss").format(new Date()));
        //库存为 -1 抢完 ；-2 重复秒杀； -
        if (num == SECKILL_FAIL_NONUM) {
            logger.info("已抢完");
            return JsonResult.build(500, "已抢完", "秒杀失败/抢完");
        }
        if (num == SECKILL_FAIL_DOUBLE) {
            logger.info("重复秒杀");
            return JsonResult.build(500, "重复秒杀", "重复秒杀");
        }

        //减库存成功，订单消息暂时存放到redis
        if (num >= 0) {
            try {
                //orderInQueue(JsonUtils.objectToJson(order));
                //将订单信息暂时保存到redis
                seckillRedis.addOrder(order, userId);

                logger.info(userId + "-> 去预减库存结束");
                logger.info("");
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.build(500, "抢购失败", "抢购失败");
            }
        }
        return JsonResult.build(200, "秒杀成功", orderId);
    }

    /*
     *库存持久化
     * */
    @RabbitListener(queues = SECKILL_QUEUE)
    public void killByProcedure(String orderMsg) {
        logger.info("秒杀订单消息出队：" + orderMsg + " " + new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss").format(new Date()));
        Order order = JsonUtils.jsonToPojo(orderMsg, Order.class);
        Map<String, Object> map = new HashMap();

        String productId = "";
        String userId = "";
        List<UserProduct> userProducts = order.getUserProducts();
        for (UserProduct userProduct : userProducts) {

            productId = userProduct.getUp_productId();
            userId = userProduct.getUp_userId();

            map.put("up_name", userProduct.getUp_name());
            map.put("up_price", userProduct.getUp_price());
            map.put("up_color", userProduct.getUp_color());
            map.put("up_img", userProduct.getUp_img());
            map.put("up_totalprice", userProduct.getUp_price());
            map.put("up_size", userProduct.getUp_size());
            map.put("up_num", userProduct.getUp_num());
            map.put("up_orderId", order.getOrderId());
            map.put("createTime", new Date());
            map.put("totalPrice", order.getTotalPrice());
            map.put("address_Id", order.getAddress_Id());
            map.put("up_userId", userProduct.getUp_userId());
            map.put("up_productId", userProduct.getUp_productId());
            map.put("kill_Time", new Date());
        }

        seckillMapper.killByProcedure(map);
        int result = MapUtils.getInteger(map, "result", -2);
        logger.info("减库存状态码：=[{ }]" , String.valueOf(result));
        if (result == 1) {
            //stringRedisTemplate.opsForValue().set(order.getUserProduct().getUp_userId()+"-"+order.getOrderId(),orderMsg);
            logger.info("秒杀成功");
        } else {
            logger.info("秒杀失败");
        }

        String userKey = userId + "-" + productId;
        boolean haveKey = seckillRedis.isHaveKey(userKey);
        Order order1 = selectOrderByIdAndUID(order.getOrderId(), userId);
//        if (!haveKey) {
//            logger.info("--> 秒杀失败");
//        } else {
//            logger.info("");
//
//            logger.info("********************************************************************秒杀成功" + "{" + order1.getOrderId() + "," + userId + "}");
//
//            logger.info("********************************************************************秒杀成功" + "{" + order1.getOrderId() + "," + userId + "}");
//            logger.info("");
//        }
    }


    /*
     * 判断秒杀时间是否已经开始
     * */
    public JsonResult isStartSecKill(String productId) {
        Seckill seckill = new Seckill();
        boolean haveKey = seckillRedis.isHaveKey(SECKILL_PRODUCT + productId + "-ID");
        //判断当前数据是否存在
        if (haveKey) {
            //从缓存中拿数据
            String msg = seckillRedis.getMsg(SECKILL_PRODUCT + productId + "-ID");
            seckill = JsonUtils.jsonToPojo(msg, Seckill.class);
        } else {
            //从数据库获取
            seckill = seckillMapper.findProductById(productId);
            //并且存到redis中
            seckillRedis.add(productId + "-ID", seckill);
        }

        if (seckill == null) {
            return JsonResult.build(-2, "秒杀失败", null);
        }

        //判断时间
        Date startTime = seckill.getKill_startTime();
        Date endTime = seckill.getKill_endTime();
        Date NowTime = new Date();

        //  开启时间 > 现在时间 （未开始）
        //  现在时间 < 现在时间 （已经结束）
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("NowTime", NowTime);
        if (NowTime.getTime() < startTime.getTime()) {
            return JsonResult.build(200, SECKILL_NOSTART, map);
        }

        if (NowTime.getTime() > endTime.getTime()) {
            return JsonResult.build(200, SECKILL_OVER, map);
        }

        return JsonResult.build(200, SECKILL_START, map);
    }


    /*
     * 通过订单id 用户id 查询
     * */
    @Override
    public Order selectOrderByIdAndUID(String orderId, String userId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        return seckillMapper.selectOrderByIdAndUID(order);
    }

    /*
     * 拿出要秒杀的商品ID
     * */
    @Override
    public JsonResult selectKillId() {
        String s = seckillMapper.selectKillById();
        if (s == null || s.equals("")) {
            return JsonResult.build(200, "没有商品抢购活动", null);
        }
        return JsonResult.ok(s);
    }

    @Override
    public JsonResult findExplain() {
        SeckillExplain explain = seckillMapper.findExplain();
        return JsonResult.ok(explain);
    }

    //    修改秒杀商品的封面描述
    @Override
    public int updateExplain(SeckillExplain explain) {
        return seckillMapper.updateExplain(explain);
    }

    @Override
    public int addExplain(SeckillExplain explain) {
        return seckillMapper.addExplain(explain);
    }


    @Override
    public int updateSeckill(Seckill seckill) {
        return seckillMapper.updateSeckill(seckill);
    }

    @Override
    public int addSeckill(Seckill seckill) {
        return seckillMapper.addSeckill(seckill);
    }

    //更改秒杀商品 更换ID
    @Override
    public JsonResult updateSeckillProductId(String id) {
        Seckill seckill = new Seckill();
        SeckillExplain seckillExplain = new SeckillExplain();
        seckillExplain.setId(id);
        seckill.setKill_productId(id);

        try {
            int i = seckillMapper.updateSeckill(seckill);
            int i1 = seckillMapper.updateExplain(seckillExplain);
        } catch (Exception e) {
            logger.info("更换秒杀商品失败=[{}]",e);
            throw new RuntimeException("更换秒杀商品失败");
        }
        return JsonResult.ok();
    }

    /*
     *获取秒杀商品
     * */
    public Seckill findProductById(String seckill_id) {
        Seckill seckill = new Seckill();
        boolean haveKey = seckillRedis.isHaveKey(SECKILL_PRODUCT + seckill_id + "-ID");
        //判断当前数据是否存在
        if (haveKey) {
            //从缓存中拿数据
            String msg = seckillRedis.getMsg(SECKILL_PRODUCT + seckill_id + "-ID");
            seckill = JsonUtils.jsonToPojo(msg, Seckill.class);
        } else {
            //从数据库获取
            seckill = seckillMapper.findProductById(seckill_id);
            //并且存到redis中
            seckillRedis.add(seckill_id + "-ID", seckill);

            //将商品库存存到redis上 在redis上执行减库存的操作
            Product product = seckillMapper.selectNumById(seckill_id);
            seckillRedis.addStock(seckill_id, product.getNum().longValue());

        }
        return seckill;
    }

    /*
     * 获取库存量
     * */
    @Override
    public JsonResult slectNumById(String id) {
        return JsonResult.ok(seckillMapper.selectNumById(id));
    }



    /*
     * 每半个小时更新一次秒杀时间以及库存
     * */
//    @Scheduled(cron = "0 0/30 * * * ?")
//    public void getCron(){
//
//    }


}
