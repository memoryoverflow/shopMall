package com.yj.shopmall.pojo;

import com.yj.shopmall.pojo.Address;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order extends BasePojo implements Serializable{
    private String orderId ;//订单id
    private Date createTime ;//下单时间
    private double totalPrice ;//当前订单的总金额
    private String address_Id ;//地址id
    private String userId ;//下单人
    private String isPayfor ;//是否支付

    private List<UserProduct> userProducts;

    private UserProduct userProduct;

    private ReturnGoods returnGoods;

    //地址信息
    private Address address;




    public ReturnGoods getReturnGoods() {
        return returnGoods;
    }

    public void setReturnGoods(ReturnGoods returnGoods) {
        this.returnGoods = returnGoods;
    }



    public Order() {
    }

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public Order(String orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("orderId='").append(orderId).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", address_Id='").append(address_Id).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", isPayfor='").append(isPayfor).append('\'');
        sb.append(", userProducts=").append(userProducts);
        sb.append(", userProduct=").append(userProduct);
        sb.append(", returnGoods=").append(returnGoods);
        sb.append(", address=").append(address);
        sb.append('}');
        return sb.toString();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress_Id() {
        return address_Id;
    }

    public void setAddress_Id(String address_Id) {
        this.address_Id = address_Id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsPayfor() {
        return isPayfor;
    }

    public void setIsPayfor(String isPayfor) {
        this.isPayfor = isPayfor;
    }

    public List<UserProduct> getUserProducts() {
        return userProducts;
    }

    public void setUserProducts(List<UserProduct> userProducts) {
        this.userProducts = userProducts;
    }

    public UserProduct getUserProduct() {
        return userProduct;
    }

    public void setUserProduct(UserProduct userProduct) {
        this.userProduct = userProduct;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    @Override
    public int getCurrPage() {
        return super.getCurrPage();
    }

    @Override
    public void setCurrPage(int currPage) {
        super.setCurrPage(currPage);
    }

    @Override
    public int getPageSize() {
        return super.getPageSize();
    }

    @Override
    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
    }
}
