package com.yj.shopmall.pojo;

import java.io.Serializable;

public class ProductSize implements Serializable {
    private String sizeId;
    private String product_Id;
    private Double size;
    private int sizeIsdel;

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public int getSizeIsdel() {
        return sizeIsdel;
    }

    public void setSizeIsdel(int sizeIsdel) {
        this.sizeIsdel = sizeIsdel;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProductSize{");
        sb.append("product_Id='").append(product_Id).append('\'');
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
