package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/10/26.
 */
public class GasPriceList {
    public String priceId;
    public String priceCapacity;
    public String price20;
    public String price16;
    public String priceBulidDate;
    public String priceChangeDate;
    public String priceRemark;

    GasPriceList(String p20, String p16, String bulidDate, String remark) {
        setPrice20(p20);
        setPrice16(p16);
        setPriceBulidDate(bulidDate);
        setPriceRemark(remark);
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getPriceCapacity() {
        return priceCapacity;
    }

    public void setPriceCapacity(String priceCapacity) {
        this.priceCapacity = priceCapacity;
    }

    public String getPrice20() {
        return price20;
    }

    public void setPrice20(String price20) {
        this.price20 = price20;
    }

    public String getPrice16() {
        return price16;
    }

    public void setPrice16(String price16) {
        this.price16 = price16;
    }

    public String getPriceBulidDate() {
        return priceBulidDate;
    }

    public void setPriceBulidDate(String priceBulidDate) {
        this.priceBulidDate = priceBulidDate;
    }

    public String getPriceChangeDate() {
        return priceChangeDate;
    }

    public void setPriceChangeDate(String priceChangeDate) {
        this.priceChangeDate = priceChangeDate;
    }

    public String getPriceRemark() {
        return priceRemark;
    }

    public void setPriceRemark(String priceRemark) {
        this.priceRemark = priceRemark;
    }
}
