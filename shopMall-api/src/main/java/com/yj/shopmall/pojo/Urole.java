package com.yj.shopmall.pojo;

import java.io.Serializable;

public class Urole implements Serializable{
    private int id;
    private String m_uId;
    private int m_rId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getM_uId() {
        return m_uId;
    }

    public void setM_uId(String m_uId) {
        this.m_uId = m_uId;
    }

    public int getM_rId() {
        return m_rId;
    }

    public void setM_rId(int m_rId) {
        this.m_rId = m_rId;
    }
}
