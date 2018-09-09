package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class BasePojo implements Serializable{
    //mybatis分页使用
    private int currPage=1;  //当前页
    private int pageSize;//每页多少条

    //layui的分页使用
    private int page; //当前页码
    private int limit;//每页多少条


    //搜索结果分页用，区分是否是搜索后分页，还是默认所有数据分页
    private String temp="0";


    //辅助订单 时间段 查询
    private Date startTime=null;
    private Date endTime=null;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
