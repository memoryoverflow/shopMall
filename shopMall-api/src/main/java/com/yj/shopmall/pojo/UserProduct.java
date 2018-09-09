package com.yj.shopmall.pojo;

import java.io.Serializable;

public class UserProduct implements Serializable{
    private String up_order_Id;
    private String up_userId;
    private String up_name;
    private String up_productId;
    private Double up_price;
    private Double up_size;
    private Integer up_num;
    private double up_totalprice;
    private String up_color;
    private String up_img;
    private int isdel;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserProduct{");
        sb.append("up_userId='").append(up_userId).append('\'');
        sb.append(", up_name='").append(up_name).append('\'');
        sb.append(", up_productId='").append(up_productId).append('\'');
        sb.append(", up_price=").append(up_price);
        sb.append(", up_size=").append(up_size);
        sb.append(", up_num=").append(up_num);
        sb.append(", up_totalprice=").append(up_totalprice);
        sb.append(", up_color='").append(up_color).append('\'');
        sb.append(", up_img='").append(up_img).append('\'');
        sb.append(", isdel=").append(isdel);
        sb.append('}');
        return sb.toString();
    }



    public String getUp_order_Id() {
        return up_order_Id;
    }

    public void setUp_order_Id(String up_order_Id) {
        this.up_order_Id = up_order_Id;
    }

    public String getUp_userId() {
        return up_userId;
    }

    public void setUp_userId(String up_userId) {
        this.up_userId = up_userId;
    }

    public String getUp_name() {
        return up_name;
    }

    public void setUp_name(String up_name) {
        this.up_name = up_name;
    }

    public String getUp_productId() {
        return up_productId;
    }

    public void setUp_productId(String up_productId) {
        this.up_productId = up_productId;
    }

    public Double getUp_price() {
        return up_price;
    }

    public void setUp_price(Double up_price) {
        this.up_price = up_price;
    }

    public Double getUp_size() {
        return up_size;
    }

    public void setUp_size(Double up_size) {
        this.up_size = up_size;
    }

    public Integer getUp_num() {
        return up_num;
    }

    public void setUp_num(Integer up_num) {
        this.up_num = up_num;
    }

    public double getUp_totalprice() {
        return up_totalprice;
    }

    public void setUp_totalprice(double up_totalprice) {
        this.up_totalprice = up_totalprice;
    }

    public String getUp_color() {
        return up_color;
    }

    public void setUp_color(String up_color) {
        this.up_color = up_color;
    }

    public String getUp_img() {
        return up_img;
    }

    public void setUp_img(String up_img) {
        this.up_img = up_img;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }
}