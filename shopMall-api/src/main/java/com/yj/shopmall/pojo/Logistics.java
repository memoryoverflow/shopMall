package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class Logistics extends BasePojo implements Serializable{
    private String id;
    private String o_Id;
    private String content;
    private String onsignor;
    private String onsignor_tel;
    private String shipAddress;
    private String consignee;
    private String consignee_tel;
    private String recAddress;
    private int status;
    private Date cTime;

    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }

    public String getO_Id() {
        return o_Id;
    }

    public void setO_Id(String o_Id) {
        this.o_Id = o_Id;
    }

    public String getOnsignor() {
        return onsignor;
    }

    public void setOnsignor(String onsignor) {
        this.onsignor = onsignor;
    }

    public String getOnsignor_tel() {
        return onsignor_tel;
    }

    public void setOnsignor_tel(String onsignor_tel) {
        this.onsignor_tel = onsignor_tel;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsignee_tel() {
        return consignee_tel;
    }

    public void setConsignee_tel(String consignee_tel) {
        this.consignee_tel = consignee_tel;
    }

    public String getRecAddress() {
        return recAddress;
    }

    public void setRecAddress(String recAddress) {
        this.recAddress = recAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Logistics{");
        sb.append("id='").append(id).append('\'');
        sb.append(", o_Id='").append(o_Id).append('\'');
        sb.append(", onsignor='").append(onsignor).append('\'');
        sb.append(", onsignor_tel='").append(onsignor_tel).append('\'');
        sb.append(", shipAddress='").append(shipAddress).append('\'');
        sb.append(", consignee='").append(consignee).append('\'');
        sb.append(", consignee_tel='").append(consignee_tel).append('\'');
        sb.append(", recAddress='").append(recAddress).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
