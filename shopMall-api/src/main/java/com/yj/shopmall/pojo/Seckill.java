package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Seckill implements Serializable{
    private String kill_productId;
    private Date kill_startTime;
    private Date kill_endTime;
    private int isStart;

    private List<Product> productList;
    private Product product;

    public Seckill() {
    }

    public Seckill(String kill_productId, Date kill_startTime, Date kill_endTime, int isStart, List<Product> productList, Product product) {
        this.kill_productId = kill_productId;
        this.kill_startTime = kill_startTime;
        this.kill_endTime = kill_endTime;
        this.isStart = isStart;
        this.productList = productList;
        this.product = product;
    }

    public String getKill_productId() {
        return kill_productId;
    }

    public void setKill_productId(String kill_productId) {
        this.kill_productId = kill_productId;
    }

    public Date getKill_startTime() {
        return kill_startTime;
    }

    public void setKill_startTime(Date kill_startTime) {
        this.kill_startTime = kill_startTime;
    }

    public Date getKill_endTime() {
        return kill_endTime;
    }

    public void setKill_endTime(Date kill_endTime) {
        this.kill_endTime = kill_endTime;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
