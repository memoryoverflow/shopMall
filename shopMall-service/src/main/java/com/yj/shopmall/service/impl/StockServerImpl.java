package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.mapper.StockMapper;
import com.yj.shopmall.pojo.Order;
import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.UserProduct;
import com.yj.shopmall.pojo.orderAndproduct;
import com.yj.shopmall.service.StockServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yj.shopmall.constant.Constant.ORDER_QUEUE;

@Component
@Service
@Transactional
public class StockServerImpl implements StockServerce {
    private final Logger logger = LoggerFactory.getLogger(StockServerImpl.class);
    @Autowired
    StockMapper stockMapper;

    private String orderMsg = "";

    //订阅订单消息 更新库存信息
    @RabbitListener(queues = ORDER_QUEUE)
    public void updateStockAndOther(String orderStr) throws Exception {
        this.orderMsg = orderStr;
        logger.info("已订阅订单消息------->"+orderStr);

        Order order = JsonUtils.jsonToPojo(orderStr, Order.class);

        String userId = order.getUserId();
        String orderId = order.getOrderId();


        logger.info("用户id:" + userId);
        logger.info("订单id:" + orderId);
        List<UserProduct> userProducts = order.getUserProducts();
        for (UserProduct product : userProducts) {
            //减库存
            try {
                int i1 = stockMapper.updateStock(new Product(product.getUp_num(), product.getUp_productId()));
                logger.info("商品个数：" + product.getUp_num());

                //设置用户 id 和 订单id
                product.setUp_userId(userId);
                product.setUp_order_Id(order.getOrderId());

            } catch (Exception e) {
                logger.info("减库存失败");
                throw new Exception("更新库存失败:" + e.getMessage());
            }
        }
    }


    //更新库存
    public int updateStock(Product product) {
        return stockMapper.updateStock(product);
    }

    @Override
    public int addOrderAndProduct(List<orderAndproduct> list) {
        return 0;
    }


}
