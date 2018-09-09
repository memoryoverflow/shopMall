package com.yj.shopmall.redis;


import com.alibaba.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.*;

@Component
public class SeckillRedisLock {
    private static final Logger logger = LoggerFactory.getLogger(SeckillRedisLock.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 操作redis获取全局锁
     *
     * @param lockName       锁的名称
     * @param lockId         锁的值
     * @param timeout        获取的超时时间
     * @param tryInterval    多少ms尝试一次
     * @param lockExpireTime 获取成功后锁的过期时间
     * @return true 获取成功，false获取失败
     */
    public boolean getLock(String lockId, long timeout, long tryInterval, long lockExpireTime) {

        try {
            //判断锁的名字是否为空
            if (StringUtils.isEmpty(LOCK_NAME) || StringUtils.isEmpty(lockId)) {
                return false;
            }
            long startTime = System.currentTimeMillis();
            while (true) {
                if (stringRedisTemplate.opsForValue().setIfAbsent(LOCK_NAME, lockId)) {
                    stringRedisTemplate.opsForValue().set(LOCK_NAME, lockId, LOCK_EXPIRE, TimeUnit.MILLISECONDS);
                    logger.info("用户"+lockId+ " : 获得锁"+LOCK_NAME);
                    return true;
                } else {
                    logger.info("用户 "+lockId + "已占用锁,等待释放!!!-->"+LOCK_NAME);
                }
                //超时自动释放锁
                if (System.currentTimeMillis() - startTime > timeout) {
                    return false;
                }
                //等待... 继续请求拿锁
                Thread.sleep(tryInterval);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock 锁的名称
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(String lockId) {
        return getLock(lockId, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }


    /**
     * 尝试获取全局锁
     *
     * @param lock    锁的名称
     * @param timeout 获取超时时间 单位ms
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(String lockId, long timeout) {
        return getLock(lockId, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }


    /**
     * 尝试获取全局锁
     *
     * @param lock        锁的名称
     * @param timeout     获取锁的超时时间
     * @param tryInterval 多少毫秒尝试获取一次
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(String lockId, long timeout, long tryInterval) {
        return getLock(lockId, timeout, tryInterval, LOCK_EXPIRE);
    }


    /**
     * 尝试获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取锁的超时时间
     * @param tryInterval    多少毫秒尝试获取一次
     * @param lockExpireTime 锁的过期
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(String lockId, long timeout, long tryInterval, long lockExpireTime) {
        return getLock(lockId, timeout, tryInterval, lockExpireTime);
    }


    /**
     * 释放锁
     */
    public void releaseLock(String lockId) {
        if (!StringUtils.isEmpty(LOCK_NAME)) {
            logger.info("用户"+lockId + " : 已释放锁");
            stringRedisTemplate.delete(LOCK_NAME);
        }
    }


    //-----------
    public void seckill() throws InterruptedException {
        SeckillRedisLock seckillRedisLock = new SeckillRedisLock();

        //获得锁
        boolean b = seckillRedisLock.tryLock("213");
        if (b) {
            Thread.sleep(2000);
        }
        logger.info(Thread.currentThread().getName() + "获得了锁");
        seckillRedisLock.releaseLock(LOCK_NAME);
    }

}
