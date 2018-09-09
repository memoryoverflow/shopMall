package com.yj.shopmall.Shiro;

import com.yj.shopmall.redis.SessionRedis;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Collection;

@Configuration
public class RedisSessionDao extends AbstractSessionDAO {
    private final static Logger log = LoggerFactory.getLogger(Realm.class);

    @Autowired
    SessionRedis sessionRedis;


    //更新
    public void update(Session session) throws UnknownSessionException {
        //log.info("更新seesion,id=[{}]", session.getId().toString());
        try {
            sessionRedis.updateSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    public void delete(Session session) {
        log.info("删除seesion,id=[{}]", session.getId().toString());
        //删除缓存的
        sessionRedis.delSession(session);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        //log.info("获取存活的session");
        Collection<Session> activeSessions = sessionRedis.getActiveSessions();
        return activeSessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        //log.info("创建seesion,id=[{}]", session.getId().toString());
        try {
            sessionRedis.createSession(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = null;
        try {
            session = sessionRedis.getSession(sessionId.toString());
           //log.info("获取seesion"+ session.getId()+","+session.getHost()+","+session.getTimeout());
        } catch (Exception e) {

        }
        return session;
    }
}
