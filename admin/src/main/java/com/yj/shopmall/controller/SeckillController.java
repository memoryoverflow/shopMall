package com.yj.shopmall.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.FtpUtil;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.Utils.UploadFile;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.redis.SeckillRedis;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    private String responseMsg = "";

    @Reference
    SeconkillServerce seconkillServerce;

    @Autowired
    Sid sid;

    @Reference
    ProductServerce productServerce;


    @Autowired
    SeckillRedis seckillRedis;

    /*
     * 订单秒杀页面
     * */
    @RequestMapping("/{productId}")
    @Mylog(description = "查看秒杀")
    public String toDetail(Model model, @PathVariable("productId") String pid) {
        logger.info("订单秒杀详情页面");
        Seckill productById = seconkillServerce.findProductById(pid);
        logger.info(JsonUtils.objectToJson(productById));
        model.addAttribute("product", productById);
        return "commodity/SeckillDetails";
    }


    /*
     * 执行秒杀
     * */
    @RequestMapping("/seckilling")
    @ResponseBody
    @Mylog(description = "执行秒杀")
    public JsonResult seckill(Order order, HttpSession session) {

        User user = (User) session.getAttribute("user_Session");
        order.setUserId(user.getuId());//用户Id
        order.setOrderId(sid.nextShort());//订单id 随机
        order.setCreateTime(new Date());//下单时间

        //用集合的原因 每笔订单中 会有多个商品 将多个商品批量入库
        String productId = "";
        List<UserProduct> userProducts = order.getUserProducts();
        for (UserProduct userProduct : userProducts) {
            userProduct.setUp_order_Id(order.getOrderId());
            userProduct.setUp_userId(order.getUserId());
            productId = userProduct.getUp_productId();
        }
        JsonResult jsonResult = seconkillServerce.reduceStock(order, productId);


        return jsonResult;
    }


    /**
     * 执行秒杀入口
     */
    @RequestMapping("/seckilling/{productId}")
    @ResponseBody
    public JsonResult test(@PathVariable("productId") String pId, HttpSession session) {
        logger.info("秒杀请求");
        User user_session = (User) session.getAttribute("user_Session");
        UserProduct uP = new UserProduct();

        //随机生成用户id,模拟并发
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 15).replace("-", "");

        Order order = new Order();

        order.setOrderId(sid.nextShort());
        order.setTotalPrice(10.0);
        order.setAddress_Id("1");


        //生成订单消息，模拟前台传过来的订单
        Map<String, Object> map = new HashMap();

        uP.setUp_name("1");
        uP.setUp_price(10.0);
        uP.setUp_color("红色");
        uP.setUp_img("/images/product-slide/1-1-lg.jpg");
        uP.setUp_totalprice(10.0);

        uP.setUp_userId(uid);

        uP.setUp_size(40.5);
        uP.setUp_num(1);
        uP.setUp_productId(pId);

        order.setUserProduct(uP);

        String up_productId = order.getUserProduct().getUp_productId();
        String orderMsg = JsonUtils.objectToJson(order);

        JsonResult jsonResult = seconkillServerce.reduceStock(order, pId);


        return jsonResult;
    }


    /*
     * 是否开始秒杀
     * */
    @RequestMapping("/isStart/{id}")
    @ResponseBody
    public JsonResult isStart(@PathVariable("id") String productId) {
        JsonResult startSecKill = seconkillServerce.isStartSecKill(productId);
        return startSecKill;
    }


    /*
     * 获取系统时间
     * */
    @RequestMapping("/time/now")
    @ResponseBody
    public JsonResult time() {
        Date nowTime = new Date();
        JsonResult jsonResult = seconkillServerce.selectKillId();
        Object data = jsonResult.getData();
        if (data == null || data.equals("")) {
            data = "null";
        }
        logger.info("请求时间判断，time=[{}]", new Date());
        return JsonResult.build(200, data.toString(), nowTime.getTime());
    }


    /*
     * 获取抢购商品入口
     * */
    @RequestMapping("/getProductId")
    @ResponseBody
    public JsonResult getKillProductId() {
        JsonResult jsonResult = seconkillServerce.selectKillId();
        return jsonResult;
    }


    //更换logo
    @RequestMapping("changeLogo")
    @ResponseBody
    @Mylog(description = "更换秒杀LOGO")
    public JsonResult changeLogo(List<MultipartFile> file, String id, String imgOldName, HttpServletRequest request)
            throws IOException, InterruptedException {
//        final String filePath = "http://106.14.226.138:7777/";
//        Boolean b = false;
//        //全部更换，要么单张更换
//        if (file.size() == 3){
//            String[] split = imgOldName.split(",");
//            List<String> objects = Arrays.asList(split);;
//            //更换全部
//            List<String> list = UploadFile.SeckillImgUpload(file, id, null);
//            if (list.size() > 0) {
//
//                //更新数据库地址
//                SeckillExplain seckillExplain = new SeckillExplain();
//                seckillExplain.setImg1(filePath+list.get(0));
//                seckillExplain.setImg2(filePath+list.get(1));
//                seckillExplain.setImg3(filePath+list.get(2));
//                seckillExplain.setId(id);
//                seconkillServerce.updateExplain(seckillExplain);
//                b = true;
//
//                //删掉旧图片
//                FtpUtil.deleteFile(objects);
//            }
//
//        } else {
//            //单张更换
//            Ueditor ueditor = new Ueditor();
//
//            String newFileType = BaseController.isSame(file.get(0), imgOldName);
//
//            if (newFileType==null) {
//                //图片类型相同,不更换数据库的
//                ueditor = UploadFile.uploadImgUEditor(file.get(0), imgOldName, request);
//            }else{
//                //截取就图片 名字 不带后缀
//                String oldName = imgOldName.substring(0,imgOldName.indexOf("."));
//                //生成新图片名字
//                String newImgPath =  oldName + "." + newFileType;
//
//                //上传
//                ueditor = UploadFile.uploadImgUEditor(file.get(0), newImgPath, request);
//
//                //更新数据库
//                SeckillExplain seckillExplain = new SeckillExplain();
//
//                //更换第一张
//                if (oldName.indexOf("A")>=0) {
//                    seckillExplain.setImg1(filePath+newImgPath);
//                } else if (oldName.indexOf("B")>=0) {
//                    //更换第二张
//                    seckillExplain.setImg2(filePath+newImgPath);
//                }else{
//                    //更换第三张
//                    seckillExplain.setImg3(filePath+newImgPath);
//                }
//                seconkillServerce.updateExplain(seckillExplain);
//
//                List<String> list=new ArrayList<>();
//                list.add(imgOldName);
//                FtpUtil.deleteFile(list);
//            }
//
//            if ("SUCCESS".equals(ueditor.getState())) {
//                b = true;
//            }
//        }
//        if (b) {
//            return JsonResult.ok();
//        }
//        return JsonResult.errorMsg("false");
        return JsonResult.ok();
    }

    //添加秒杀的还是拿个屁以及相关信息
    @RequestMapping("addSeckillExplain")
    @ResponseBody
    public JsonResult addSeckillExplain(List<MultipartFile> maxfile, SeckillExplain explain,
                                        Seckill seckill) throws IOException, InterruptedException {

        //只能添加一个活动商品，不然就先删除，或者更改活动产品；
//        JsonResult jsonResult = seconkillServerce.selectKillId();
//        if (jsonResult.getData() != null) {
//            return JsonResult.errorMsg("false");
//        }
//
//
//        //上传图片
//        List<String> list = UploadFile.SeckillImgUpload(maxfile, explain.getId(), null);
//        if (list.size() > 0) {
//
//            explain.setImg3(list.get(2));
//            explain.setImg2(list.get(1));
//            explain.setImg1(list.get(0));
//            int i = seconkillServerce.addExplain(explain);
//
//            //Seckill seckill=new Seckill();
//
//            seckill.setKill_productId(explain.getId());
//            seconkillServerce.addSeckill(seckill);
//            return JsonResult.ok();
//        }
//        return JsonResult.errorMsg("图片上传失败");
        return JsonResult.ok();
    }

    //修改活动封面描述文字
    @RequestMapping("/updateSeckillExplain")
    @ResponseBody
    @Mylog(description = "修改活动封面描述文字")
    public JsonResult updateSeckillExplain(SeckillExplain explain) {
        //int i = seconkillServerce.updateExplain(explain);
        return JsonResult.ok();
    }

    /*
     * 修改库存
     * */
    @RequestMapping("/updateSeckillProductStock")
    @ResponseBody
    @Mylog(description = "修改库存")
    public JsonResult updateSeckillProductStock(String id, Integer num) {
//        Product product = new Product();
//        product.setNum(num);
//        product.setProductId(id);
//        productServerce.updateProduct(product);
//        seckillRedis.addStock(id, num);
        return JsonResult.ok();
    }


    /*
     * 修改秒杀商品的id
     * */
    @RequestMapping("/updateSeckillProductId")
    @ResponseBody
    @Mylog(description = "修改秒杀商品")
    public JsonResult updateSeckillProductId(String id) {
        //seconkillServerce.updateSeckillProductId(id);
        return JsonResult.ok();
    }


    //获取秒杀封面的信息
    @RequestMapping("/getSeckillExplainMsg")
    @ResponseBody
    public JsonResult<SeckillExplain> getSeckillExplainMsg() {
        JsonResult explain = seconkillServerce.findExplain();
        return explain;
    }

    //获取秒杀封面的信息
    @RequestMapping("/getSeckillPrroductStock/{id}")
    @ResponseBody
    public JsonResult<SeckillExplain> getSeckillPrroductStock(@PathVariable("id") String id) {
        JsonResult num = seconkillServerce.slectNumById(id);
        return num;
    }


}
