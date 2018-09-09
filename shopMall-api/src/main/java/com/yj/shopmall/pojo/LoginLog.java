package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class LoginLog extends BasePojo implements Serializable{
    private String id;
    private String name; //日志类型
    private String ip; //操作标题
    private String address;  //主机
    private String browser; //方法
    private String status; //方法
    private String system; //方法
    private String  msg; //异常信息
    private Date time; //请求时间

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginLog{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", browser='").append(browser).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", system='").append(system).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
