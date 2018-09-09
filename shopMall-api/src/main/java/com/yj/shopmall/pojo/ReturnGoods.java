package com.yj.shopmall.pojo;

import java.io.Serializable;
import java.util.Date;

public class ReturnGoods extends BasePojo implements Serializable {
    private int id;
    private String return_oId;
    private String reason;
    private String remark;
    private Date returnTime;
    private String img1;
    private String img2;
    private String img3;
    private int return_status;
    private String returnContent;
    private int isdel;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReturnGoods{");
        sb.append("id=").append(id);
        sb.append(", return_oId='").append(return_oId).append('\'');
        sb.append(", reason='").append(reason).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", returnTime=").append(returnTime);
        sb.append(", img1='").append(img1).append('\'');
        sb.append(", img2='").append(img2).append('\'');
        sb.append(", img3='").append(img3).append('\'');
        sb.append(", return_status=").append(return_status);
        sb.append(", returnContent='").append(returnContent).append('\'');
        sb.append(", isdel=").append(isdel);
        sb.append('}');
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReturn_oId() {
        return return_oId;
    }

    public void setReturn_oId(String return_oId) {
        this.return_oId = return_oId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
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

    public int getReturn_status() {
        return return_status;
    }

    public void setReturn_status(int return_status) {
        this.return_status = return_status;
    }

    public String getReturnContent() {
        return returnContent;
    }

    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }
}
