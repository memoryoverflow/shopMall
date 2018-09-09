package com.yj.shopmall.pojo;



import com.yj.shopmall.utils.MyStringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class OperateLog extends BasePojo implements Serializable {
    private String id;
    private String logtype; //日志类型
    private String operateType; //操作标题
    private String host;  //主机
    private String address;  //主机
    private String url;
    private String method; //方法
    private String params;  //参数
    private String  exception; //异常信息
    private Date time; //请求时间
    private String  user; //用户




    /**
     * 设置请求参数
     * @param paramMap
     */
    public void setMapToParams(Map<String, String[]> paramMap) {
        if (paramMap == null){
            return;
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(MyStringUtils.abbr(MyStringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        this.params = params.toString();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OperateLog{");
        sb.append("id='").append(id).append('\'');
        sb.append(", logtype='").append(logtype).append('\'');
        sb.append(", operateType='").append(operateType).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", method='").append(method).append('\'');
        sb.append(", params='").append(params).append('\'');
        sb.append(", exception='").append(exception).append('\'');
        sb.append(", time=").append(time);
        sb.append(", user='").append(user).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
