package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/12/6.
 */
public class PaymentList {
    private String payment_id;
    private String payment_type;
    private String payment_status;
    private String payment_content;
    private String payment_money_cash;
    private String payment_build_date;

    public PaymentList(String payment_id, String payment_type, String payment_status,
                       String payment_content, String payment_money_cash, String payment_build_date) {
        this.payment_id = payment_id;
        this.payment_type = payment_type;
        this.payment_status = payment_status;
        this.payment_content = payment_content;
        this.payment_money_cash = payment_money_cash;
        this.payment_build_date = payment_build_date;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getPayment_content() {
        return payment_content;
    }

    public void setPayment_content(String payment_content) {
        this.payment_content = payment_content;
    }

    public String getPayment_money_cash() {
        return payment_money_cash;
    }

    public void setPayment_money_cash(String payment_money_cash) {
        this.payment_money_cash = payment_money_cash;
    }

    public String getPayment_build_date() {
        return payment_build_date;
    }

    public void setPayment_build_date(String payment_build_date) {
        this.payment_build_date = payment_build_date;
    }
}
