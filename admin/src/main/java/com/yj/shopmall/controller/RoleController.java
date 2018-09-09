package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.Role;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/mall")
public class RoleController {
    @Reference
    RoleService roleService;

    @RequestMapping("getRoles")
    public ResultLayui getRoles(Role role){
        ResultLayui<Role> roles = roleService.findRole(role);
        return roles;
    }

    @RequestMapping("addRole")
    public JsonResult addRole(Role role){
        role.setStatus(1);
        role.setCreatTime(new Date());
        return roleService.addRole(role);
    }

    @RequestMapping("delRole")
    public JsonResult delRole(String[] ids){
        //return roleService.delRole(ids);
        return JsonResult.ok();
    }

    @RequestMapping("updateRole")
    public JsonResult updateRole(Role role){
        //return roleService.updateRole(role);
        return JsonResult.ok();
    }

}
