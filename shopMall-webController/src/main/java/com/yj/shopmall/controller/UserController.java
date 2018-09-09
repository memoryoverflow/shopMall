package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Email.Email;
import com.yj.shopmall.Utils.*;

import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.service.ProductServerce;
import com.yj.shopmall.redis.MyRedis;
import com.yj.shopmall.redis.SessionRedis;
import com.yj.shopmall.service.UserServerce;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static com.yj.shopmall.Utils.getBrowser.getBrowser;
import static com.yj.shopmall.constant.Constant.REDIS_SESSION_Prefix;
import static com.yj.shopmall.constant.Constant.USER_SESSIOMN;
import static com.yj.shopmall.constant.Constant.user_img_default;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Reference
    UserServerce userServerce;
    @Autowired
    Email email;

    @Autowired
    JavaMailSender javaMailSender;

    @Reference
    ProductServerce productServerce;

    @Autowired
    SessionRedis sessionRedis;

    @Autowired
    MyRedis myRedis;

    /*
     * 用户登录
     * */
    @RequestMapping("login.do")
    @Mylog(description = "执行登录")
    @ResponseBody
    public JsonResult shirologin(User user,HttpServletRequest request,HttpSession session)
            throws Exception {
        //获取浏览器头部信息，判断浏览器类型
        final String agent = request.getHeader("USER-AGENT");

        //获取subject
        Subject currentUser = getSubject();
        logger.info("登录的SessionId,web=[{}]",session.getId());

        if (!currentUser.isAuthenticated()) {
            //将数据封装到token 中 传递到 realm 里面 执行认证了登陆 以及授权
            UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), user.getPassword());
            //记住我
            token.setRememberMe(false);
            //捕获登陆异常
            try {
                //调用 subject的 login（）方法，执行登陆，登陆将进行账户密码验证，不对的将抛出异常
                currentUser.login(token);
                User u = (User) currentUser.getPrincipal();

                //登录成功将会话信息存到数据库
                Session session1 = currentUser.getSession();
                addOnlineUser(session1, agent, u);
                session1.setAttribute(USER_SESSIOMN, u);


                //也保存一份信息到redis 方便管理对当前用户的信息实时监听更改
                myRedis.setUserToRedis(u.getuId(), u);

                String object = JsonUtils.objectToJson(u);
                session.setAttribute("user_Json", object);
            } catch (UnknownAccountException u) {

                updateLoginLog(session,u.getMessage(),"*账户不存在");
                return JsonResult.errorMsg("*"+u.getMessage());

            } catch (IncorrectCredentialsException u) {
                logger.info("密 码 错 误:" + u.getMessage() + " was incorrect!");
                updateLoginLog(session,u.getMessage(),"*密码错误");
                return JsonResult.errorMsg("*密码错误");
                //用户被锁定的异常
            } catch (LockedAccountException u) {
                logger.info("用 户 被 锁 定={} " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
                updateLoginLog(session,u.getMessage(),"*用户已被锁定");
                return JsonResult.errorMsg("*用户已被锁定");
            } catch (AuthenticationException u) {
                logger.info("登录异常=[{}]", u);
                updateLoginLog(session,u.getMessage(),"*其它异常");
                return JsonResult.errorMsg("登录失败");
            }
        }

        //登录成功 返回商品信息
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


    /*
     *用户登出
     */
    @RequestMapping("/logout.do")
    @Mylog(description = "执行登出")
    public String logout(ModelAndView model, HttpSession session) {
        session.removeAttribute("user_Json");
        session.removeAttribute("user_Session");

        logger.info("即将要退出的SesionID:" + session.getId().toString());
        boolean haveKey = sessionRedis.isHaveSeesionKey(getSession());
        //删除redis上的 用户信息
        myRedis.delUserFromRedis(getUserId());
        if (haveKey) {
            BaseController.logout();
        }
        //退出删除数据库的信息
        return "redirect: ../../mall/toLogin";
    }


    /*
     * 注册
     * */
    @RequestMapping("/register.do")
    @ResponseBody
    @Mylog(description = "用户注册")
    public JsonResult register(User user, String address) {
        //生成Id
        String uid = user.getTel() + String.valueOf((int) (Math.random() * 9000) + 1000);//随机生成4位数字
        user.setuId(uid);
        user.setImg(user_img_default);
        JsonResult jsonResult = userServerce.addUser(user, address);
        try {
            if (jsonResult.getMsg().equals("注册成功")) {
                email.sendEmail(uid, user.getEmail());
            }
        } catch (Exception e) {
            return JsonResult.build(2001, "邮件发送失败", null);
        }
        return jsonResult;

    }

    /*
     * 激活账户
     * */
    @RequestMapping("/active.do/{id}")
    @Mylog(description = "激活账户")
    public String active(@PathVariable("id") String uid, String email, Model model) {
        JsonResult jsonResult = userServerce.activedEmail(uid);
        User user = userServerce.findUserById(uid);
        model.addAttribute("email", "*账号：" + user.getEmail());
        model.addAttribute("msg", jsonResult.getMsg());
        return "commodity/EmailActived";
    }

    /*
     * 根据ID 查询用户
     * */
    @RequiresUser
    @RequestMapping("/getUserAddressMsg")
    @ResponseBody
    public JsonResult getUserById() {
        JsonResult address = userServerce.getAddress(BaseController.getUserId());
        return address;

    }

    /*
     * 检验用户名的唯一性
     * */
    @RequestMapping("/nameIsOnly.do")
    @ResponseBody
    public String checkNameIsOnly(String msg) {
        boolean b = userServerce.checkNameIsOnly(msg);
        if (b) {
            return "ok";

        }
        return "false";
    }


    /*
     * 检验用户名的唯一性
     * */
    @RequestMapping("/phoneIsOnly.do")
    @ResponseBody
    public String checkPhoneIsOnly(String msg) {
        boolean b = userServerce.checkPhoneIsOnly(msg);
        if (b) {
            return "ok";
        }
        return "false";
    }

    /*
     * 检验邮箱唯一性
     * */
    @RequestMapping("/emailIsOnly.do")
    @ResponseBody
    public String checkEmailIsOnly(String msg) {
        boolean b = userServerce.chekEmailIsOnly(msg);
        if (b) {
            return "ok";
        }
        return "false";
    }

    /*
     * 添加收货地址
     * */
    @RequestMapping("/addAddress")
    @ResponseBody
    @Mylog(description = "添加收货地址")
    public JsonResult  addAddress(Address address, HttpSession session) {
        User user = BaseController.getUser();
        address.setUser_id(user.getuId());
        JsonResult jsonResult = userServerce.addAddress(address);
        return jsonResult;
    }

    /*
     * 修改收货地址
     * */
    @RequestMapping("/updateAddress")
    @ResponseBody
    @Mylog(description = "修改收货地址")
    public JsonResult updateAddress(Address address, HttpSession session) {
        //User user_session = (User) session.getAttribute("user_Session");
        User user = BaseController.getUser();
        address.setUser_id(user.getuId());
        JsonResult jsonResult = userServerce.updateAddress(address);
        return jsonResult;
    }


    /*
     * 修改用户信息
     * */
    @RequestMapping("/updateuserMsg")
    @ResponseBody
    @Mylog(description = "修改信息")
    public JsonResult updateuserMsg(User user) {
        user.setuId(getUserId());
        user.setEmail(getUser().getEmail());
        JsonResult jsonResult = userServerce.updateUser(user);
        return JsonResult.build(200, "OK", user);
    }

    //新旧密码比对
    @RequestMapping("/equalPwd")
    @ResponseBody
    public JsonResult equalPwd(String pwd){
        User user = getUser();
        //旧密码加密后与当前面比对
        String md5 = encryption.getMD5(pwd, user.getEmail()).toString();
        String password = user.getPassword();
        if (md5.equals(password)) {
            return JsonResult.ok();
        }
        return JsonResult.errorMsg("false");
    }


    /*
     * 获取用户信息
     * */
    @RequestMapping("/getUserMsg")
    public String getuserMsg(Model model) {
        JsonResult userFromDB = getUserFromDB();
        User data = (User)userFromDB.getData();
        model.addAttribute("user",data);
        //logger.info(session.getAttribute(USER_SESSIOMN).toString());
        return "commodity/uMsg";
    }


    /*
     * 跳转到地址页面
     * */
    @RequestMapping("UserAddress")
    public String toAddress() {
        return "commodity/uAddress";
    }


    @RequestMapping("/userImgUpload")
    @ResponseBody
    @Mylog(description = "上传图片")
    public JsonResult userImgUpload(String oldImgPath, String uId,
                                    MultipartFile file, HttpServletRequest request)
            throws IOException {

        List<String> fileNameList = new ArrayList<>();

        final String filePath = "http://106.14.226.138:7777/";


        //如果第一次上传就更新数据库字段
        if (oldImgPath == null || oldImgPath.equals("")) {
            String ct = file.getContentType();
            //图片类型
            String fileType = ct.substring(ct.indexOf("/") + 1);
            //图片名字
            oldImgPath = uId + "." + fileType;
            //上传
            fileNameList.add(oldImgPath);
            Ueditor ueditor = UploadFile.uploadImgUEditor(file, oldImgPath, request);

            //上传成功
            if (ueditor.getState().equals("SUCCESS")) {
                //更新数据库
                User user = new User();
                user.setImg(filePath + oldImgPath);
                user.setuId(uId);
                userServerce.updateUser(user);

                //更新session
//                User attribute = (User) session.getAttribute(USER_SESSIOMN);
//                attribute.setImg(filePath + oldImgPath);
                User userById = userServerce.findUserById(getUserId());
                getSession().setAttribute(USER_SESSIOMN, userById);

                return JsonResult.ok(oldImgPath);
            }
        } else {
            //更改头像，不需要更新数据库字段
            Ueditor ueditor = new Ueditor();

            //判断类型是否相同
            String newImgType = BaseController.isSame(file, oldImgPath);
            String newImgPath = "";

            if (newImgType == null) {
                //图片类型相同 无需更换
                fileNameList.add(oldImgPath);
                ueditor = UploadFile.uploadImgUEditor(file, oldImgPath, request);
                newImgPath = oldImgPath;
            } else {
                //生成新图片名字
                newImgPath = uId + "." + newImgType;
                //上传
                ueditor = UploadFile.uploadImgUEditor(file, newImgPath, request);

                //更新数据库图片路径
                User user = new User();
                user.setImg(filePath + newImgPath);
                user.setuId(uId);

                userServerce.updateUser(user);


                //删除旧图片
                List<String> strings = new ArrayList<>();
                strings.add(oldImgPath);
                FtpUtil.deleteFile(strings);
            }

            if (ueditor.getState().equals("SUCCESS")) {
                return JsonResult.ok(filePath+newImgPath);
            }
        }
        return JsonResult.errorMsg("false");
    }


    /*
     * 根
     * 据id删除收货地址
     * */
    @RequestMapping("/delAddresById")
    @ResponseBody
    @Mylog(description = "删除收货地址")
    public JsonResult delAddress(String addressId) {
        return userServerce.deleteAddressbyId(addressId);
    }

    //shiro校验是否登录
    @RequestMapping("/isLogin")
    @ResponseBody
    public JsonResult isLogin() {
        return getUserFromDB();
    }



    public JsonResult getUserFromDB(){
        User userById = userServerce.findUserById(getUserId());
        return JsonResult.build(200,getSession().getHost(),userById);
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

}
