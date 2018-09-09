
package com.yj.shopmall.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.*;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.ImgLLg;
import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.Ueditor;
import com.yj.shopmall.pojo.User;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/mall")
public class ProductController {
    private Logger logger = LoggerFactory.getLogger(ProductController.class);

    //调用接口
    @Reference
    ProductServerce productServerce;

    //添加商品
    @RequestMapping("/addProduct")
    @Mylog(description = "添加商品")
    @ResponseBody
    public JsonResult addProduct(
            Product product,
            MultipartFile uploadfile,//主图片
            List<MultipartFile> maxfile,  //大图片
            HttpServletRequest request) {

        JsonResult jsonResult = new JsonResult();

        //如果直接 maxfile.add(uploadfile);会报错 UnsupportedOperationException
        //返回的市Arrays的内部类ArrayList， 而不是java.util.ArrayList
        List<MultipartFile> fileList = new ArrayList<>(maxfile);
        fileList.add(uploadfile);

        System.out.println("图片个数为：" + fileList.size());
        try {
            String p_Id = UUID.randomUUID().toString().replace("-", "");
            List<String> imgNameList = UploadFile.uploadImg(fileList, p_Id, request);
            //图片上传成功
            if (imgNameList.size() > 0) {

                List<ImgLLg> imgLLgList = new ArrayList<>();
                ImgLLg imgLLg = null;
                for (int i = 0; i < imgNameList.size(); i++) {
                    if (i == imgNameList.size() - 1) {
                        //取出主图片名字
                        product.setImgMain(imgNameList.get(imgNameList.size() - 1));
                    } else {
                        imgLLg = new ImgLLg();
                        imgLLg.setProduct_Id(p_Id);
                        imgLLg.setImgllg(imgNameList.get(i));
                        imgLLgList.add(imgLLg);
                    }
                }

                product.setImgLLgList(imgLLgList);
                product.setProductId(p_Id);
                jsonResult = productServerce.addProduct(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonResult;
    }

    //产品列表
//    @RequestMapping("/productList")
//    @Mylog(description = "查询商品列表")
//    public String productList(Model model) {
//        List<Product> product1 = productServerce.findProductWeb(new Product());
//        model.addAttribute("ProductList", product1);
//        return "demo/productList";
//    }

    //产品列表
    @RequestMapping("/productList")
    @Mylog(description = "查询商品列表")
    @ResponseBody
    public ResultLayui productListTest(Product product, Model model) {
        ResultLayui<Product> product1 = new ResultLayui<>();
        if (product == null) {
            product1 = productServerce.findProduct(new Product());
        } else {
            product1 = productServerce.findProduct(product);

        }
        logger.info(product.toString());
        return product1;
    }


    //上架 下架
    @RequestMapping("delProductByd")
    @Mylog(description = "删除商品")
    @ResponseBody
    public JsonResult delProductByd(String[] ids, int isdel, Model model) {
        //不允许逻辑操作
//        productServerce.delProductById(ids, isdel);
//        for (String i : ids) {
//            logger.info(i);
//        }
        return JsonResult.ok();
    }


    //修改
    @RequestMapping("/updateProductById")
    @Mylog(description = "更新商品")
    @ResponseBody
    public JsonResult updateProductById(Product product, String productStr, Model model) {
        logger.info("修改商品：" + JsonUtils.objectToJson(product));
//        if (productStr == null || productStr.equals("")) {
//            productServerce.updateProduct(product);
//        } else {
//            Product product1 = JsonUtils.jsonToPojo(productStr, Product.class);
//            productServerce.updateProduct(product1);
//            logger.info(product1.toString());
//        }
        return JsonResult.ok();
    }

    //修改图片
    @RequestMapping("/updateImgMain/{version}")
    @ResponseBody
    public JsonResult updateProductMainImg(@PathVariable("version") String version,
                                           String oldImgPath,
                                           String productId,
                                           MultipartFile file,
                                           HttpServletRequest request,
                                           String oldImgId) throws IOException {
        //先生成文件名
//        final String filePath = "http://106.14.226.138:7777/";
//
//        List<String> fileNameList = new ArrayList<>();
//        Ueditor ueditor = new Ueditor();
//
//        String newFileType = BaseController.isSame(file, oldImgPath);
//
//
//
//        if (newFileType==null) {
//            //图片类型相同 无需更换
//            //不必要更新数据库，上传文件直接更新 vsftb服务器上的图片即可，图片名字不变
//
//            //更新服务器上的图片
//            fileNameList.add(oldImgPath);
//            ueditor = UploadFile.uploadImgUEditor(file, oldImgPath, request);
//
//        } else {
//            //删除的旧图片地址
//            String delImg = oldImgPath;
//            //获取图片的名字 不带后缀
//            String imgName= oldImgPath.substring(0, oldImgPath.indexOf("."));
//            //生成新图片名字
//            String newImgPath = imgName + "." + newFileType;
//
//            //上传
//            ueditor = UploadFile.uploadImgUEditor(file, newImgPath, request);
//            //更新数据库图片路径
//            if ("0".equals(version)) {
//                Product product = new Product();
//                product.setProductId(productId);
//                product.setImgMain(filePath + newImgPath);
//                productServerce.updateProduct(product);
//            } else{
//                ImgLLg imgLLg = new ImgLLg();
//                imgLLg.setProduct_Id(productId);
//                imgLLg.setImgllg_id(oldImgId);
//                imgLLg.setImgllg(filePath+newImgPath);
//                productServerce.updateImg_llg(imgLLg);
//            }
//
//            //删除旧图片
//            List<String> strings = new ArrayList<>();
//            strings.add(delImg);
//            FtpUtil.deleteFile(strings);
//        }
//
//
//        if (ueditor.getState().equals("SUCCESS")) {
//            return JsonResult.ok();
//        }
//        return JsonResult.errorMsg("false");
        return JsonResult.ok();

    }


    //商品详情
    @RequestMapping("/toDetails")
    @ResponseBody
    public JsonResult toDetails(String productId, Model model) {
        Product productById = productServerce.findProductById(productId);

        return JsonResult.ok(productById);
    }


    @RequestMapping("/getProductAllId")
    @ResponseBody
    public JsonResult producList(Model model) {
        List<Product> product = productServerce.findProductWeb(new Product());
        logger.info(String.valueOf(product.size()));
        return JsonResult.ok(product);
    }


}
