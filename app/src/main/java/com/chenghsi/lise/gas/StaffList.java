package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/11/20.
 */
public class StaffList {
    private String staff_id;
    private String staff_name;

    public StaffList(String staff_id, String staff_name) {
        this.staff_id = staff_id;
        this.staff_name = staff_name;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }
}
