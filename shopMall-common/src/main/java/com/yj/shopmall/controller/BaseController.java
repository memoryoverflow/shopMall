package com.yj.shopmall.controller;

import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.Utils.getIPAddress;
import com.yj.shopmall.Utils.getOperateSys;
import com.yj.shopmall.pojo.OnLineUser;
import com.yj.shopmall.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.Utils.getBrowser.getBrowser;
import static com.yj.shopmall.constant.Constant.USER_MSG;

public class BaseController {

    //getSubject
    public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    //getUser
    public static User getUser(){
        return (User)getSubject().getPrincipal();
    }

    //getUser
    public static Session getSession(){
        return getSubject().getSession();
    }

    public static void logout(){
         getSubject().logout();
    }

    public static String getUserId(){
        return getUser().getuId();
    }

    public static String getUserName(){
        return getUser().getName();
    }

    /**
     * @Description: 判断是否是ajax请求
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals(httpRequest.getHeader("X-Requested-With").toString()));
    }



    /*
    * 判断图片上传的图片是否相同
    * */
    public static String isSame(MultipartFile file,String oldImgName){
        //新图片类型
        String ct = file.getContentType();
        //要上传的图片类型
        String newImgType = file.getContentType().substring(ct.indexOf("/") + 1);

        //判断旧图片类型是否与新图片相同,截取旧图片类型
        String oldImgType = oldImgName.substring(oldImgName.indexOf(".") + 1, oldImgName.length());

        System.out.println("旧图片名字："+oldImgName);
        System.out.println("上传的图片类型："+ct);
        System.out.println("类型是否相同："+(ct.indexOf(oldImgType)>=0));
        if (ct.indexOf(oldImgType)>=0) {
            //类型相同
            return null;
        }
        return newImgType;
    }





}
