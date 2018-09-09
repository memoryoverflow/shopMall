package com.yj.shopmall.pojo;

import java.io.Serializable;

public class ImgLLg implements Serializable {
    private String product_Id;
    private String imgllg_id;
    private String imgllg;
    private int imgllgIsdel;

    public int getImgllgIsdel() {
        return imgllgIsdel;
    }

    public void setImgllgIsdel(int imgllgIsdel) {
        this.imgllgIsdel = imgllgIsdel;
    }

    public String getImgllg_id() {
        return imgllg_id;
    }

    public void setImgllg_id(String imgllg_id) {
        this.imgllg_id = imgllg_id;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public String getImgllg() {
        return imgllg;
    }

    public void setImgllg(String imgllg) {
        this.imgllg = imgllg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImgLLg{");
        sb.append("product_Id='").append(product_Id).append('\'');
        sb.append(", imgllg_id='").append(imgllg_id).append('\'');
        sb.append(", imgllg='").append(imgllg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
