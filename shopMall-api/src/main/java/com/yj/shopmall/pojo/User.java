package com.yj.shopmall.pojo;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User extends BasePojo implements Serializable{
    private String uId;
    private String name;
    private String password;
    private String email;
    private String tel;
    private String sex;
    private Date createTime;

    private String isActivated; //-1 为默认状态 未激活 1为激活
    private String isFrozen;    //1 为默认状态 未冻结 -1为冻结
    private String img;

    private List<Address> uAddressList;

    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(String isActivated) {
        this.isActivated = isActivated;
    }

    public String getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("uId='").append(uId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", tel='").append(tel).append('\'');
        sb.append(", sex='").append(sex).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", isActivated=").append(isActivated);
        sb.append(", isFrozen=").append(isFrozen);
        sb.append(", img='").append(img).append('\'');
        sb.append(", uAddressList=").append(uAddressList);
        sb.append('}');
        return sb.toString();
    }



    public User() {
    }
    public User(String email) {
        this.email=email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Address> getuAddressList() {
        return uAddressList;
    }

    public void setuAddressList(List<Address> uAddressList) {
        this.uAddressList = uAddressList;
    }


}
