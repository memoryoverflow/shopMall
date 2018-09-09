package com.yj.shopmall.Shiro;


import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;


@Component
public class FilterChainDefinitionMapBuider {
    public LinkedHashMap<String, String> BuiderFilterChainDefinitionMap() {
        //LinkedHashMap是有序的
        LinkedHashMap<String, String> ListMap = new LinkedHashMap<>();
         /**  配置受保护页面 就是无需验证权限的页面 以及 需要权限访问的面
         *   1) anon 可以匿名访问 无需登陆验证
         *   2）authc 必须要权限认证才可以访问
         *   3) logout 登出过滤器
         **/
        //不拦截静态资源
        ListMap.put("/js/**.js","anon");
        ListMap.put("/css/**.css","anon");
        ListMap.put("/images/product-slide/**.jpg","anon");
        ListMap.put("/images/product-slide/**.png","anon");
        ListMap.put("/images/**.gif","anon");
        ListMap.put("/images/**.jpg","anon");
        ListMap.put("/images/**.png","anon");
        ListMap.put("/layui/**.ttf","anon");
        ListMap.put("/layui/**.woff","anon");


        //不拦截商品下的页面
        ListMap.put("/","anon");
        ListMap.put("/mall/**","anon");

        ListMap.put("/user/**.do","anon");
        ListMap.put("/user/active.do/**","anon");
        //获取页面秒杀封面图片
        ListMap.put("/seckill/getSeckillExplainMsg","anon");
        //不拦截，处理登陆逻辑的请求
        ListMap.put("/user/login.do", "anon");
        //登出
        ListMap.put("/user/logout.do", "logout");

        //设置所有资源需要访问权限
        ListMap.put("/**", "authc,roles[admin,common]");
        return ListMap;
    }


}
