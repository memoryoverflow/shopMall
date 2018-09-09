package com.yj.shopmall.service;


import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.pojo.*;

public interface UserServerce {
    /*
     * 注册
     * */
    JsonResult addUser(User user, String address);

    /*
     * 删除
     * */
    JsonResult delUserById(String[] ids);

    /*
     * findUser
     * */
    ResultLayui<User> findUser(User user);

    /*
     * update
     * */
    JsonResult updateUser(User user);

    /*
     * 登录
     * */
    User checkLogin(User user);

    User findUserLoginRole(User user);

    /*
     * 校验用户名是否唯一
     * */
    boolean checkNameIsOnly(String name);

    /*
     *校验邮箱是否唯一
     * */
    boolean chekEmailIsOnly(String email);

    /*
     * 添加地址
     * */
    JsonResult addAddress(Address address);

    JsonResult updateAddress(Address address);

    /*
     * 获得地址
     * */
    JsonResult getAddress(String uid);


    /*
     * 检查手机号
     * */
    boolean checkPhoneIsOnly(String msg);

    /*
     * 激活账户
     * */
    JsonResult activedEmail(String uid);

    /*
     * ById
     * */
    User findUserById(String uid);

    /*
     * 删除地址
     * */
    JsonResult deleteAddressbyId(String addressId);

    /*
     * 统计在线用户
     * */
    int addOnlineUser(OnLineUser onLineUser);

    int delOnlineUser(String[] ids);

    ResultLayui<OnLineUser> findOnlineUser(OnLineUser onLineUser);

    int addLoginlog(LoginLog onLineUser);

    int delLoginlog(String[] ids);

    ResultLayui<LoginLog> findLoginlog(LoginLog onLineUser);

    int updateLoginLog(LoginLog loginLog);

    //    修改角色
    JsonResult updateUserRole(Urole urole);
}
