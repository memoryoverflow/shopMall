package com.yj.shopmall.service;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.Seckill;
import com.yj.shopmall.pojo.SeckillExplain;

public interface SeconkillServerce {
    /*
     * 存储过程执行秒杀
     *
     * */
    void killByProcedure(String id);


    /*
     * 根据id 查询
     * */
    Seckill findProductById(String seckill_id);


    /*
    * 根据id 查询出 当前数据库存量
    * */
    JsonResult slectNumById(String id);

    /*
     * 秒杀减redis库存
     * */
    JsonResult reduceStock(Order order, String productId);


    /*
    * 判断秒杀时间是否已经开始
    * */
    JsonResult isStartSecKill(String productId);


    /*
     * 通过id和用户id
     * */
    Order selectOrderByIdAndUID(String orderId, String userId);

    /*
     * 获取seckill商品Id
     *
     * */
    public JsonResult selectKillId();


    //获取秒杀描述
    JsonResult findExplain();
    //genghuan
    int updateExplain(SeckillExplain explain);
    int addExplain(SeckillExplain explain);

    int updateSeckill(Seckill seckill);
    int addSeckill(Seckill seckill);

    //更改秒杀商品 更换ID
    JsonResult updateSeckillProductId(String id);

}
