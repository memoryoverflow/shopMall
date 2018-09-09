package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.List;

public class Product extends BasePojo implements Serializable{
    private String productId;
    private String name;
    private Double price;
    private Integer num;
    private String describes;
    private String detail;
    private String imgMain;
    private Integer isdel;

    /*private List<ImgSm> imgSmList;
    private List<ImgLg> imgLgList;*/
    private List<ImgLLg> imgLLgList;
    private List<ProductColor> colorList;
    private List<ProductSize> productSizeList;

    public Product() {
    }

    public Product( Integer num,String productId) {
        this.productId = productId;
        this.num = num;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Product{");
        sb.append("productId='").append(productId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", price=").append(price);
        sb.append(", num=").append(num);
        sb.append(", describes='").append(describes).append('\'');
        sb.append(", detail='").append(detail).append('\'');
        sb.append(", imgMain='").append(imgMain).append('\'');
        sb.append(", isdel=").append(isdel);
        sb.append(", imgLLgList=").append(imgLLgList);
        sb.append(", colorList=").append(colorList);
        sb.append(", productSizeList=").append(productSizeList);
        sb.append('}');
        return sb.toString();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImgMain() {
        return imgMain;
    }

    public void setImgMain(String imgMain) {
        this.imgMain = imgMain;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    public List<ImgLLg> getImgLLgList() {
        return imgLLgList;
    }

    public void setImgLLgList(List<ImgLLg> imgLLgList) {
        this.imgLLgList = imgLLgList;
    }

    public List<ProductColor> getColorList() {
        return colorList;
    }

    public void setColorList(List<ProductColor> colorList) {
        this.colorList = colorList;
    }

    public List<ProductSize> getProductSizeList() {
        return productSizeList;
    }

    public void setProductSizeList(List<ProductSize> productSizeList) {
        this.productSizeList = productSizeList;
    }
}
