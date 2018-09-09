package com.yj.shopmall.pojo;

import java.io.Serializable;

public class ImgLg implements Serializable{
    private String imglg_id;
    private String product_Id;
    private String imglg;
    private int imglgIsdel;

    public String getImglg_id() {
        return imglg_id;
    }

    public void setImglg_id(String imglg_id) {
        this.imglg_id = imglg_id;
    }

    public int getImglgIsdel() {
        return imglgIsdel;
    }

    public void setImglgIsdel(int imglgIsdel) {
        this.imglgIsdel = imglgIsdel;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public String getImglg() {
        return imglg;
    }

    public void setImglg(String imglg) {
        this.imglg = imglg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImgLg{");
        sb.append("imgllg_id='").append(imglg_id).append('\'');
        sb.append(", product_Id='").append(product_Id).append('\'');
        sb.append(", imglg='").append(imglg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
