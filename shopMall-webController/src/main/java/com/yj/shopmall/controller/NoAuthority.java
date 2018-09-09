package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.ProductServerce;
import com.yj.shopmall.pojo.Product;

import com.yj.shopmall.redis.SessionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/mall")
public class NoAuthority extends BaseController {
    private Logger logger = LoggerFactory.getLogger(NoAuthority.class);
    @Reference
    ProductServerce productServerce;

    @Autowired
    SessionRedis sessionRedis;


    //首页
    @RequestMapping("/")
    @Mylog(description = "浏览所有商品")
    public String toIndex(Model model, HttpSession session) {
        List<Product> products = productServerce.findProductWeb(new Product());
        model.addAttribute("productList", products);
        logger.info(session.getId());
        return "commodity/index";
    }


    @RequestMapping("/toContact")
    public String toContact() {
        return "commodity/contact";
    }

    //商品详情
    @RequestMapping("/toDetails/{productId}")
    @Mylog(description = "浏览商品详情")
    public String toDetails(@PathVariable("productId") String id, Model model) {
        Product productById = productServerce.findProductById(id);
        model.addAttribute("product", productById);
        return "commodity/details";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        if (sessionRedis.isHaveSeesionKey(getSession())) {
            logout();
        }
        return "commodity/login";
    }


    @RequestMapping("/toProducts")
    public String toProducts() {
        return "commodity/products";
    }

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "commodity/register";
    }

    @RequestMapping("/toError")
    public String toError() {
        return "commodity/error";
    }

    @RequestMapping("/toNoAuthority")
    public String toNoAuthority() {
        boolean haveSeesionKey = sessionRedis.isHaveSeesionKey(getSession());
        if (haveSeesionKey) {
            logout();
        }
        return "commodity/NoAuthority";
    }





}
