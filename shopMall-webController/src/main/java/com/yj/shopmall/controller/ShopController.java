package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.pojo.User;
import com.yj.shopmall.pojo.UserProduct;
import com.yj.shopmall.service.ShopCartServerce;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shopCart")
public class ShopController {
    private Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Reference
    ShopCartServerce shopCartServerce;


    //商品详情处 添加购物车
    @RequiresUser
    @RequestMapping("/addShopCart")
    @ResponseBody
    @Mylog(description = "添加购物车")
    public JsonResult addShop(UserProduct uproduct, HttpSession session) {
        //User user_session = (User) session.getAttribute("user_Session");
        User user = BaseController.getUser();
        uproduct.setUp_userId(user.getuId());
        logger.info("当前用户" + "" + user.getName() + "" + "添加商品到购物车------>" + uproduct.toString());
        JsonResult jsonResult = shopCartServerce.addShop(user.getName(), uproduct);
        return jsonResult;
    }


    //购物车处通过 加 减 修改商品个数
    @RequestMapping("/addOneShopCart")
    @ResponseBody
    @Mylog(description = "修改购物车商品数量")
    public JsonResult addOneShop(String jsonMsgProduct, Integer temp, Integer nums, double sumPrice, HttpSession session, Model model) {
        //User user_session = (User) session.getAttribute("user_Session");
        String userName = BaseController.getUserName();
        UserProduct userProduct = JsonUtils.jsonToPojo(jsonMsgProduct, UserProduct.class);
        userProduct.setUp_num(temp);

        //取出商品的单价
        double price = userProduct.getUp_price();
        //取出商品的总价
        double ToltakPrice = userProduct.getUp_totalprice();

        //接收结果
        JsonResult jsonResult = new JsonResult();

        //为增加商品
        if (temp == 1) {
            //先删除原来的，再进行更新
            JsonResult jsonResult1 = clearCart(jsonMsgProduct, session, model);
            logger.info(jsonResult1.getData() + "删除提示消息");
            if (jsonResult1.getData().toString().equals("1")) {
                /*//商品加一后的总价（需重新封装）
                double SumPrice = price + ToltakPrice;*/
                //重新封装产品参数
                userProduct.setUp_num(nums);
                logger.info("数量------->" + nums.toString() + "--------------------->");
                userProduct.setUp_totalprice(sumPrice);
                logger.info("商品数量加 1 后的总价：" + sumPrice);
                logger.info("商品封装后的信息为-->：" + userProduct);
                try {
                    jsonResult = shopCartServerce.addShop(userName, userProduct);
                } catch (Exception e) {
                    return JsonResult.errorMsg("失败");
                }
            }

        } else {
            //先删除原来的，再进行更新
            JsonResult jsonResult1 = clearCart(jsonMsgProduct, session, model);
            if (jsonResult1.getData().toString().equals("1")) {
                //商品加一后的总价（需重新封装）
                //double SumPrice2 = ToltakPrice - price;
                //重新封装产品参数
                userProduct.setUp_num(nums);
                userProduct.setUp_totalprice(sumPrice);

                logger.info("商品数量减 1 后的总价：" + sumPrice);
                logger.info("商品封装后的信息为-->：" + userProduct);
                try {
                    jsonResult = shopCartServerce.addShop(userName, userProduct);
                } catch (Exception e) {
                    return JsonResult.errorMsg("失败");
                }

            }
        }

        logger.info("购物车处通过 加 减 修改商品个数------->  " + userProduct);


        return jsonResult;
    }


    /*购物车信息回显*/
    @RequestMapping("/ShopCartList")
    @ResponseBody
    public JsonResult shopCartList(HttpSession session) throws Exception {
        //User user_session = (User) session.getAttribute("user_Session");
        try {
            List<UserProduct> shop = shopCartServerce.getShop(BaseController.getUserName());
            return JsonResult.ok(shop);
        } catch (Exception e) {
            return JsonResult.ok();
        }
    }

    //删除某个
    @RequestMapping("/clearCart")
    @ResponseBody
    @Mylog(description = "删除购物车信息")
    public JsonResult clearCart(String ids, HttpSession session, Model model) {
        //User user_session = (User) session.getAttribute("user_Session");
        String userName = BaseController.getUserName();
        List<UserProduct> list = new ArrayList<>();
        UserProduct userProduct = JsonUtils.jsonToPojo(ids, UserProduct.class);
        logger.info(userName + "删除当前商品 pojo--->" + userProduct);
        list.add(userProduct);
        JsonResult jsonResult = shopCartServerce.delshop(userName, list);
        return jsonResult;
    }

    //删除某个或者多个
    @RequestMapping("/clearCartMany")
    @ResponseBody
    @Mylog(description = "删除购物车信息")
    public JsonResult clearCart2(String product, HttpSession session, Model model) {
        //User user_session = (User) session.getAttribute("user_Session");
        String userName = BaseController.getUserName();
        List<UserProduct> userProductList = new ArrayList<>();
        String[] split = product.split("===");

        for (String s : split) {
            logger.info(s);
            UserProduct userProduct = JsonUtils.jsonToPojo(s, UserProduct.class);
            logger.info(userName + "删除当前商品 pojo--->" + userProduct);
            userProductList.add(userProduct);
        }
        JsonResult jsonResult = shopCartServerce.delshop(userName, userProductList);

        return jsonResult;
    }


    //清空购物车
    @RequestMapping("/clearCartAll")
    @ResponseBody
    @Mylog(description = "清空购物车")
    public JsonResult clearCartAll(HttpSession session) {
        //User user_session = (User) session.getAttribute("user_Session");
        JsonResult jsonResult = shopCartServerce.delShopAll(BaseController.getUserName());
        return jsonResult;
    }


    //购物车页面
    @RequestMapping("/toShoipCart")
    @Mylog(description = "浏览购物车")
    public String toShopCart() {
        return "commodity/shopingCartList";
    }
}


