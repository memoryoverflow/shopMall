package com.yj.shopmall.redis;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.service.UserServerce;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.yj.shopmall.constant.Constant.REDIS_SESSION_Prefix;
import static com.yj.shopmall.constant.Constant.USER_MSG;

@Component
public class SessionRedis {
    private static final Logger logger = LoggerFactory.getLogger(SessionRedis.class);
    @Autowired
    StringRedisTemplate redisTemplate;
    @Reference
    UserServerce userServerce;


    //存储session
    public void createSession(Session session) {
        try {
            redisTemplate.opsForValue().set(REDIS_SESSION_Prefix+session.getId().toString(),serialize(session));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //删除session
    public void delSession(Session session) {
        if (isHaveSeesionKey(session)) {
            redisTemplate.delete(REDIS_SESSION_Prefix+session.getId().toString());
            //自动退出或者过期删除数据库的
            String[] id = new String[]{session.getId().toString()};
            userServerce.delOnlineUser(id);
        }
    }

    //更新session
    public void updateSession(Session session) {
        if (session == null) {
            return;
        }
        if (isHaveSeesionKey(session)) {
            redisTemplate.opsForValue().set(REDIS_SESSION_Prefix+session.getId().toString(), serialize(session));
            redisTemplate.expire(REDIS_SESSION_Prefix+session.getId(),30,TimeUnit.MINUTES);

            redisTemplate.expire(USER_MSG+"["+session.getId()+"]",30,TimeUnit.MINUTES);
            //logger.info("### 更新session=[{}]", session.getLastAccessTime());
        }
    }

    // 获取活跃的session，可以用来统计在线人数，如果要实现这个功能，
    // 可以在将session加入redis时指定一个session前缀，统计的时候则使用keys("session-prefix*")的方式来模糊查找redis中所有的session集合
    public Collection<Session> getActiveSessions() {
        Set<String> keys = redisTemplate.keys("REDIS-SESSIONID:*");
        Collection<Session> sessions = new ArrayList<>();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String str = it.next();
            Session session = getSessionByName(str);
            sessions.add(session);
        }
        return sessions;
    }


    //获取一个session
    public Session getSession(String SessionId){
        Boolean aBoolean = redisTemplate.hasKey(REDIS_SESSION_Prefix + SessionId.toString());
        if (aBoolean) {
            Session session = (Session)deserialize(redisTemplate.opsForValue().get(REDIS_SESSION_Prefix+SessionId));
            return session;
        }
        return null;
    }


    //获取一个session
    public Session getSessionByName(String RedisId){
        Boolean aBoolean = redisTemplate.hasKey(RedisId);
        if (aBoolean) {
            Session session = (Session)deserialize(redisTemplate.opsForValue().get(RedisId));
            //logger.info("通过键获取，sesion=[{}]",redisTemplate.opsForValue().get(RedisId));
            return session;
        }
        return null;
    }





    /*
     * 判断是否存在
     * */
    public boolean isHaveSeesionKey(Session session) {
        Boolean aBoolean = redisTemplate.hasKey(REDIS_SESSION_Prefix+session.getId().toString());
        return aBoolean;
    }

    private static Object deserialize(String str) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(Base64.decode(str));
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error", e);
        } finally {
            try {
                ois.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static String serialize(Object obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return Base64.encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("serialize session error", e);
        } finally {
            try {
                oos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
