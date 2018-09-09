package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.*;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.service.*;
import com.yj.shopmall.util.MyRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.*;

@RestController
@RequestMapping("/mall")
public class userController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Reference
    UserServerce userServerce;
    @Autowired
    MyRedis myRedis;


    /*
     * 用户列表
     * */
    @RequestMapping("/userList")
    @Mylog(description = "查询用户列表")
    public ResultLayui<User> userList(User user) {
        return userServerce.findUser(user);
    }

//    获取登录用户的信息
    @RequestMapping("/getUser")
    @ResponseBody
    public JsonResult getUsers(){
        User userById = userServerce.findUserById(getUserId());
        return JsonResult.build(200,getSession().getHost(),userById);
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
        logger.info(user.toString());
        JsonResult jsonResult = userServerce.addUser(user, address);
        return jsonResult;

    }


    /*
     * 管理员修改用户信息
     * */
    @RequestMapping("/updateUserById")
    @Mylog(description = "管理员修改用户信息")
    public JsonResult<User> updateUserById(User user) {
//        JsonResult jsonResult = userServerce.updateUser(user);
//        User userById = userServerce.findUserById(user.getuId());
        //修改权限
//        myRedis.setUserToRedis(user.getuId(), userById);
//        return jsonResult;
        return JsonResult.ok();
    }


    /*
     * 管理员修改自己的信息
     * */
    @RequestMapping("/updateuserMsg")
    @ResponseBody
    @Mylog(description = "管理员修改信息")
    public JsonResult updateuserMsg(User user) {
        user.setuId(getUserId());
        user.setEmail(getUser().getEmail());
        JsonResult jsonResult = userServerce.updateUser(user);
        return JsonResult.ok();
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


    //修改用户当前角色
    @RequestMapping("/updateUserRole")
    @Mylog(description = "修改角色")
    public JsonResult updateUserById(Urole urole) {
//        JsonResult jsonResult = userServerce.updateUserRole(urole);
//        return jsonResult;
        return JsonResult.ok();
    }


    /*
     * 检验用户名的唯一性
     * */
    @RequestMapping("/nameIsOnly.do")
    @Mylog(description = "校验用户名唯一性")
    public String checkNameIsOnly(String msg) {
        boolean b = userServerce.checkNameIsOnly(msg);
        if (b) {
            //为ok用户名可用
            return "ok";

        }
        return "false";
    }

    /*
     * 检验电话的唯一性
     * */
    @RequestMapping("/phoneIsOnly.do")
    @Mylog(description = "校验电话唯一性")
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
     * 删除
     * */
    @RequestMapping("/delUserById")
    @Mylog(description = "删除用户")
    public JsonResult<User> delUserById(String[] ids) {
        //return userServerce.delUserById(ids);
        return JsonResult.ok();
    }


    /*
     *在线用户列表
     * */
    @RequestMapping("/OnlineUserList")
    public ResultLayui<OnLineUser> OnlineUserList(OnLineUser onLineUser) {
        ResultLayui<OnLineUser> onlineUser = userServerce.findOnlineUser(onLineUser);
        return onlineUser;
    }


    /*
     * 管理员强制退出在线用户
     * */
    @RequestMapping("/logoutCore.do")
    @Mylog(description = "强制登出")
    @ResponseBody
    public JsonResult qinagzhilogout(ModelAndView model, String[] ids, HttpServletRequest request) {
//        for (String id : ids) {
//            Boolean aBoolean = myRedis.isHaveSessionKey(id);
//            if (!aBoolean) {
//                continue;
//            }
//            myRedis.delSessionFromRedis(id);
//        }
//        userServerce.delOnlineUser(ids);
        return JsonResult.ok();
    }


    /*
     * 获取登录信息
     * */
    @RequestMapping("LoginMSGList")
    public ResultLayui<LoginLog> LoginMSGList(LoginLog loginLog) {
        ResultLayui<LoginLog> LoginLogList = userServerce.findLoginlog(loginLog);
        return LoginLogList;
    }

    /*
     * 删除登录信息
     * */
    @RequestMapping("delLoginMSG")
    public JsonResult delLoginMSG(String[] ids) {
//        int i = userServerce.delLoginlog(ids);
        return JsonResult.ok();
    }





    @RequestMapping("/userImgUpload")
    @ResponseBody
    @Mylog(description = "上传图片")
    public JsonResult userImgUpload(String oldImgPath, String uId,
                                    MultipartFile file, HttpServletRequest request)
            throws IOException {

//        List<String> fileNameList = new ArrayList<>();
//
//        final String filePath = "http://106.14.226.138:7777/";
//
//
//        //如果第一次上传就更新数据库字段
//        if (oldImgPath == null || oldImgPath.equals("")) {
//            String ct = file.getContentType();
//            //图片类型
//            String fileType = ct.substring(ct.indexOf("/") + 1);
//            //图片名字
//            oldImgPath = uId + "." + fileType;
//            //上传
//            fileNameList.add(oldImgPath);
//            Ueditor ueditor = UploadFile.uploadImgUEditor(file, oldImgPath, request);
//
//            //上传成功
//            if (ueditor.getState().equals("SUCCESS")) {
//                //更新数据库
//                User user = new User();
//                user.setImg(filePath + oldImgPath);
//                user.setuId(uId);
//                userServerce.updateUser(user);
//
//                //更新session
////                User attribute = (User) session.getAttribute(USER_SESSIOMN);
////                attribute.setImg(filePath + oldImgPath);
//                User userById = userServerce.findUserById(getUserId());
//                getSession().setAttribute(USER_SESSIOMN, userById);
//
//                return JsonResult.ok(oldImgPath);
//            }
//        } else {
//            //更改头像，不需要更新数据库字段
//            Ueditor ueditor = new Ueditor();
//
//            //判断类型是否相同
//            String newImgType = BaseController.isSame(file, oldImgPath);
//            String newImgPath = "";
//
//            if (newImgType == null) {
//                //图片类型相同 无需更换
//                fileNameList.add(oldImgPath);
//                ueditor = UploadFile.uploadImgUEditor(file, oldImgPath, request);
//                newImgPath = oldImgPath;
//            } else {
//                //生成新图片名字
//                newImgPath = uId + "." + newImgType;
//                //上传
//                ueditor = UploadFile.uploadImgUEditor(file, newImgPath, request);
//
//                //更新数据库图片路径
//                User user = new User();
//                user.setImg(filePath + newImgPath);
//                user.setuId(uId);
//
//                userServerce.updateUser(user);
//
//
//                //删除旧图片
//                List<String> strings = new ArrayList<>();
//                strings.add(oldImgPath);
//                FtpUtil.deleteFile(strings);
//            }
//
//            if (ueditor.getState().equals("SUCCESS")) {
//                return JsonResult.ok(filePath+newImgPath);
//            }
//        }
//        return JsonResult.errorMsg("false");

        return JsonResult.ok();
    }


    public JsonResult getUserFromDB(){
        User userById = userServerce.findUserById(getUserId());
        return JsonResult.build(200,getSession().getHost(),userById);
    }

}
