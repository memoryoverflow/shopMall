package com.yj.shopmall.redis;

public class ThreadA extends Thread {

    private SeckillRedisLock seckillRedisLock;

    public ThreadA(SeckillRedisLock seckillRedisLock) {
        this.seckillRedisLock = seckillRedisLock;
    }

    @Override
    public void run() {
        try {
            seckillRedisLock.seckill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
