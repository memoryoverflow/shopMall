package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.mapper.OrderMapper;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.redis.SeckillRedisLock;
import com.yj.shopmall.service.OrderServerce;
import com.yj.shopmall.service.ShopCartServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yj.shopmall.constant.Constant.*;

@Component
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServerceImpl implements OrderServerce {
    private final Logger logger = LoggerFactory.getLogger(OrderServerceImpl.class);

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ShopCartServerce shopCartServerce;

    @Autowired
    SeckillRedisLock seckillRedisLock;

    /**
     * Author: yj
     *
     * @Discription:
     * @Date:11:31 2018/7/6
     * @params:
     */
    //订单入库
    public JsonResult addOrders(Order order, String uanme, int j) {
        String userId = order.getUserId();
        String orderId = order.getOrderId();

        boolean b = seckillRedisLock.tryLock(userId);
        if (b) {
            //  入库前判断是否有库存
            List<UserProduct> productList = order.getUserProducts();
            String str = "";
            for (UserProduct uproduct : productList) {
                uproduct.setUp_order_Id(orderId);
                uproduct.setUp_userId(userId);
                Product product1 = orderMapper.checkStock(uproduct.getUp_productId());
                if (product1 != null) {
                    //如果购买的商品数量大于库存数量
                    if (uproduct.getUp_num() >= product1.getNum()) {
                        str += "-";
                    }
                }
            }
            //订单中只要有一个商品库存不足，该订单入库失败
            if (str.equals("")) {
                try {
                    int i = orderMapper.addOrder(order);
                    try {
                        int i1 = orderMapper.addUserProduct(productList);
                    } catch (Exception e) {
                        logger.info("插入用户商品表失败！" + e);
                        seckillRedisLock.releaseLock(userId);
                        throw new RuntimeException("下单失败！");
                    }
                    //下单成功，购物车结算下单 删除购物车信息
                    if (i >= 1) {
                        if (j == 0) {
                            //清空购物车；
                            JsonResult jsonResult = shopCartServerce.delshop(uanme, productList);

                            Order o = new Order();
                            o.setOrderId(orderId);
                            Order orderById = orderMapper.findOrderById(o);

                            logger.info("插入后查询结果：" + orderById);
                            logger.info("下单成功,清除购物车商品：" + jsonResult.getData());
                        }
                    }
                } catch (Exception e) {
                    logger.info("订单入库失败------->" + order + "  " + e.getMessage());
                    logger.info("错误信息-->" + e);
                    seckillRedisLock.releaseLock(userId);
                    throw new RuntimeException("下单失败！");
                }
            } else {
                seckillRedisLock.releaseLock(userId);
                return JsonResult.errorMsg("库存不足！");
            }
            //入库成功返回主键ID
        }
        seckillRedisLock.releaseLock(userId);
        return new JsonResult(200, "OK", orderId);
    }

    /*
     * 批量删除订单
     * */
    @Override
    public JsonResult delOrderById(String[] ids) {
        try {
            int i = orderMapper.delOrderById(ids);//删除订单表的订单
            int i1 = orderMapper.delUserProductById(ids);//删除订单商品表
            orderMapper.delLogistics(ids);
            orderMapper.delReturnGoods(ids);
            return JsonResult.ok(i);
        } catch (Exception e) {
            logger.info("删除订单异常：-->");
            throw new RuntimeException("删除异常" + e);
        }
    }

    //前台订单列表
    @Override
    public PageInfo<Order> findOrder(Order order) {
        PageInfo<Order> pageInfo = null;
        try {
            int currPage;
            if ((Integer) order.getCurrPage() == null || order.getCurrPage() == 0) {
                currPage = 1;
            } else {
                currPage = order.getCurrPage();
            }
            PageHelper.startPage(currPage, order.getPageSize());
            //查询结果
            List<Order> orderList = orderMapper.findOrder(order);
            logger.info("查询结果——》" + orderMapper.findOrder(order));

            pageInfo = new PageInfo<Order>(orderList);
        } catch (Exception e) {
            throw new RuntimeException("查询异常"+e);
        }
        return pageInfo;
    }

    @Override
    public PageInfo<Order> findReturnOrder(Order order) {
        PageInfo<Order> pageInfo = null;
        try {
            int currPage;
            if ((Integer) order.getCurrPage() == null || order.getCurrPage() == 0) {
                currPage = 1;
            } else {
                currPage = order.getCurrPage();
            }
            PageHelper.startPage(currPage, order.getPageSize());
            //查询结果
            List<Order> orderList = orderMapper.findReturnOrder(order);
            logger.info("查询结果——》" + orderList);

            pageInfo = new PageInfo<Order>(orderList);
        } catch (Exception e) {
            throw new RuntimeException("查询异常"+e);
        }
        return pageInfo;
    }

    //后台查询
    @Override
    public ResultLayui findOrderAdmin(Order order) {
        int orderCount = orderMapper.findOrderCount(order);
        int page = order.getPage();
        int pageStart = (page - 1) * order.getLimit();
        order.setPage(pageStart);
        List<Order> order1 = orderMapper.findOrderAdmin(order);
        logger.info("所有订单=[{}]",order1);
        return ResultLayui.jsonLayui(0, "", orderCount, order1);
    }


    //秒杀操作使用mysql 存储过程
    @Override
    public int addOrderSeckill(Order order) {
        return orderMapper.addOrderSeckill(order);
    }

    @Override
    public Order findOrderById(Order order) {
        Order orderById = orderMapper.findOrderById(order);
        return orderById;
    }

    /*
     * 更改订单状态
     * */
    @Override
    public JsonResult updateOrderStatus(Order order) {
        try {
            int i = orderMapper.updateOrderStatus(order);
        } catch (Exception e) {
            logger.info("支付失败：" + e);
            throw new RuntimeException("支付失败");
        }
        return JsonResult.build(200, "OK", null);
    }


    //    物流
    @Override
    public JsonResult addLogistics(Logistics logistics) {
        try {
            orderMapper.addLogistics(logistics);
        } catch (Exception e) {
            throw new RuntimeException("发货异常：" + e);
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult updateLogistics(Logistics logistics) {
        try {
            orderMapper.updateLogistics(logistics);
        } catch (Exception e) {
            throw new RuntimeException("修改异常：" + e);
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult delLogistics(String[] ids) {
        try {
            orderMapper.delLogistics(ids);
        } catch (Exception e) {
            throw new RuntimeException("删除异常：" + e);
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult findOne(String id) {
        try {
            Logistics one = orderMapper.findOne(id);

            return JsonResult.ok(one);
        } catch (Exception e) {
            throw new RuntimeException("查找异常：" + e);
        }
    }


//退货

    @Override
    public JsonResult addReturnGoods(ReturnGoods returnGoods) {

        try {
            orderMapper.addReturnGoods(returnGoods);
            Order order = new Order();
            order.setOrderId(returnGoods.getReturn_oId());
            order.setIsPayfor(ORDER_return);
            orderMapper.updateOrderStatus(order);
        } catch (Exception e) {
            throw new RuntimeException("添加异常：" + e);
        }

        return JsonResult.ok();
    }

    @Override
    public JsonResult updateReturnGoods(ReturnGoods returnGoods) {
        try {
            orderMapper.updateReturnGoods(returnGoods);
            System.out.println(returnGoods);
            Order order = new Order();
            if (returnGoods.getReturn_status()==RETURN_refuse) {
                order.setIsPayfor(ORDER_agree);
            }else{
                order.setIsPayfor(ORDER_refuse);
            }
            order.setOrderId(returnGoods.getReturn_oId());
            orderMapper.updateOrderStatus(order);
        } catch (Exception e) {
            throw new RuntimeException("修改异常：" + e);
        }

        return JsonResult.ok();
    }

    @Override
    public JsonResult delReturnGoods(String[] ids) {
        try {
            orderMapper.delReturnGoods(ids);
        } catch (Exception e) {
            throw new RuntimeException("删除异常：" + e);
        }

        return JsonResult.ok();
    }

    @Override
    public JsonResult findReturnGoodsOne(ReturnGoods returnGoods) {
        return JsonResult.ok(orderMapper.findReturnGoodsOne(returnGoods));
    }

    @Override
    public ResultLayui findReturnGoods(ReturnGoods returnGoods) {
        int returnGoodsCount = orderMapper.findReturnGoodsCount(returnGoods);
        int page = returnGoods.getPage();
        int pageStart = (page - 1) * returnGoods.getLimit();
        returnGoods.setPage(pageStart);
        List<ReturnGoods> returnGoodsListeturnGoods = orderMapper.findReturnGoods(returnGoods);
        return ResultLayui.jsonLayui(0, "", returnGoodsCount, returnGoodsListeturnGoods);
    }


}
