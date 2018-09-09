package com.yj.shopmall.pojo;

import java.io.Serializable;

public class SeckillExplain implements Serializable{
    private String p_id;
    private String id;
    private String name;
    private String explain1;
    private String explain2;
    private String explain3;
    private String img1;
    private String img2;
    private String img3;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SeckillExplain{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", explain1='").append(explain1).append('\'');
        sb.append(", explain2='").append(explain2).append('\'');
        sb.append(", explain3='").append(explain3).append('\'');
        sb.append(", img1='").append(img1).append('\'');
        sb.append(", img2='").append(img2).append('\'');
        sb.append(", img3='").append(img3).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain1() {
        return explain1;
    }

    public void setExplain1(String explain1) {
        this.explain1 = explain1;
    }

    public String getExplain2() {
        return explain2;
    }

    public void setExplain2(String explain2) {
        this.explain2 = explain2;
    }

    public String getExplain3() {
        return explain3;
    }

    public void setExplain3(String explain3) {
        this.explain3 = explain3;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }
}
