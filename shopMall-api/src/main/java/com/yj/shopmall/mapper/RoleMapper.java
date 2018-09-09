package com.yj.shopmall.mapper;

import com.yj.shopmall.pojo.Role;

import java.util.List;

public interface RoleMapper {
    int addRole(Role role);
    int delRole(String[] ids);
    int updateRole(Role role);
    List<Role> findRole(Role role);
    int findRoleCount(Role role);
}
