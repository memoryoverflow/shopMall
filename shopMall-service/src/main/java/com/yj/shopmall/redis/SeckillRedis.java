package com.yj.shopmall.redis;

import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.Seckill;
import com.yj.shopmall.service.SeconkillServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.*;


@Component
public class SeckillRedis {
    private static final Logger logger = LoggerFactory.getLogger(SeckillRedis.class);
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SeconkillServerce seconkillServerce;

    @Autowired
    SeckillRedisLock seckillRedisLock;


    /*
     * 插入数据商品信息
     * */
    public void add(String id, Seckill seckill) {
        String s = JsonUtils.objectToJson(seckill);
        //logger.info("将产品Id为"+seckill.getKill_productId()+"--->"+s);
        try {
            stringRedisTemplate.opsForValue().set(SECKILL_PRODUCT + id, s);
        } catch (Exception e) {
            throw new RuntimeException("存储失败" + e);
        }
    }

    /*
     * 将库存放到redis上
     * */
    public void addStock(String id, long num) {
        try {
            stringRedisTemplate.opsForValue().set(SECKILL_PRODUCT_STOCK + id, String.valueOf(num));
        } catch (Exception e) {
            throw new RuntimeException("存储失败" + e);
        }
    }

    /*减库存*/
    public long reduceStock(Order order, String uId, String productId) throws InterruptedException {
        logger.info("");
        logger.info("用户" + uId + "-- > 请求开始减库存------------------->");

        boolean b = seckillRedisLock.tryLock(uId);

        if (b) {
            //用户id-商品id
            String userKey = uId + "-" + productId;

            //判断库存
            String msg = getMsg(SECKILL_PRODUCT_STOCK + productId);
            int num = Integer.parseInt(msg);

            if (num > 0) {
                //先判断是否重复秒杀
                if (isHaveKey(USER_HASKILL + userKey)) {
                    seckillRedisLock.releaseLock(uId);
                    return SECKILL_FAIL_DOUBLE;
                }
                logger.info("检查库存量- >剩余：" + msg);
                //减库存操作
                Long n = stringRedisTemplate.opsForValue().increment(SECKILL_PRODUCT_STOCK + productId, -1L);
                logger.info("开始减库存，减一后，剩余：" + n);
                if (n >= 0) {
                    stringRedisTemplate.opsForValue().set(USER_HASKILL + userKey, JsonUtils.objectToJson(order));
                    seckillRedisLock.releaseLock(uId);
                    return n;
                }
                if (n < 0) {
                    seckillRedisLock.releaseLock(uId);
                    return SECKILL_FAIL_NONUM;
                }
            } else {
                seckillRedisLock.releaseLock(uId);
                return SECKILL_FAIL_NONUM; //秒杀失败
            }
        }
        seckillRedisLock.releaseLock(uId);
        return SECKILL_FAIL_NONUM;
    }


    /*
     * 获取数据
     * */
    public String getMsg(String productId) {
        String s = stringRedisTemplate.opsForValue().get(productId);
        return s;
    }


    /*
     * 判断是否存在
     * */
    public boolean isHaveKey(String key) {
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        return aBoolean;
    }


    /*
     * 减库存成功后，将订单信息暂存redis
     *
     * */
    public void addOrder(Order order, String uid) {
        String object = JsonUtils.objectToJson(order);
        String key = ORDER + "[" + order.getOrderId() + "-" + uid + "]";
        try {
            stringRedisTemplate.opsForValue().set(key, object, 600, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
