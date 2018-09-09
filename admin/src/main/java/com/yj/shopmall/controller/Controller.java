package com.yj.shopmall.controller;

import com.yj.shopmall.VerificationCode.ValidateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@org.springframework.stereotype.Controller
public class Controller {
    private Logger logger = LoggerFactory.getLogger(Controller.class);



    @RequestMapping("/index")
    public String index(){
        return "/index";
    }

    @RequestMapping("/welcome.page")
    public String welcome(){
        return "demo/welcome";
    }

    @RequestMapping("toProduct.Page")
    public String toProduc(){
        return "demo/productList";
    }

    @RequestMapping("toOrder.Page")
    public String toOrder(){
        return "demo/orderList";
    }

    @RequestMapping("toUser.Page")
    public String toUser(){
        return "demo/userList";
    }

    @RequestMapping("toLog.Page")
    public String toLog(){
        return "demo/log-tree-table";
    }

    @RequestMapping("toPublisher.Page")
    public String toPublisher(){
        return "demo/publisher";
    }

    @RequestMapping("toSystemMonitor.Page")
    public String toSystemMonitor(){
        return "demo/onlineUserList.html";
    }

    @RequestMapping("toSeckillManager.Page")
    public String toSeckillManager(){
        return "demo/seckillManager.html";
    }

    @RequestMapping("toRole.Page")
    public String toRole(){
        return "demo/role.html";
    }

    @RequestMapping("toUserMsg.Page")
    public String toUserMsg(){
        return "demo/userMsg";
    }



//    login登录
    @RequestMapping("/login.Page")
    public String toLoginPage(){
        return "login";
    }

//    获取验证码
    @RequestMapping("/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            ValidateCodeUtil randomValidateCode = new ValidateCodeUtil();
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            logger.error("获取验证码失败>>>>   ", e);
        }
    }

}
