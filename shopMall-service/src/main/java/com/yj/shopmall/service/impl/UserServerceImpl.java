package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.Utils.encryption;
import com.yj.shopmall.mapper.UserMapper;
import com.yj.shopmall.pojo.*;
import com.yj.shopmall.service.UserServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yj.shopmall.constant.Constant.ADDRESS_DEFAULT;

@Component
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServerceImpl implements UserServerce {
    private Logger logger = LoggerFactory.getLogger(UserServerceImpl.class);
    @Autowired
    UserMapper userMapper;


    /*
     *注册
     * */
    @Override
    public JsonResult addUser(User user, String add) {
        try {

            //加密
            user.setPassword((encryption.getMD5(user.getPassword(), user.getEmail())).toString());
            user.setCreateTime(new Date());
            //用户自己注册时默认不激活用户
            if (user.getIsActivated() == null || user.getIsActivated().equals("")) {
                user.setIsActivated("-1");
            }
            //用户自己注册时默认不冻结用户
            if (user.getIsFrozen() == null || user.getIsFrozen().equals("")) {
                user.setIsFrozen("1");
            }
            //用户自己注册时设置为普通角色
            if (user.getRole().getId() == 0) {
                user.getRole().setId(2);
            }

            //添加用户
            Integer integer = userMapper.addUser(user);

           //添加地址
            Address address = new Address();
            address.setAddress(add);
            address.setRecTel(user.getTel());
            address.setUser_id(user.getuId());
            address.setRecUser(user.getName());
            address.setIsDefault(ADDRESS_DEFAULT);
            int i = userMapper.addAddress(address);

            //添加角色
            Urole urole=new Urole();
            urole.setM_uId(user.getuId());
            urole.setM_rId(user.getRole().getId());
            userMapper.addUserRole(urole);

            return JsonResult.build(200, "注册成功", null);
        } catch (Exception e) {
            throw new RuntimeException("注册失败！" + e.getMessage());
        }
    }


    /*
     * 激活账户
     * */
    public JsonResult activedEmail(String uid) {
        try {
            int i = userMapper.activedEmail(uid);
            if (i > 0) {
                return JsonResult.build(200, "激活成功！去登陆吧~", null);
            } else {
                return JsonResult.build(500, "激活失败！联系管理员~ 1375668614@qq.com", null);

            }
        } catch (Exception e) {
            logger.info("激活失败:" + e);
            throw new RuntimeException("激活失败!请联系管理员 1375668614@qq.com");
        }

    }


    /*
     * ById
     * */
    public User findUserById(String uid) {
        return userMapper.findUserById(uid);
    }

    /*
     * 删除地址
     * */
    @Override
    public JsonResult deleteAddressbyId(String addressId) {
        try {
            userMapper.deleteAddressbyId(addressId);
        } catch (Exception e) {
            throw new RuntimeException("删除异常:" + e);
        }
        return JsonResult.ok();
    }

    /*
     * 添加在线用户
     * */
    @Override
    public int addOnlineUser(OnLineUser onLineUser) {
        int i = 0;
        try {
            i = userMapper.addOnlineUser(onLineUser);
        } catch (Exception e) {
            throw new RuntimeException("登录用户添加失败" + e);
        }

        return i;
    }

    /*
     * 删除在线用户
     * */
    @Override
    public int delOnlineUser(String[] ids) {
        int i = 0;
        try {
            i = userMapper.delOnlineUser(ids);
        } catch (Exception e) {
            throw new RuntimeException("删除登录用户失败:" + e);
        }
        return i;
    }

    /*
     * 获取在线用户
     * */
    @Override
    public ResultLayui<OnLineUser> findOnlineUser(OnLineUser onLineUser) {
        int page = onLineUser.getPage();
        int pageStart = (page - 1) * onLineUser.getLimit();
        onLineUser.setPage(pageStart);
        List<OnLineUser> onLineUserList = userMapper.findOnlineUser(onLineUser);
        int onlineUserCount = userMapper.findOnlineUserCount(onLineUser);
        return ResultLayui.jsonLayui(0, "", onlineUserCount, onLineUserList);
    }

    /*
     * 记录登录信息
     * */
    @Override
    public int addLoginlog(LoginLog onLineUser) {
        int i = 0;
        try {
            i = userMapper.addLoginlog(onLineUser);
        } catch (Exception e) {
            throw new RuntimeException("记录登录信息失败:" + e);
        }
        return i;
    }

    /*
     * 删除登录信息
     * */
    @Override
    public int delLoginlog(String[] ids) {
        int i = 0;
        try {
            i = userMapper.delLoginlog(ids);
        } catch (Exception e) {
            throw new RuntimeException("删除登录信息失败");
        }
        return i;
    }

    /*
     * 获取登录信息
     * */
    @Override
    public ResultLayui<LoginLog> findLoginlog(LoginLog onLineUser) {
        int page = onLineUser.getPage();
        int pageStart = (page - 1) * onLineUser.getLimit();
        onLineUser.setPage(pageStart);
        List<LoginLog> loginlog = userMapper.findLoginlog(onLineUser);
        int loginlogCount = userMapper.findLoginlogCount(onLineUser);
        return ResultLayui.jsonLayui(0, "", loginlogCount, loginlog);
    }

    @Override
    public int updateLoginLog(LoginLog loginLog) {
        try {
            userMapper.updateLoginLog(loginLog);
        } catch (Exception e) {
            throw new RuntimeException("更新登出记录失败：" + e);
        }
        return 1;
    }

    @Override
    public JsonResult updateUserRole(Urole urole) {
        try {
            userMapper.updateUserRole(urole);
        } catch (Exception e) {
            logger.info(e.toString());
            throw new RuntimeException("修改用户角色失败");
        }
        return JsonResult.ok();
    }

    /*
     * 删除用户
     * */
    @Override
    public JsonResult delUserById(String[] ids) {

        try {
            Integer integer = userMapper.delUserById(ids);
            if (integer > 0) {
                int i = userMapper.delUserAddressByUserId(ids);
                userMapper.delUserRole(ids);
            }
        } catch (Exception e) {
            throw new RuntimeException("删除用户失败");
        }

        return JsonResult.build(200, "OK", null);
    }

    /*
     * userList
     * */
    @Override
    public ResultLayui<User> findUser(User user) {
        int page = user.getPage();
        int pageStart = (page - 1) * user.getLimit();
        user.setPage(pageStart);

        List<User> user1 = userMapper.findUser(user);
        int userCount = userMapper.findUserCount(user);

        return ResultLayui.jsonLayui(0, "", userCount, user1);
    }

    /*
     * 修改
     * */
    @Override
    public JsonResult updateUser(User user) {
        try {

            if (user.getPassword() != null && !user.getPassword().equals("")) {
                String md5 = encryption.getMD5(user.getPassword(), user.getEmail()).toString();
                user.setPassword(md5);
            }

            Integer integer = userMapper.updateUser(user);
            if (integer > 0) {
                return JsonResult.build(200, "OK", null);
            }
            return JsonResult.build(500, "false", null);
        } catch (Exception e) {
            throw new RuntimeException("修改失败"+e);
        }


    }

    /*
     * 登录认证
     * */
    @Override
    public User checkLogin(User user) {
        return userMapper.checkLogin(user);
    }

    @Override
    public User findUserLoginRole(User user) {
        return userMapper.findUserLoginRole(user);
    }

    /*
     * 校验用户名唯一性
     * */
    @Override
    public boolean checkNameIsOnly(String name) {
        User user = userMapper.checkNameIsOnly(name);
        if (user == null) {
            return true;
        }
        return false;
    }

    /*
     * 校验邮箱唯一
     * */
    @Override
    public boolean chekEmailIsOnly(String email) {
        User user = userMapper.checkEmailIsOnly(email);
        if (user == null) {
            return true;
        }
        return false;
    }

    /*
     *
     * 添加地址
     * */
    @Override
    public JsonResult addAddress(Address address) {
        try {
            int i = userMapper.addAddress(address);
            return JsonResult.ok();
        } catch (Exception e) {
            logger.info("添加地址失败：" + e);
            //return JsonResult.errorMsg("修改地址失败");
            throw new RuntimeException("添加失败");
        }
    }

    /*
     * 修改地址
     * */
    @Override
    public JsonResult updateAddress(Address address) {
        try {
            int i = userMapper.updateAddress(address);
            if (i >= 1) {
                return JsonResult.ok();
            }
        } catch (Exception e) {
            logger.info("修改地址失败：" + e);
            throw new RuntimeException("修改失败");
        }
        return JsonResult.errorMsg("修改失败");
    }

    /*
     * 查找用户地址
     * */
    @Override
    public JsonResult getAddress(String uid) {
        try {
            List<User> address = userMapper.getAddress(uid);
            if (address.size() == 0) {
                return JsonResult.errorMsg("查不到用户");
            }
            logger.info("参数： " + uid + ":根据用户id获取用户信息--->" + address);
            return JsonResult.ok(address);
        } catch (Exception e) {
            return JsonResult.errorMsg("查不到用户");
        }
    }

    /*
     * 手机号码唯一校验
     * */
    @Override
    public boolean checkPhoneIsOnly(String msg) {
        User user = userMapper.checkPhoneIsOnly(msg);
        if (user == null) {
            return true;
        }
        return false;
    }
}
