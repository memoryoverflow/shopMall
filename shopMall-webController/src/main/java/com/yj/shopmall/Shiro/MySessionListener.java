package com.yj.shopmall.Shiro;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.controller.OrderController;
import com.yj.shopmall.service.UserServerce;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySessionListener extends SessionListenerAdapter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OrderController.class);

     @Reference
     UserServerce userServerce;

    public MySessionListener() {
    }

    @Override
    public void onStop(Session session) {
        logger.info("session监听，session停止=[{}]",session.getId().toString());
    }

    //会话过期时触发
    @Override
    public void onExpiration(Session session) {
        String S_id=session.getId().toString();
        String[] id=new String[]{S_id};
        userServerce.delOnlineUser(id);
        logger.info("会话过期：" + session.getId() + ":" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
