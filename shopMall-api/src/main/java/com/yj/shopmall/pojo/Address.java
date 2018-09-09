package com.yj.shopmall.pojo;

import java.io.Serializable;

public class Address implements Serializable{
    private String addressId;
    private String user_id;
    private String address;
    private String recUser;
    private String recTel;
    private int isDefault;
    private int isdel;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Address{");
        sb.append("addressId='").append(addressId).append('\'');
        sb.append(", user_id='").append(user_id).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", recUser='").append(recUser).append('\'');
        sb.append(", recTel='").append(recTel).append('\'');
        sb.append(", isDefault=").append(isDefault);
        sb.append(", isdel=").append(isdel);
        sb.append('}');
        return sb.toString();
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecUser() {
        return recUser;
    }

    public void setRecUser(String recUser) {
        this.recUser = recUser;
    }

    public String getRecTel() {
        return recTel;
    }

    public void setRecTel(String recTel) {
        this.recTel = recTel;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }
}
