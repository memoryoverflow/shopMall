package com.yj.shopmall.mapper;

import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.Seckill;
import com.yj.shopmall.pojo.SeckillExplain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SeckillMapper {

    /*
     * 存储过程执行秒杀
     *
     * */
    void killByProcedure(Map<String, Object> paraMap);

    /*
     * 根据id 查询
     * */
    Seckill findProductById(String seckill_id);

    /*库存数量*/
    Product selectNumById(String productId);


    /*
     * 查询所有秒杀商品
     * */
    List<Product> findSeckillProduct();

    /*
    * 通过id和用户id
    * */
    Order selectOrderByIdAndUID(Order order);


    /*
    * 获取seckill商品Id
    *
    * */
    @Select("select kill_productId from seckill")
    String selectKillById();




    /*
     * 定时更新秒杀时间
     * */
//    @Update("update seckill set kill_startTime=#{kill_startTime},kill_endTime=#{kill_endTime}")
//    public void updateSeckillDate(Seckill seckill);


   //获取秒杀描述
    SeckillExplain findExplain();
    //genghuan
    int updateExplain(SeckillExplain explain);
    int addExplain(SeckillExplain explain);

    int updateSeckill(Seckill seckill);
    //活动秒杀时间
    int addSeckill(Seckill seckill);


}
