package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.mapper.ProductMapper;
import com.yj.shopmall.pojo.ImgLLg;
import com.yj.shopmall.pojo.Product;
import com.yj.shopmall.pojo.ProductColor;
import com.yj.shopmall.pojo.ProductSize;
import com.yj.shopmall.service.ProductServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Component
@Service
@Transactional
@CacheConfig(cacheNames = "PROUDUCT-ONE",cacheManager = "myCacheManager")
public class ProductServerceImpl implements ProductServerce {
    private final Logger logger = LoggerFactory.getLogger(ProductServerceImpl.class);
    @Autowired
    ProductMapper productMapper;

    /*
     * 后台获取产品
     * */
    @Override
    public ResultLayui<Product> findProduct(Product product) {
        int page = product.getPage();
        int pageStart = (page - 1) * product.getLimit();
        product.setPage(pageStart);

        List<Product> productList = productMapper.findProduct(product);
        int count = productMapper.findProductCount(product);
        return ResultLayui.jsonLayui(0,"",count,productList);
    }

    /*
     * 前台获取产品
     * */
    @Override
    public List<Product> findProductWeb(Product product) {
        List<Product> productList = productMapper.findProductWeb(product);
        return productList;
    }

    /*
     * 通过id
     * */
    @Cacheable(key = "'['+#productId+']'")
    public Product findProductById(String productId) {
        Product product = productMapper.findProductById(productId);
        return product;
    }

    /**
     * 更改商品信息
     * 即调用方法 又更新缓存数据
     */
    @CachePut(key = "'['+#product.productId+']'")
    public Product updateProduct(Product product) {

        String p_Id = product.getProductId();
        List<ProductSize> sizeList = new ArrayList<ProductSize>();
        List<ProductColor> colorList = new ArrayList<ProductColor>();

        //判断区分前面主页的单个字段修改
        if (product.getColorList() != null) {
            //遍历被选中的大小值
            List<ProductColor> productColorList = new ArrayList<>(product.getColorList());
            //遍历被选中的颜色
            ProductColor productColor1=null;
            for (ProductColor productColor : productColorList) {
                if (productColor == null || productColor.getColor() == null || productColor.getColor().equals("")) {
                    logger.info("颜色");
                } else {
                    if (productColor.getColor() != null && !productColor.getColor().equals("")) {
                        productColor1 = new ProductColor();
                        productColor1.setProduct_Id(p_Id);
                        productColor1.setColor(productColor.getColor());
                        colorList.add(productColor1);
                    }
                }
            }
        }
        if (product.getProductSizeList() != null) {
            List<ProductSize> productSizeList = new ArrayList<>(product.getProductSizeList());

            ProductSize productSize1=null;

            for (ProductSize size : productSizeList) {
                if (size == null || size.getSize() == null || size.getSize().equals("")) {
                    logger.info("");
                } else {
                    if (size.getSize() != null && !size.getSize().equals("")) {
                        productSize1 = new ProductSize();
                        productSize1.setProduct_Id(p_Id);
                        productSize1.setSize(size.getSize());
                        sizeList.add(productSize1);
                    }
                }
            }
        }


        try {
            //先删除原来的，然后插入新的
            if (colorList.size() > 0) {
                productMapper.delProductColor(p_Id);
                productMapper.addProductColor(colorList);
            }
            if (sizeList.size() > 0) {
                productMapper.delProductSize(p_Id);
                productMapper.addProductSize(sizeList);
            }
            productMapper.updateProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("更新失败:" + e);
        }
        return productMapper.findProductById(p_Id);
    }


    @CachePut(key = "'['+#imgLLg.product_Id+']'")
    public Product updateImg_llg(ImgLLg imgLLg) {
        try {
            productMapper.updateImg_llg(imgLLg);
        } catch (Exception e) {
            throw new RuntimeException("更改图片失败" + e);
        }
        return productMapper.findProductById(imgLLg.getProduct_Id());
    }


    /*
     * 添加商品
     * */
    public JsonResult addProduct(Product product) {
        try {
            //先添加商品信息
            int i = productMapper.addProduct(product);


            List<ImgLLg> imgLLgList = product.getImgLLgList();
            List<ProductSize> productSizeList = product.getProductSizeList();
            List<ProductColor> productColorList = product.getColorList();

            List<ProductColor> colorList = new ArrayList<>();
            List<ProductSize> sizeList = new ArrayList<>();
            if (i > 0) {
                ProductSize productSize1=null;
                for (ProductSize productSize : productSizeList) {
                    if (productSize == null || productSize.getSize() == null || productSize.getSize().equals("")) {
                        System.out.println("尺寸");
                    } else {
                        if (productSize.getSize() != null && !productSize.getSize().equals("")) {
                            productSize1 = new ProductSize();
                            productSize1.setProduct_Id(product.getProductId());
                            productSize1.setSize(productSize.getSize());
                            sizeList.add(productSize1);
                        }
                    }
                }
                ProductColor productColor1=null;
                for (ProductColor productColor : productColorList) {
                    if (productColor == null || productColor.getColor() == null || productColor.getColor().equals("")) {
                        System.out.println("颜色");
                    } else {
                        if (productColor.getColor() != null && !productColor.getColor().equals("")) {
                            productColor1 = new ProductColor();
                            productColor1.setProduct_Id(product.getProductId());
                            productColor1.setColor(productColor.getColor());
                            colorList.add(productColor1);
                        }
                    }
                }

                //添加商品颜色
                int I_Color = productMapper.addProductColor(colorList);

                //再添加商品尺寸
                int I_size = productMapper.addProductSize(sizeList);

                if (I_size > 0) {

                    int n = -1;
                    for (ImgLLg imgLLg : imgLLgList) {
                        n++;
                        ImgLLg imgLLg1 = imgLLgList.get(n);
                        imgLLg.setProduct_Id(product.getProductId());
                    }

                    int I_imgllg = productMapper.addProductImg_llg(imgLLgList);
                }
            }

        } catch (Exception e) {
            logger.info("添加商品失败--->" + e);
            throw new RuntimeException(e);
        }
        return JsonResult.build(200, "OK", null);
    }

    /*
     * 上架下架
     * */
    @Override
    public int delProductById(String[] ids, int isdel) {
        try {

            productMapper.delProductById(isdel, ids);
            String pid = "";
            for (int i = 0; i < ids.length; i++) {
                pid = ids[0];
                break;
            }

            ImgLLg imgLLg = new ImgLLg();
            imgLLg.setProduct_Id(pid);
            imgLLg.setImgllgIsdel(isdel);
            productMapper.Img_llgDown(imgLLg);

            ProductSize productSize = new ProductSize();
            productSize.setProduct_Id(pid);
            productSize.setSizeIsdel(isdel);
            productMapper.updateProductSize(productSize);

            ProductColor productColor = new ProductColor();
            productColor.setProduct_Id(pid);
            productColor.setColorIsdel(isdel);
            productMapper.updateProductColor(productColor);

        } catch (Exception e) {
            throw new RuntimeException("删除失败-->" + e);
        }
        return 1;
    }
}
