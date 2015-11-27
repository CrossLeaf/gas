package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/10/29.
 */
public class TaskLists {
    private String order_doddle_id;
    private String order_prefer_time;
    private String order_task;
    private String order_phone;
    private String order_cylinders_list;
    private String customer_name;
    private String customer_address;
    private String customer_settle_type;
    private String order_doddle_accept;
    private String order_doddle_status;
    private String customer_id;
    private String order_should_money;
    private String order_gas_residual;


    public TaskLists(String order_doddle_id, String order_prefer_time, String order_task,
                     String order_phone, String order_cylinders_list, String customer_name,
                     String customer_address, String customer_settle_type, String order_should_money, String order_doddle_status,
                     String order_doddle_accept, String customer_id, String order_gas_residual) {
        setOrder_doddle_id(order_doddle_id);
        setOrder_cylinders_list(order_cylinders_list);
        setOrder_phone(order_phone);
        setOrder_prefer_time(order_prefer_time);
        setOrder_task(order_task);
        setCustomer_name(customer_name);
        setCustomer_address(customer_address);
        setCustomer_settle_type(customer_settle_type);
        setOrder_doddle_accept(order_doddle_accept);
        setOrder_doddle_status(order_doddle_status);
        setCustomer_id(customer_id);
        setOrder_should_money(order_should_money);
        setOrder_gas_residual(order_gas_residual);
    }

    public String getOrder_doddle_id() {
        return order_doddle_id;
    }

    public void setOrder_doddle_id(String order_doddle_id) {
        this.order_doddle_id = order_doddle_id;
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

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_settle_type() {
        return customer_settle_type;
    }

    public void setCustomer_settle_type(String customer_settle_type) {
        this.customer_settle_type = customer_settle_type;
    }

    public String getOrder_doddle_accept() {
        return order_doddle_accept;
    }

    public void setOrder_doddle_accept(String order_doddle_accept) {
        this.order_doddle_accept = order_doddle_accept;
    }

    public String getOrder_doddle_status() {
        return order_doddle_status;
    }

    public void setOrder_doddle_status(String order_doddle_status) {
        this.order_doddle_status = order_doddle_status;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getOrder_should_money() {
        return order_should_money;
    }

    public void setOrder_should_money(String order_should_money) {
        this.order_should_money = order_should_money;
    }

    public String getOrder_gas_residual() {
        return order_gas_residual;
    }

    public void setOrder_gas_residual(String order_gas_residual) {
        this.order_gas_residual = order_gas_residual;
    }
}
