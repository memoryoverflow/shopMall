package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class OnLineUser extends BasePojo implements Serializable {
    private String opId;
    private String id;
    private String name;
    private String ip;
    private String address;
    private String browser;
    private String system;
    private Date time;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OnLineUser{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", browser='").append(browser).append('\'');
        sb.append(", system='").append(system).append('\'');
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
