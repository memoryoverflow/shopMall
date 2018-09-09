package com.yj.shopmall.Shiro;


import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.service.UserServerce;
import com.yj.shopmall.pojo.Role;
import com.yj.shopmall.pojo.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.yj.shopmall.constant.Constant.USER_ISACTIVATED;
import static com.yj.shopmall.constant.Constant.USER_ISFROZEN;

@Component
public class Realm extends AuthorizingRealm {
    private final static Logger logger = LoggerFactory.getLogger(Realm.class);
    @Reference
    UserServerce userService;

    /**
     * 登陆认证逻辑
     * 认证
     **/

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //拿到封装好账户密码的token
        UsernamePasswordToken u_pTaken = (UsernamePasswordToken) authenticationToken;
        String inEmail = u_pTaken.getUsername();
        if (inEmail == null) {
            //用户不存在
            throw new UnknownAccountException("账户不存在！");
        }
        logger.info("登录账户=[{}]", inEmail);
        //查询数据库
        User u = userService.checkLogin(new User(inEmail));

        if (u == null) {
            //用户不存在
            throw new UnknownAccountException("账户不存在！");
        }
        logger.info("当前用户是否激活：" + u.getIsActivated());
        if (u.getIsActivated().equals(String.valueOf(USER_ISACTIVATED))) {
            throw new UnknownAccountException("未激活登录！");

        }

        if (u.getIsFrozen().equals(String.valueOf(USER_ISFROZEN))) {
            throw new LockedAccountException("用户已被锁定");
        }


        User userLoginRole = userService.findUserLoginRole(u);
        logger.info("登录用户的信息=[{}]",userLoginRole);

        if (!userLoginRole.getRole().getRoleChar().equals("admin")) {
            throw new AuthenticationException("权限不足！");
        }


        /**  用户存在密码交给 realm 比对
         * 1).principal: 认证的实体信息，可以使username 也可以是数据表对应的用户实体类对象
         * 2).credentials:  密码
         * 3).realmName: 当前reaml 对象的name. 丢奥用父类的getName() 方法即可
         **/


        User principal = userService.findUserLoginRole(u);


        String realName = getName();
        String credentials = u.getPassword();

        /** 数据库的密码是进行过MD5盐值加密的，表单传过来的密码 进行md5加密后对比
         *hashAlgorithmName
         **/


        //加盐 计算盐值 保证每个加密后的 MD5 不一样
        ByteSource credentialsSalt = ByteSource.Util.bytes(u.getEmail());
        /* * 密码比对流程：
         * 进入：HashedCredentialsMatcher 类 此类拿到 盐值 和 加密次数以及加密方法后，
         * 调用：SimpleHash 类 里面的  SimpleHash(String algorithmName, Object source, Object salt, int hashIterations)；方法
         * 将 credentialsSalt盐值 convertSaltToBytes(salt) 转成byte[]
         * 随后调用 当前类的 hash(byte[] bytes, byte[] salt, int hashIterations) 方法 进行MD5加密，
         * 然后 HashedCredentialsMatcher类 重写 SimpleCredentialsMatcher类 doCredentialsMatch 方法，doCredentialsMatch方法
         * 调用了equals(Object tokenCredentials, Object accountCredentials)方法进行密码的比对
         * 所以，最终的密码比对是交给了 SimpleCredentialsMatcher类的doCredentialsMatch方法
         **/

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realName);
        return info;
    }


    /**
     * 授权逻辑
     **/

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.从PrincipalCollection 中获取登陆用户的信息,获取登陆者的id
        User principal = (User) principalCollection.getPrimaryPrincipal();

        //2.利用当前用户的信息用户当前用户的角色或权限
        Set<String> roles = new HashSet<>();

        //从用户中取出权限
        Role role = principal.getRole();

        roles.add(role.getRoleChar());
        logger.info("### 登录授权，用户=[{}],角色=[{}]", principal.getName(), role.getRoleChar());


        //3.创建 SimpleAuthenticationInfo 对象，设置角色
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
        return info;
    }
}
