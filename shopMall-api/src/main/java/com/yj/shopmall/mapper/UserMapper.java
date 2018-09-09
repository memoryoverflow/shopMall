package com.yj.shopmall.mapper;



import com.yj.shopmall.pojo.*;

import java.util.List;

public interface UserMapper {
    /*
    * 注册
    * */
    Integer addUser(User user);

    /*
    * 删除
    * */
    Integer delUserById(String[] id);



    /*
    * 删除地址
    * */
    Integer delUserAddressById(String u_id);



    /*
    * findUser
    * */
    List<User> findUser(User user);
    int findUserCount(User user);


    /*
    * update
    * */
    Integer updateUser(User user);

    /*
    * 登录
    * */
    User checkLogin(User user);
    User findUserLoginRole(User user);

    /*
     * 校验用户名是否唯一
     * */
    User checkNameIsOnly(String name);

    /*
     *校验邮箱是否唯一
     * */
    User checkEmailIsOnly(String email);
    /*
     *校验手机是否唯一
     * */
    User checkPhoneIsOnly(String msg);
    /*
     * 添加地址
     * */
    int addAddress(Address address);
    int updateAddress(Address address);

    //激活邮箱
    int activedEmail(String uid);

    /*
    * 获得地址
    * */
    List<User> getAddress(String uid);


    /*
    * selectById
    * */
    User findUserById(String uid);


    /*
    * 删除地址
    * */
    int deleteAddressbyId(String addressId);
    int delUserAddressByUserId(String [] userIds);


    /*
    * 统计在线用户
    * */
    int addOnlineUser(OnLineUser onLineUser);
    int delOnlineUser(String[] ids);
    List<OnLineUser> findOnlineUser(OnLineUser onLineUser);
    int findOnlineUserCount(OnLineUser onLineUser);


    int addLoginlog(LoginLog onLineUser);
    int delLoginlog(String[] ids);
    List<LoginLog> findLoginlog(LoginLog onLineUser);
    int findLoginlogCount(LoginLog onLineUser);
    int updateLoginLog(LoginLog loginLog);


    //角色
    int addUserRole(Urole urole);
    int delUserRole(String [] ids);
    int updateUserRole(Urole urole);

}

