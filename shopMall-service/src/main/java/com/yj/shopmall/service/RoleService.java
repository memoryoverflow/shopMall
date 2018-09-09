package com.yj.shopmall.service;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.pojo.Role;

public interface RoleService {
    JsonResult addRole(Role role);
    JsonResult delRole(String[] ids);
    JsonResult updateRole(Role role);
    ResultLayui<Role> findRole(Role role);
}
