package com.yj.shopmall.Utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public  class encryption {
    public  static  Object getMD5(String pwd,String email){
        //加密方式
        String hashAlgorithmName = "MD5";
        //密码
        Object credentials = pwd;
        //使用ByteSource.Util.bytes()计算盐值
        Object salt = ByteSource.Util.bytes(email);
        //加密次数
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
        return result;
    }

    public static void main(String []args){
        //加密方式
        String hashAlgorithmName = "MD5";
        //密码
        Object credentials = "123456";
        //使用ByteSource.Util.bytes()计算盐值
        Object salt = ByteSource.Util.bytes("admin@qq.com");
        //加密次数
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
        System.out.println(result);
    }

}
