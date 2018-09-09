package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.pojo.UserProduct;
import com.yj.shopmall.redis.CartRedis;
import com.yj.shopmall.service.ProductServerce;
import com.yj.shopmall.service.ShopCartServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Service
public class ShopCartServerceImpl implements ShopCartServerce {
    private static final Logger logger = LoggerFactory.getLogger(ShopCartServerceImpl.class);


    @Autowired
    ProductServerce productServerce;

    @Autowired
    CartRedis cartRedis;

    /*
     * 商品进入购物车
     * */
    public JsonResult addShop(String username, UserProduct uproduct) {
        double count = (double) uproduct.getUp_num();//商品个数量

        //商品信息设为0 因为判断 一个用户 如果添加相同的商品的话 保证 value 值 在个数上加上新的数量就好
        uproduct.setUp_num(0);
        uproduct.setUp_totalprice(0d);
        uproduct.setUp_order_Id("");

        // 转为字符窜存到 缓存中
        String productMsg = JsonUtils.objectToJson(uproduct);
        logger.info("商品进入购物车=[{}]",productMsg);

        //购物车名
         ;

        //-----> 入库逻辑
        //1、判断是否存在购物车
        if (!cartRedis.isHsahCartKey(username)) {
            //不存在则创建
            try {
                //向里面插入商品 购物车名（key）、商品名信息（productMsg）、数量（num）
                cartRedis.addCartToRedis(username, productMsg, count);

            } catch (Exception e) {
                logger.info("加入购物车失败," + e);
                return JsonResult.errorMsg("加入购物车失败");
                //throw new BusinessException("加入购物车失败," + e);
            }

        //如果存在
        } else {
            //购物车存在就添加一个值，如果商品存在就加一不存在就添加一个商品进购物车
            //将key对应的有序集合中member元素的scroe加上increment。如果指定的member不存在，
            // 那么将会添加该元素，并且其score的初始值为increment
            try {
                double aDouble = cartRedis.incrementNum(username, productMsg, count);

            } catch (Exception e) {
                logger.info("加入购物车失败=[{}]", e);
                return JsonResult.errorMsg("加入购物车失败");
                //throw new BusinessException("加入购物车失败," + e);
            }
        }
        List<UserProduct> mapshop = getShop(username);

        return JsonResult.ok(mapshop);
    }


    /*
     * 清空购物车
     * */
    public JsonResult delShopAll(String username) {
        try {
            cartRedis.delCartAll(username);
            return JsonResult.ok("购物已空");
        } catch (Exception e) {
            logger.info("清空购物车失败：" + e);
            return JsonResult.errorMsg("失败!");
        }
    }
    /*
     * 删除指定 购物车里面的 商品
     * */
    public JsonResult delshop(String username, List<UserProduct> productList) {
        Long aRemove = 0L;
        try {
            for (UserProduct uproduct : productList) {
                uproduct.setUp_num(0);
                uproduct.setUp_totalprice(0d);
                uproduct.setUp_order_Id("");
                String productMsg = JsonUtils.objectToJson(uproduct);
                logger.info("删除购物车的指定商品 json--->=[{}]",productMsg);
                aRemove+= cartRedis.delCartMsg(username, productMsg);
                logger.info("删除的条数=[{}]",aRemove);
            }
            return JsonResult.ok(aRemove);
        }catch (Exception e){
            logger.info("删除指定商品失败=[{}]",e);
            return JsonResult.errorMsg("失败");
        }
    }

    /*
     * 回显购物车信息
     * */
    public List<UserProduct> getShop(String username) {
        //先查出购物车有多少个商品
        Long aLong = cartRedis.getAllCartHashNum(username);

        //根据范围取出购物车所有商品
        Set<ZSetOperations.TypedTuple<String>> typedTuples = cartRedis.getAllCartMsg(username , 0, aLong);
        //存到集合中
        List<UserProduct> userProductList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> t : typedTuples) {
            logger.info("购物车信息回显--->"+"数量=[{}]",t.getScore() + ","+"商品信息=[{}]", t.getValue());
            //getValue()--商品信息  getScore()--购买数量

            //获取商品信息
            String str = t.getValue().toString();
            //获取数量 转为整型
            Integer score = Integer.parseInt(new DecimalFormat("0").format(t.getScore()));
            
            
            //将json字符 转化为 商品信息
            UserProduct userProduct = JsonUtils.jsonToPojo(str, UserProduct.class);
            logger.info("取出购物车里json，转为对象---->=[{}]",userProduct);
            //将商品个数量 赋值到 商品信息中；
            userProduct.setUp_num(score);

            //总价
            double v = userProduct.getUp_price() * score;

            DecimalFormat d = new DecimalFormat("#.00");

            userProduct.setUp_totalprice(Double.parseDouble(d.format(v)));
            logger.info(d.format(v));
            userProductList.add(userProduct);

        }
        return userProductList;
    }


}
