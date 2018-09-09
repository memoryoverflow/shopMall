package com.yj.shopmall.Aspect;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.IpUtils;
import com.yj.shopmall.Utils.getBrowser;
import com.yj.shopmall.Utils.getIPAddress;
import com.yj.shopmall.Utils.getOperateSys;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.controller.BaseController;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.LoginLog;
import com.yj.shopmall.pojo.OperateLog;
import com.yj.shopmall.pojo.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 系统日志切点类
 *
 * @author linrx
 */
@Aspect
@Component
public class SystemLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);


    @Reference
    LogServerce logServerce;

    @Reference
    UserServerce userServerce;

    private static final ThreadLocal<Object> logThreadLocal =
            new NamedThreadLocal<Object>("ThreadLocal log");
    @Autowired
    private ThreadPoolExecutor threadPoolTaskExecutor;

    /**
     * Controller层切点 注解拦截
     */
//    @Pointcut("@annotation(Mylog)")
//    public void controllerAspect() {
//    }

    /**
     * 方法规则拦截
     */
    @Pointcut("execution(* com.yj.shopmall.controller.*.*(..))")
    public void controllerAspect() {
    }


    /**
     * 前置通知 用于拦截Controller层记录用户登出的操作
     *
     * @param joinPoint 切点
     */
    @Before("controllerAspect() && @annotation(mylog)")
    public void doBefor(JoinPoint joinPoint, Mylog mylog) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //头部信息 获取浏览器和 操作系统
        String header = request.getHeader("User-Agent");
        HttpSession session = request.getSession();
