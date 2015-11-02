package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/10/29.
 */
public class TaskLists {
    private String order_prefer_time;
    private String order_task;
    private String order_phone;
    private String order_cylinders_list;
    private String customer_name;
    private String customer_addreess;
    private String order_accept;
    private String order_status;

    public TaskLists(String order_prefer_time, String order_task, String order_phone, String order_cylinders_list, String customer_name, String customer_addreess, String order_status, String order_accept) {
        setOrder_cylinders_list(order_cylinders_list);
        setOrder_phone(order_phone);
        setOrder_prefer_time(order_prefer_time);
        setOrder_task(order_task);
        setCustomer_name(customer_name);
        setCustomer_addreess(customer_addreess);
        setOrder_accept(order_accept);
        setOrder_status(order_status);
    }

    public String getOrder_prefer_time() {
        return order_prefer_time;
    }

    public void setOrder_prefer_time(String order_prefer_time) {
        this.order_prefer_time = order_prefer_time;
    }

    public String getOrder_task() {
        return order_task;
    }

    public void setOrder_task(String order_task) {
        this.order_task = order_task;
    }

    public String getOrder_phone() {
        return order_phone;
    }

    public void setOrder_phone(String order_phone) {
        this.order_phone = order_phone;
    }

    public String getOrder_cylinders_list() {
        return order_cylinders_list;
    }

    public void setOrder_cylinders_list(String order_cylinders_list) {
        this.order_cylinders_list = order_cylinders_list;
    }


    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_addreess() {
        return customer_addreess;
    }

    public void setCustomer_addreess(String customer_addreess) {
        this.customer_addreess = customer_addreess;
    }

    public String getOrder_accept() {
        return order_accept;
    }

    public void setOrder_accept(String order_accept) {
        this.order_accept = order_accept;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
