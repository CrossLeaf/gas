package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/11/12.
 */
public class ClientInfoList {
    String id;
    String name;
    String address;
    String[] phone;

    public ClientInfoList(String id, String name, String address, String[]phone) {
        setId(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getPhone() {
        return phone;
    }

    public void setPhone(String[] phone) {
        this.phone = phone;
    }
}
