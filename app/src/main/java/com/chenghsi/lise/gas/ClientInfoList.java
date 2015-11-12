package com.chenghsi.lise.gas;

/**
 * Created by MengHan on 2015/11/12.
 */
public class ClientInfoList {
    String id;
    String name;
    String address;

    public ClientInfoList(String id, String name, String address) {
        setId(id);
        setName(name);
        setAddress(address);
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
}
