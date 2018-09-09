package com.yj.shopmall.pojo;

import java.io.Serializable;

public class ImgSm implements Serializable {
    private String imgsm_id;
    private String product_Id;
    private String imgsm;
    private int imgsmIsdel;

    public int getImgsmIsdel() {
        return imgsmIsdel;
    }

    public void setImgsmIsdel(int imgsmIsdel) {
        this.imgsmIsdel = imgsmIsdel;
    }

    public String getImgsm_id() {
        return imgsm_id;
    }

    public void setImgsm_id(String imgsm_id) {
        this.imgsm_id = imgsm_id;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public String getImgsm() {
        return imgsm;
    }

    public void setImgsm(String imgsm) {
        this.imgsm = imgsm;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImgSm{");
        sb.append("imgsm_id='").append(imgsm_id).append('\'');
        sb.append(", product_Id='").append(product_Id).append('\'');
        sb.append(", imgsm='").append(imgsm).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
