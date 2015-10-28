package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/10/26.
 */
public class GasPriceList {
    public String PRICE_ID;
    public String PRICE_CAPACITY;
    public String PRICE_20;
    public String PRICE_16;
    public String PRICE_BULID_DATE;
    public String PRICE_CHANGE_DATE;
    public String PRICE_REMARK;

    GasPriceList(String p20, String p16, String bulidDate, String remark) {
        setPRICE_20(p20);
        setPRICE_16(p16);
        setPRICE_BULID_DATE(bulidDate);
        setPRICE_REMARK(remark);
    }

    public String getPRICE_ID() {
        return PRICE_ID;
    }

    public void setPRICE_ID(String PRICE_ID) {
        this.PRICE_ID = PRICE_ID;
    }

    public String getPRICE_CAPACITY() {
        return PRICE_CAPACITY;
    }

    public void setPRICE_CAPACITY(String PRICE_CAPACITY) {
        this.PRICE_CAPACITY = PRICE_CAPACITY;
    }

    public String getPRICE_20() {
        return PRICE_20;
    }

    public void setPRICE_20(String PRICE_20) {
        this.PRICE_20 = PRICE_20;
    }

    public String getPRICE_16() {
        return PRICE_16;
    }

    public void setPRICE_16(String PRICE_16) {
        this.PRICE_16 = PRICE_16;
    }

    public String getPRICE_BULID_DATE() {
        return PRICE_BULID_DATE;
    }

    public void setPRICE_BULID_DATE(String PRICE_BULID_DATE) {
        this.PRICE_BULID_DATE = PRICE_BULID_DATE;
    }

    public String getPRICE_CHANGE_DATE() {
        return PRICE_CHANGE_DATE;
    }

    public void setPRICE_CHANGE_DATE(String PRICE_CHANGE_DATE) {
        this.PRICE_CHANGE_DATE = PRICE_CHANGE_DATE;
    }

    public String getPRICE_REMARK() {
        return PRICE_REMARK;
    }

    public void setPRICE_REMARK(String PRICE_REMARK) {
        this.PRICE_REMARK = PRICE_REMARK;
    }
}
