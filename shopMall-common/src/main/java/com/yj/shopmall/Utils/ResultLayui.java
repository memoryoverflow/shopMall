package com.yj.shopmall.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class ResultLayui<T> implements Serializable{

    /*
    *  code:返回状态码
    *  msg: 消息提示
    * data：返回的数据
    * 格式{code:200,msg:"成功",data:[{}]};
    * */
    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer code;

    // 响应消息
    private String msg;

    private int count;

    // 响应中的数据
    private Object data;

    public static ResultLayui jsonLayui(Integer code, String msg, int count, Object data) {
        return new ResultLayui(code, msg,count, data);
    }



    public ResultLayui() {

    }


    public ResultLayui(Integer code, String msg, int count, Object data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResultLayui{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", count=").append(count);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
