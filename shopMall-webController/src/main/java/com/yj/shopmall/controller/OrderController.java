package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.Utils.UploadFile;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.pojo.*;

import com.yj.shopmall.service.OrderServerce;

import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yj.shopmall.constant.Constant.ORDERQUEUE_KEY;
import static com.yj.shopmall.constant.Constant.ORDER_EXCHANGE;

@Controller
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    //调用接口
    @Reference
    OrderServerce orderServerce;

    @Autowired
    Sid sid;

    //订单消息入队 由库存服务去减库存
    public void orderInQueue(String orderMsg) {
        logger.info("订单消息进队:" + orderMsg);
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDERQUEUE_KEY, orderMsg);
    }

    /*
     * 下单
     * */
    @RequestMapping("/creatOrder")
    @ResponseBody
    @Mylog(description = "用户下单")
    public JsonResult addOrder(String product, String addressId, Order order1, HttpSession session)
            throws Exception {

        //User user = (User) session.getAttribute("user_Session");
        User user = BaseController.getUser();
        JsonResult jsonResult = new JsonResult();

        //product 不为空 购物车页面来的 ，购物车页面将订单信息 拼接一个字符窜

        if (product == null || product.equals("")) {

            order1.setOrderId(sid.nextShort());
            order1.setCreateTime(new Date());
            order1.setUserId(user.getuId());
            jsonResult = orderServerce.addOrders(order1, user.getName(), 1);
            String msg = jsonResult.getMsg();
            //订单持久化成功后 消息进队
            if (msg.equals("OK")) {
                //数据转换为json
                String objectStr = JsonUtils.objectToJson(order1);
                //调用进队的方法
                orderInQueue(objectStr);
            }
            logger.info("订单信息------1>" + order1);
        } else {
            Order order = new Order();
            List<UserProduct> userProductList = new ArrayList<>();
            String[] split = product.split("===");
            for (String s : split) {
                UserProduct userProduct = JsonUtils.jsonToPojo(s, UserProduct.class);
                userProductList.add(userProduct);
            }
            double totalPrice = 0L;
            //计算总价
            for (UserProduct uProduct : userProductList) {
                totalPrice += uProduct.getUp_totalprice();
            }
            DecimalFormat d = new DecimalFormat("#.00");
            order.setTotalPrice(Double.parseDouble(d.format(totalPrice)));
            order.setUserProducts(userProductList);
            order.setOrderId(sid.nextShort());
            order.setCreateTime(new Date());
            order.setUserId(user.getuId());
            order.setAddress_Id(addressId);

            logger.info("订单信息------2>" + order);
            jsonResult = orderServerce.addOrders(order, user.getName(), 0);
            String msg = jsonResult.getMsg();
            logger.info("下单后返回响应结果" + jsonResult);
            //订单持久化成功后 消息进队
            if (msg.equals("OK")) {
                //数据转换为json
                String objectStr = JsonUtils.objectToJson(order);
                //调用进队的方法
                orderInQueue(objectStr);
            }
        }
        return jsonResult;
    }

    /*
     * 删除订单
     * */
    @RequestMapping("/delOrder")
    @Mylog(description = "删除订单")
    public String delOrder(String[] orderIds, Model model) throws Exception {

        logger.info("数组长度：" + String.valueOf(orderIds.length));
        if (orderIds != null) {
            JsonResult jsonResult = orderServerce.delOrderById(orderIds);
            model.addAttribute("msg", jsonResult);
        }
        Order order = new Order();
        order.setPageSize(3);
        PageInfo<Order> pageInfo = orderServerce.findOrder(order);
        model.addAttribute("pageInfo", pageInfo);
        return "redirect:/oList";
    }

    /*
     * 订单列表详情 查询订单页面
     * */
    @RequestMapping("/Personal")
    @Mylog(description = "个人中心")
    public String orderList(HttpSession session) {
        return "commodity/personal";
    }


    @RequestMapping("/orderList")
    public String toOList(Order order, Model model, HttpSession session) {
        //获取用户Id
        User user = BaseController.getUser();
        PageInfo<Order> pageInfo = new PageInfo();

        //分页按钮有个标识符 如果标识符为0  说明不是搜索结果的分页 是默认所有的结果分页
        order.setPageSize(3);//设置每页多少条
        order.setUserId(user.getuId());

        if ("0".equals(order.getTemp())) {
            pageInfo = orderServerce.findOrder(order);
            model.addAttribute("temp", "0");
        } else {

            //筛选条件后面会带个标志 判断是否搜索结果进行搜索分页
            int i = 0;
            if ("search".equals(order.getTemp())) {
                if (order.getOrderId() == null || order.getOrderId().equals("")) {
                    session.setAttribute("search", order.getIsPayfor());
                } else {
                    session.setAttribute("search", order.getOrderId());
                    i = 1;
                }
                //取出session
                String search = session.getAttribute("search").toString();

                if (i == 2) {
                    order.setOrderId(search);
                } else {
                    order.setIsPayfor(search);
                }
                pageInfo = orderServerce.findOrder(order);
                model.addAttribute("temp", "search");
            } else {
                pageInfo = orderServerce.findOrder(order);
            }

        }

        model.addAttribute("pageInfo", pageInfo);
        logger.info("查询订单结果：-->" + pageInfo);

        model.addAttribute("version", order.getIsPayfor());

        return "commodity/oList";
    }


    //    到订单退货页面
    @RequestMapping("returnOrderList")
    public String returnOrderList(Order order, Model model, HttpSession session) {
        //获取用户Id
        User user = BaseController.getUser();

        //分页按钮有个标识符 如果标识符为0  说明不是搜索结果的分页 是默认所有的结果分页
        order.setPageSize(3);//设置每页多少条
        order.setUserId(user.getuId());

        PageInfo<Order> pageInfo = orderServerce.findReturnOrder(order);

        model.addAttribute("pageInfo", pageInfo);

        return "commodity/returnOrder";
    }


//    退货订单信息详情

    @RequestMapping("/returnOrderDetail")
    @ResponseBody
    public JsonResult returnOrderDetail(ReturnGoods returnGoods) {
        return orderServerce.findReturnGoodsOne(returnGoods);
    }

    //    提交退货申请
    @RequestMapping("addReturnGoods")
    @ResponseBody
    @Mylog(description = "提交退货申请")
    public JsonResult addReturnGoods(ReturnGoods returnGoods, List<MultipartFile> file) throws IOException {
        //退货申请中是否上传了图片
        int size = file.size();
        if (file != null && file.size() > 0) {
            List<String> list = UploadFile.uploadOrtherImg(file);
            if (list.size() > 0) {
                if (list.size() == 1) {
                    returnGoods.setImg1(list.get(0));
                }
                if (list.size() == 2) {
                    returnGoods.setImg1(list.get(0));
                    returnGoods.setImg2(list.get(1));
                }
                if (list.size() == 3) {
                    returnGoods.setImg1(list.get(0));
                    returnGoods.setImg2(list.get(1));
                    returnGoods.setImg3(list.get(2));
                }
            }
        }
        returnGoods.setReturnTime(new Date());
        return orderServerce.addReturnGoods(returnGoods);
    }

    /*查看物流*/
    @RequestMapping("/findLogistics/{id}")
    @ResponseBody
    public JsonResult findLogistics(@PathVariable("id") String id) {
        Logistics logistics = new Logistics();
        logistics.setO_Id(id);
        logger.info(logistics.toString());
        return orderServerce.findOne(id);
    }


}