//        User u = (User) session.getAttribute("user_Session");
        User u = BaseController.getUser();
        String operateType = "";
        String address = "";
        try {//
            operateType = getControllerMethodDescription2(joinPoint); //操作类型 获得注解的那个名字
            logger.info("操作方法=[{}]", operateType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String Ip = IpUtils.getIpAddr(request);//request.getRemoteAddr();//请求的IP
        logger.info("请求Ip=[{}]", Ip);
        if (operateType == null || operateType.equals("")) {
        } else {
            if (operateType.indexOf("登出") >= 0) {
                if (u == null) {
                    //未登录
                    logger.info("用户未登录点了退出按钮操作");
                } else {
                    LoginLog loginLog = new LoginLog();
                    loginLog.setMsg("执行登出");
                    loginLog.setName(u.getEmail());
                    loginLog.setStatus("成功");

                    address = getIPAddress.getAddress(Ip);
                    if (address == null) {
                        address = "获取地址失败";
                    }
                    loginLog.setAddress(address);
                    loginLog.setIp(Ip);
                    loginLog.setBrowser(getBrowser.getBrowser(header));
                    loginLog.setSystem(getOperateSys.getSys(header));

                    loginLog.setTime(new Date());
                    loginLog.setId(UUID.randomUUID().toString().replace("-", ""));
                    threadPoolTaskExecutor.execute(new SaveLogThread(loginLog, userServerce));
                    logThreadLocal.set(loginLog);
                }
            }
            if (operateType.indexOf("登录") >= 0) {
                LoginLog loginLog = new LoginLog();
                //登录成功
                loginLog.setStatus("成功");
                loginLog.setMsg("登录成功");
                address = getIPAddress.getAddress(Ip);
                if (address == null || address.equals("")) {
                    address = "获取地址失败";
                }
                loginLog.setAddress(address);
                loginLog.setIp(Ip);
                String id=UUID.randomUUID().toString().replace("-", "");
                loginLog.setId(id);
                session.setAttribute("loginId",id);
                loginLog.setBrowser(getBrowser.getBrowser(header));
                loginLog.setSystem(getOperateSys.getSys(header));
                //String s = JSONUtils.toJSONString(joinPoint.getArgs());
                Object[] args = joinPoint.getArgs();
                String uname = "无参数";
                for (Object o : args) {
                    User user = new User();
                    try {
                        user = (User) o;
                        uname = user.getEmail();
                    } catch (Exception e) {
                        /*忽视*/
                    }
                }
                loginLog.setTime(new Date());
                loginLog.setName(uname);
                threadPoolTaskExecutor.execute(new SaveLogThread(loginLog, userServerce));
                //userServerce.addLoginlog(loginLog);
            }
        }
    }

    /**
     * 后置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @After("controllerAspect() && @annotation(mylog)")
    public void doAfter(JoinPoint joinPoint, Mylog mylog) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取subject 判断用户是否登录
        HttpSession session = request.getSession();
        User u = BaseController.getUser();
        //头部信息 获取浏览器和 操作系统
        String header = request.getHeader("User-Agent");

        String operateType = "";
        try {//
            operateType = getControllerMethodDescription2(joinPoint); //操作类型 获得注解的那个名字
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (operateType == null || operateType.equals("")) {
        } else {
            String Ip = request.getRemoteAddr();//请求的IP
            String url = request.getRequestURL().toString();//请求的Uri
            String method = request.getMethod();//请求的方法类型(post/get)
            if (operateType.indexOf("登出") < 0) {
                if (operateType.indexOf("登录") < 0) {
                    String type = "info";                          //日志类型(info:入库,error:错误)
                    Map<String, String[]> params = request.getParameterMap(); //请求提交的参数
                    OperateLog log = new OperateLog();
                    log.setId(UUID.randomUUID().toString()); //设置id
                    log.setLogtype(type); //设置日志类型
                    log.setOperateType(operateType); //操作方法
                    log.setHost(Ip); //主机
                    log.setUrl(url); //url
                    log.setMethod(method); //请求的方法名
                    log.setMapToParams(params); //请求参数
                    log.setTime(new Date());
                    if (u == null) {
                        String uname = "无名游客";
                        log.setUser(uname);
                    } else {
                        log.setUser(u.getName());
                    }

                    String address = "";
                    address = getIPAddress.getAddress(Ip);
                    if (address == null || address.equals("")) {
                        address = "获取IP失败";
                    }
                    System.out.println(Ip);
                    log.setAddress(address);//登录所在地址


                    //查询列表时候不添加 如果有异常就添加
                    if (operateType.indexOf("查询") < 0) {
                        //new SaveLogThread(log, logServerce).start();
                        threadPoolTaskExecutor.execute(new SaveLogThread(log, logServerce, "oprate"));
                    }
                    logThreadLocal.set(log);

                    //登录日记记录
                }
            }
        }
    }

    /**
     * 异常通知
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "controllerAspect() && @annotation(mylog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e, Mylog mylog) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String ano = getControllerMethodDescription2(joinPoint);


        //判断是不是执行登出的异常
        if (ano.equals("执行登出")) {
            LoginLog log = (LoginLog) logThreadLocal.get();
            if (log != null) {
                String errorMsg = "";
                if (e.getMessage() == null || e.getMessage().equals("")) {
                    errorMsg = e.toString();
                } else {
                    log.setStatus("失败");
                    log.setMsg(errorMsg);
                    threadPoolTaskExecutor.execute(new UpdateLogThread(log, userServerce));
                }
            }
        } else {
            OperateLog log = (OperateLog) logThreadLocal.get();
            if (log != null) {
                String errorMsg = "";
                if (e.getMessage() == null || e.getMessage().equals("")) {
                    errorMsg = e.toString();
                } else {
                    if (log.getOperateType().indexOf("查询") >= 0) {
                        threadPoolTaskExecutor.execute(new SaveLogThread(log, logServerce, "oprate "));
                    }
                    log.setLogtype("error");
                    log.setException(e.getMessage().toString());
                    threadPoolTaskExecutor.execute(new UpdateLogThread(log, logServerce, "oprate"));
                }
            }
        }


    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     */
    public static String getControllerMethodDescription2(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Mylog controllerLog = method
                .getAnnotation(Mylog.class);
        String discription = controllerLog.description();
        return discription;
    }

    /**
     * 保存日志线程
     *
     * @author lin.r.x
     */
    private static class SaveLogThread extends Thread {

        private String temp;

        private OperateLog log;
        private LogServerce logServerce;

        private UserServerce userServerce;
        private LoginLog loginLog;

        //操作日志
        public SaveLogThread(OperateLog log, LogServerce logServerce, String temp) {
            this.temp = temp;
            this.log = log;
            this.logServerce = logServerce;
        }

        //登录日志
        public SaveLogThread(LoginLog log, UserServerce userServerce) {
            this.loginLog = log;
            this.userServerce = userServerce;
        }

        @Override
        public void run() {
            if (temp == null || temp.equals("")) {
                System.out.println(loginLog);
                userServerce.addLoginlog(loginLog);
            } else {
                logServerce.addOperateLog(log);
            }
        }
    }

    /**
     * 日志更新线程
     *
     * @author lin.r.x
     */
    private static class UpdateLogThread extends Thread {
        private String temp;

        private OperateLog log;
        private LogServerce logServerce;

        private UserServerce userServerce;
        private LoginLog loginLog;


        public UpdateLogThread(OperateLog log, LogServerce logServerce, String temp) {
            super(UpdateLogThread.class.getSimpleName());
            this.temp = temp;
            this.log = log;
            this.logServerce = logServerce;
        }

        public UpdateLogThread(LoginLog log, UserServerce userServerce) {
            super(UpdateLogThread.class.getSimpleName());
            this.loginLog = log;
            this.userServerce = userServerce;
        }

        @Override
        public void run() {
            if (temp == null || temp.equals("")) {
                userServerce.updateLoginLog(loginLog);
            } else {
                logServerce.updateOperateLog(log);
            }
        }
    }

}
