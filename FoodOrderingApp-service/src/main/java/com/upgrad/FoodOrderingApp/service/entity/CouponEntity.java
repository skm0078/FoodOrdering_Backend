package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(
                name = "getCouponByCouponName",
                query = "SELECT c FROM CouponEntity c WHERE c.couponName = :coupon_name"),
        @NamedQuery(
                name = "getCouponByCouponId",
                query = "SELECT c FROM  CouponEntity c WHERE c.uuid = :uuid"),
})
public class CouponEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "COUPON_NAME")
    @Size(max = 255)
    private String couponName;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "percent")
    private int percent;

    public CouponEntity() {}

    public CouponEntity(String couponId, String myCoupon, int i) {
        this.uuid = couponId;
        this.couponName = myCoupon;
        this.percent = i;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    //    @Override
    //    public int hashCode() {
    //        return new HashCodeBuilder().append(this).hashCode();
    //    }
    //
    //    @Override
    //    public String toString() {
    //        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    //    }

}
