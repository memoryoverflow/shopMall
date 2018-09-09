package com.yj.shopmall.Shiro;

import com.alibaba.druid.support.json.JSONUtils;
import com.yj.shopmall.controller.BaseController;
import com.yj.shopmall.controller.OrderController;
import com.yj.shopmall.pojo.User;
import com.yj.shopmall.redis.MyRedis;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.yj.shopmall.constant.Constant.REDIS_SESSION_Prefix;
import static com.yj.shopmall.constant.Constant.USER_ISACTIVATED;
import static com.yj.shopmall.constant.Constant.USER_ISFROZEN;

public class ShiroRoleFilter extends RolesAuthorizationFilter {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MyRedis myRedis;


    public static void out(ServletResponse response, Map<String, String> resultMap) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");//设置编码
            response.setContentType("application/json");//设置返回类型
            out = response.getWriter();
            out.println(JSONUtils.toJSONString(resultMap));//输出
//            logger.info(JSONUtils.toJSONString(resultMap));
        } catch (Exception e) {
            logger.info(e + "输出JSON报错。");
        } finally {
            out.flush();
            out.close();
        }
    }

    //先调用isAccessAllowed，如果返回的是true，则直接放行执行后面的filter和servlet，如果返回的是false，
    // 则继续执行后面的onAccessDenied方法，如果后面返回的是true则也可以有权限继续执行后面的filter和servelt。
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        //return super.onAccessDenied(request, response, mappedValue);
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse reqs = (HttpServletResponse) response;
        //判断是否登录
        Subject subject = getSubject(request, response);
        boolean isAuthc = subject.isAuthenticated();
        String requestURI = req.getRequestURI();
        //拦截登录请求 其它有 loginfiter请求
        if (BaseController.isAjax(req)) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("status", "403");
            resultMap.put("msg", "*对不起，权限不足");
//            logger.info("### 角色过滤ajax请求！");
            out(response, resultMap);
            return false;
        }

        WebUtils.issueRedirect(req, reqs, "/mall/toNoAuthority");
        return false;

    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse reqs = (HttpServletResponse) response;
        //取到参数[admin,common] ，强制转换判断。
        String[] arra = (String[]) mappedValue;
//        logger.info("### 角色过滤拦截,需要拥有的角色={}", Arrays.toString(arra));
        Subject subject = getSubject(request, response);
        User principal = (User) subject.getPrincipal();
        if (principal != null) {
            if (req.getRequestURI().indexOf("/mall/toNoAuthority")>=0) {
                return true;
            }
            for (String role : arra) {
                //判断是否有拥有当前权限，有则返回true
                if (subject.hasRole(role)) {
//                    logger.info("### 角色验证通过 ###" + principal.getRole().getRoleChar());
//                    logger.info("### 当前用户拥有的角色=[{}]", role);
                    return true;
                }
            }
//            logger.info("### 角色验证未通过");
            return false;
        }
        return true;
    }
}
