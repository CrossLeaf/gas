package com.chenghsi.lise.gas;

import android.app.Application;
import android.util.Log;

/**
 * Created by MengHan on 2015/12/9.
 */
public class Globals extends Application {
    private static String user_id;
    private static String user_name;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        Log.e("global", user_id);
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        Log.e("global", user_name);
        this.user_name = user_name;
    }
}
