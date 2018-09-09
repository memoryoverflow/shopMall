package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class Role extends BasePojo implements Serializable{
    private int id;
    private String roleChar;
    private String roleName;
    private int status;
    private Date creatTime;
    private String remark;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Role{");
        sb.append("id=").append(id);
        sb.append(", r_uId='").append(roleChar).append('\'');
        sb.append(", roleName='").append(roleName).append('\'');
        sb.append(", status=").append(status);
        sb.append(", creatTime=").append(creatTime);
        sb.append(", remark=").append(remark);
        sb.append('}');
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleChar() {
        return roleChar;
    }

    public void setRoleChar(String roleChar) {
        this.roleChar = roleChar;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
