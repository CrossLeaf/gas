package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/11/12.
 */
public class DetailClientInfoList {
    String order_time;
    String order_task;
    String order_cylinders_list;
    String doddle_this_phase_degree;

    public DetailClientInfoList(String order_time, String order_task, String order_cylinders_list, String doddle_this_phase_degree) {
        this.order_time = order_time;
        this.order_task = order_task;
        this.order_cylinders_list = order_cylinders_list;
        this.doddle_this_phase_degree = doddle_this_phase_degree;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getOrder_task() {
        return order_task;
    }

    public void setOrder_task(String order_task) {
        this.order_task = order_task;
    }

    public String getOrder_cylinders_list() {
        return order_cylinders_list;
    }

    public void setOrder_cylinders_list(String order_cylinders_list) {
        this.order_cylinders_list = order_cylinders_list;
    }

    public String getDoddle_this_phase_degree() {
        return doddle_this_phase_degree;
    }

    public void setDoddle_this_phase_degree(String doddle_this_phase_degree) {
        this.doddle_this_phase_degree = doddle_this_phase_degree;
    }
}
