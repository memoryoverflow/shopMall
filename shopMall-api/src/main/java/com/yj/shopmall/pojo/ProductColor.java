package com.yj.shopmall.pojo;

import java.io.Serializable;

public class ProductColor implements Serializable{
    private String colorId;
    private String product_Id;
    private String color;
    private int colorIsdel;

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public int getColorIsdel() {
        return colorIsdel;
    }

    public void setColorIsdel(int colorIsdel) {
        this.colorIsdel = colorIsdel;
    }

    public String getProduct_Id() {
        return product_Id;
    }

    public void setProduct_Id(String product_Id) {
        this.product_Id = product_Id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProductColor{");
        sb.append("product_Id='").append(product_Id).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
