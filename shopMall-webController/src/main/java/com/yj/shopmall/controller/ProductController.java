package com.yj.shopmall.controller;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.ProductServerce;
import com.yj.shopmall.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@org.springframework.stereotype.Controller
@RequestMapping("product")
public class ProductController {
    private Logger logger= LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductServerce productServerce;

    @RequestMapping("/list")
    @ResponseBody
    @Mylog(description = "浏览商品")
    public JsonResult producList(Model model){
        List<Product> product = productServerce.findProductWeb(new Product());

        logger.info(String.valueOf(product.size()));
        return JsonResult.ok(product);

    }



}
