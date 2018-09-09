package com.yj.shopmall.Shiro;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.controller.BaseController;
import com.yj.shopmall.controller.OrderController;
import com.yj.shopmall.pojo.User;
import com.yj.shopmall.redis.MyRedis;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.yj.shopmall.constant.Constant.*;

public class LoginFilter extends AdviceFilter {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    MyRedis myRedis;


    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        super.postHandle(request, response);
    }

    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        super.afterCompletion(request, response, exception);
    }

    @Override
    protected boolean preHandle(ServletRequest req, ServletResponse resp) throws Exception {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //获取请求头中的信息，ajax请求最大的特点就是请求头中多了 X-Requested-With 参数
        String requestType = request.getHeader("X-Requested-With");
        Subject subject = BaseController.getSubject();

        if (BaseController.isAjax(request)) {
//            logger.info("拦截URL=[{}]", request.getRequestURI());
            boolean isAuthc = subject.isAuthenticated();
            Map<String, String> resultMap = new HashMap<String, String>();
            if (!isAuthc) {
                if (request.getRequestURI().indexOf("login.do")>=0) {
                    return true;
                }else {
                    resultMap.put("status", "403");
                    resultMap.put("msg",
                            "未登录/会话过期/被强退，请从新登录！");// 当前用户没有登录！
                    out(response, resultMap);
                    //如果当前账户没有被认证,本次请求被驳回，ajax进入error的function中进行重定向到登录界面。
                    return false;
                }
            }else {
                String userId = BaseController.getUserId();
                String sessionId = subject.getSession().getId().toString();
                boolean b = myRedis.isHaveUserFromRedis(userId);
                if (b) {
                    User user = myRedis.getUserFromRedis(userId);
                    logger.info(user.toString());
                    //未激活
                    if (user.getIsActivated().equals(String.valueOf(USER_ISACTIVATED))) {
                        resultMap.put("status", "403");
                        resultMap.put("msg", "账户已被锁定");
                        out(response, resultMap);
                        myRedis.delUserFromRedis(userId);
                        //stringRedisTemplate.delete(REDIS_SESSION_Prefix + sessionId);
                        //强制下线
                        BaseController.logout();
                        return false;
                    }
                    //账户冻结
                    if (user.getIsFrozen().equals(String.valueOf(USER_ISFROZEN))) {
                        resultMap.put("status", "403");
                        resultMap.put("msg", "账户已被冻结");
                        out(response, resultMap);
                        myRedis.delUserFromRedis(userId);
                        //强制下线
                        //stringRedisTemplate.delete(REDIS_SESSION_Prefix + sessionId);
                        BaseController.logout();
                        return false;
                    }
                    myRedis.updateTimeUserToRedis(userId);
                    return true;

                }
                return true;
            }
        }

        return true;
    }


    public static void out(ServletResponse response, Map<String, String> resultMap) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");//设置编码
            response.setContentType("application/json");//设置返回类型
            out = response.getWriter();
            out.println(JSONUtils.toJSONString(resultMap));//输出
            logger.info(JSONUtils.toJSONString(resultMap));
        } catch (Exception e) {
            logger.info(e + "输出JSON报错。");
        } finally {
            out.flush();
            out.close();
        }
    }

}
