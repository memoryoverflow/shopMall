package com.yj.shopmall.constant;

public class Constant {
    /*用户*/
    public static final int USER_ISACTIVATED = -1; //是否激活 1是激活 -1是未激活 默认是-1
    public static final int USER_ISFROZEN = -1; //是否冻结  1是正常 -1是冻结默认是-1
//    默认用户头像
    public static final String user_img_default="http://106.14.226.138:7777/commonuserimg.jpg";


    /*订单*/
    public static final String ORDER_HAVEPAYFOR = "1"; //1 已付款
    public static final String ORDER_HAVESEND = "2"; //2付款为发货
    public static final String ORDER_return = "4"; //4.申请退款
    public static final String ORDER_refuse = "6"; //3.拒绝退款
    public static final String ORDER_agree = "5"; //5.同意退款

    //    退货状态
    public static final int RETURN_refuse = 1; //同意
    public static final int RETURN_agree = 2; //拒绝


    /*redis的订单前缀*/
    public static final String ORDER = "ORDER:";


    /*Session会话*/
    public static final String REDIS_SESSION_Prefix = "REDIS-SESSIONID:"; //session
    public static final String USER_MSG = "USER-MSG:";//方便管理员实时更新用户的信息

    /*秒杀商品*/
    public static final String SECKILL_PRODUCT = "SECKILL:"; //库存前缀
    public static final String SECKILL_PRODUCT_STOCK = "SECKILL:STOCK:"; //库存
    public static final String SECKILL_PRODUCT_STOCKBACKUP = "SECKILL:STOCKBACKUP:";  //备用库存 用户下单后不付款后恢复
    public static final String USER_HASKILL = "HASKILL-USER:";  //已经秒杀过了的用户

    //MQ 订单
    public static final String ORDER_QUEUE = "OrderQueue";
    public static final String ORDER_EXCHANGE = "OrderExchange";
    public static final String ORDERQUEUE_KEY = "ORDERQUEUE.KEY";
    //MQ Seckill
    public static final String SECKILL_QUEUE = "SeckillQueue";//秒杀信息队列
    public static final String SECKILL_EXCHANGE = "seckillExchange";//秒杀信息队列交换器
    public static final String SECKILL_KEY = "SECKILL.KEY";//秒杀信息队列路由键

    public static final long SECKILL_FAIL_NONUM = -1L;//已抢完 result
    public static final long SECKILL_FAIL_DOUBLE = -2L;//重复
    public static final long SECKILL_SUCCESS = 1L;// 成功
    public static final long SECKILL_FAIL = -3L;// 减库存失败

    public static final String SECKILL_START = "1";//开启
    public static final String SECKILL_OVER = "0";//秒杀已经结束
    public static final String SECKILL_NOSTART = "-1"; // 未开始

    /*用户的购物车*/
    public static final String SHOP_CART = "-SHOPCART";


    /*Redis锁*/
    public static final long LOCK_EXPIRE = 20 * 1000L; //单个业务持有锁的时间20s,防止死锁
    public static final long LOCK_TRY_INTERVAL = 10L; //默认10ms尝试一次
    public static final long LOCK_TRY_TIMEOUT = 30 * 1000L; // 默认尝试30s
    public static final String LOCK_NAME = "REDIS:ADDORDERLOCK"; //锁名

    /*用户地址*/
    public static final int ADDRESS_DEFAULT = 1; // 默认地址
    public static final int ADDRESS_USUAL = 0; // 不是默认


    //用户登录的session名
    public static final String USER_SESSIOMN = "user_Session:"; // 不是默认

    // userTaken
    public static final String USER_TOKEN = "USER_TOKEN:";
    //session时间
    public static final long REDIS_EXPIRE = 1800000;

}
