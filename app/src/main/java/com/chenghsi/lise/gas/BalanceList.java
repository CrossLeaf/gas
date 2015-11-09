package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/11/9.
 */
public class BalanceList {

    String name;
    String cylinders_list;
    String order_date;
    String money;

    public BalanceList(String name, String cylinders_list, String date, String money){
        setCylinders_list(cylinders_list);
        setMoney(money);
        setName(name);
        setOrder_date(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCylinders_list() {
        return cylinders_list;
    }

    public void setCylinders_list(String cylinders_list) {
        this.cylinders_list = cylinders_list;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
