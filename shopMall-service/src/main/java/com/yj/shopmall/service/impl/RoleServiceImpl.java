package com.yj.shopmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.mapper.RoleMapper;
import com.yj.shopmall.pojo.Role;
import com.yj.shopmall.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@org.springframework.stereotype.Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    RoleMapper roleMapper;

    @Override
    public JsonResult addRole(Role role) {
        try {
            roleMapper.addRole(role);
        } catch (Exception e) {
            logger.info("添加角色异常=[{}]",e);
            throw new RuntimeException("添加角色异常！");
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult delRole(String[] ids) {

        try {
            roleMapper.delRole(ids);
        } catch (Exception e) {
            logger.info("删除角色异常=[{}]",e);
            throw new RuntimeException("删除角色异常！");
        }
            return JsonResult.ok();
    }

    @Override
    public JsonResult updateRole(Role role) {

        try {
            roleMapper.updateRole(role);
        } catch (Exception e) {
            logger.info("更新角色异常=[{}]",e);
            throw new RuntimeException("更新角色异常！");
        }

        return JsonResult.ok();
    }

    @Override
    public ResultLayui<Role> findRole(Role role) {
        int page = role.getPage();
        int pageStart = (page - 1) * role.getLimit();
        role.setPage(pageStart);
        List<Role> roleList = roleMapper.findRole(role);
        int Count = roleMapper.findRoleCount(role);
        return ResultLayui.jsonLayui(0, "", Count, roleList);
    }
}
