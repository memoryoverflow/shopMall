package com.yj.shopmall.Shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {


    //登录url
    private static final String loginUrl = "/login.Page";

    //没有权限的页面


    /* *  配置 安全管理器 securityManager
     *  securityManager需要realm
     *  将其注入
     **/

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") Realm realm,
                                                               @Qualifier("SessionManager") DefaultSessionManager sessionManager,
                                                               CookieRememberMeManager cookieRememberMeManager,
                                                               EhCacheManager ehCacheManager
    ) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(cookieRememberMeManager);
        securityManager.setCacheManager(ehCacheManager);
        return securityManager;
    }


    /**
     * 配置 ShiroFilterFactoryBean
     * <p>
     * ShiroFilterFactoryBean需要 securityManager 将其注入
     **/

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
                                                         FilterChainDefinitionMapBuider filterChainDefinitionMapBuider
                                                         ) {
        //实例化 ShiroFilterFactoryBean
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //设置访问路劲需要和不需要的 请求 url
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMapBuider.BuiderFilterChainDefinitionMap());
        //没有登陆会被拦截 直接跳转到登录页面
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        //定义没有权限的页面
//        Map<String,Filter> filterMap=new LinkedHashMap<String,Filter>();
//        filterMap.put("LoginFiter",ad);
//        filterMap.put("authc",new MyFormAuthenticationFilter());
//        filterMap.put("roles",rolesFilter);
//        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }


    //登录拦截
//    @Bean("LoginFiter")
//    public AdviceFilter adviceFilter(){
//        return new LoginFilter();
//    }
//   //角色拦截
//    @Bean("RoleFilter")
//    public RolesAuthorizationFilter accessControlFilter(){
//        return new ShiroRoleFilter();
//    }


    //设置加密方式以及加密次数
    @Bean("hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashIterations(1024);//加密次数
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//设置加密方式
        return hashedCredentialsMatcher;
    }

    //将HashedCredentialsMatcher 构造注入 realm方法 中
    //将自定义的realm 注入到当前的 realm 中
    @Bean("userRealm")
    public Realm realm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher hashedCredentialsMatcher,
                       @Qualifier("realm") Realm realm) {
        realm.setCredentialsMatcher(hashedCredentialsMatcher);
        return realm;
    }


    //session会话
    @Bean("SessionManager")
    public DefaultWebSessionManager defaultWebSessionManager(@Qualifier("MySessionListener") SessionListener sessionListener) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();

        //defaultWebSessionManager.setSessionDAO(abstractSessionDAO);

        //没5秒检查一次session是否过期
        //defaultWebSessionManager.setSessionValidationInterval(1000);

        //设置session超时时间。一旦超时将其删除
        defaultWebSessionManager.setGlobalSessionTimeout(1800000);
        defaultWebSessionManager.setDeleteInvalidSessions(true);

        //Session监听
        Collection<SessionListener> sessionListenerAdapters = new ArrayList();
        ;
        sessionListenerAdapters.add(sessionListener);
        defaultWebSessionManager.setSessionListeners(sessionListenerAdapters);

        return defaultWebSessionManager;
    }


    //会话DAO
//    @Bean
//    public AbstractSessionDAO abstractSessionDAO() {
//        return new RedisSessionDao();
//    }

    //会话id
    @Bean
    public JavaUuidSessionIdGenerator javaUuidSessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    //缓存管理器
    @Bean
    public EhCacheManager cacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return ehCacheManager;
    }

    //rememberMeManager
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(SimpleCookie simpleCookie) {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        //rememberMe cookie加密的密钥默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        return cookieRememberMeManager;
    }

    @Bean
    public SimpleCookie cookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //单位秒 设置7天
        simpleCookie.setMaxAge(604800);
        return simpleCookie;
    }


    /*
     * shiroWeb
     * */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }


    /*
     * session监听
     * */

    @Bean("MySessionListener")
    public SessionListener sessionListener() {
        return new MySessionListener();
    }




}
