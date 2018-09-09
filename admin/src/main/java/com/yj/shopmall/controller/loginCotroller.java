package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.JsonUtils;
import com.yj.shopmall.Utils.getIPAddress;
import com.yj.shopmall.Utils.getOperateSys;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.LoginLog;
import com.yj.shopmall.pojo.OnLineUser;
import com.yj.shopmall.pojo.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import static com.yj.shopmall.Utils.getBrowser.getBrowser;
import static com.yj.shopmall.VerificationCode.ValidateCodeUtil.RANDOMCODEKEY;
import static com.yj.shopmall.constant.Constant.USER_SESSIOMN;
import static com.yj.shopmall.controller.BaseController.getSubject;

@Controller
public class loginCotroller {
    private Logger logger = LoggerFactory.getLogger(loginCotroller.class);

    @Reference
    UserServerce userServerce;

    @RequestMapping("/login.do")
    @Mylog(description = "后台登录")
    @ResponseBody
    public JsonResult logining(User user, String inputImgCode, HttpServletRequest request, HttpSession session) {
        Subject currentUser = getSubject();
        Session session1 = getSubject().getSession();
        if (!currentUser.isAuthenticated()) {

            //判断验证码是否正确
            String imgCode = session.getAttribute(RANDOMCODEKEY).toString();
            logger.info("登录的SessionId=[{}]",session.getId());
            if (imgCode.equals(inputImgCode)) {

                UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), user.getPassword());
                try {
                    //调用 subject的 login（）方法，执行登陆，登陆将进行账户密码验证，不对的将抛出异常
                    currentUser.login(token);
                    User u = (User) currentUser.getPrincipal();

                    //获取浏览器头部信息，判断浏览器类型
                    final String agent = request.getHeader("USER-AGENT");
                    addOnlineUser(session1, agent, u);

                } catch (UnknownAccountException u) {
                    //错误的凭证异常，密码不对
                    updateLoginLog(session,u.getMessage(),"失败");
                    return JsonResult.errorMsg("账户不存在");

                } catch (IncorrectCredentialsException u) {
                    updateLoginLog(session,u.getMessage(),"失败");
                    return JsonResult.errorMsg("密码错误");
                    //用户被锁定的异常
                } catch (LockedAccountException u) {
                    updateLoginLog(session,u.getMessage(),"失败");
                    return JsonResult.errorMsg("用户被锁定");
                } catch (AuthenticationException u) {
                    logger.info("登录异常=[{}]", u);
                    String e="";
                    if (u.getMessage().indexOf("权限不足")>=0) {
                        e="权限不足！";
                    }else{
                        e="认证失败！";
                    }
                    updateLoginLog(session,e,"失败");
                    return JsonResult.errorMsg(e);
                }
            }else{
                updateLoginLog(session,"验证码错误","失败");
                return JsonResult.errorMsg("验证码错误");
            }
        }
        return JsonResult.ok();
    }

    //将登陆者的session保存到数据库
    public void addOnlineUser(Session session, String agent, User user) {
        OnLineUser onLineUser = new OnLineUser();
        onLineUser.setId(session.getId().toString());
        onLineUser.setIp(session.getHost());
        onLineUser.setName(user.getEmail());
        onLineUser.setTime(session.getStartTimestamp());
        onLineUser.setAddress(getIPAddress.getAddress(session.getHost()));
        onLineUser.setBrowser(getBrowser(agent));
        onLineUser.setSystem(getOperateSys.getSys(agent));
        userServerce.addOnlineUser(onLineUser);
    }

    //修改登录日志
    public void updateLoginLog(HttpSession session, String msg, String status){
        LoginLog loginLog = new LoginLog();
        loginLog.setMsg(msg);
        loginLog.setStatus(status);
        loginLog.setId(session.getAttribute("loginId").toString());
        logger.info("登录的Session=[{}]",session.getId());
        userServerce.updateLoginLog(loginLog);
    }



    @RequestMapping("/loginOut")
    @Mylog(description = "执行登出")
    public String loginOut(HttpSession session){
        getSubject().logout();
        return "/login";
    }

}
