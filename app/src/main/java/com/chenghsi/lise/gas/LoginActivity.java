package com.chenghsi.lise.gas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chenghsi.lise.gas.db.GasDB;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class LoginActivity extends Activity {
    SharedPreferences sp;
    EditText et_account;
    EditText et_password;
    String account;
    String password;
    String userName;
    String retSrc = "";
    String usn;
    static final int STAFF_COUNT = 9;
    static final int STAFF_PASSWORD = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);
        et_account = (EditText) findViewById(R.id.account);
        et_password = (EditText) findViewById(R.id.password);
        new LoginThread().start();

        //呼叫讀取偏好資料
        readPref();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //儲存偏好資料
        restorePref();
    }

    private void restorePref() {
        account = et_account.getText().toString();
        password = et_password.getText().toString();

        //取得偏好編輯模式
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("account", account);
        edit.putString("password", password);

        //確認儲存
        //使用apply()非同步更新作業
        edit.apply();
    }

    //讀取偏好資料
    private void readPref() {
        account = sp.getString("account", "");
        password = sp.getString("password", "");
        et_account.setText(account);
        et_password.setText(password);

    }

    //判斷帳號密碼是否正確
    /*private boolean isLogin(String usn,String pwd){
        if (usn.equals()){

        }
    }*/

    //按鈕事件
    public void onClick_btn_login(View view) {
//        String account = et_account.getText().toString();
//        String password = et_password.getText().toString();
//        isAccount(retSrc);
        if (isAccount()) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            intent.putExtra("userName", getUserName());
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.login_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginThread extends Thread{
        @Override
        public void run() {
            String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=staff";
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                Log.e("retSrc", "讀取 JSON-1...");
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    Log.e("retSrc", "讀取 JSON-2...");
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        retSrc = EntityUtils.toString(resEntity);
                        Log.e("retSrc", retSrc);
                    } else {
                        retSrc = "Did not work!";
                    }

                } catch (Exception e) {
                    Log.e("retSrc", "讀取JSON Error...");

                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
        }
    }

    public boolean isAccount(){
        try {
            String account = et_account.getText().toString();
            String password = et_password.getText().toString();
            JSONArray jsonArray = new JSONArray(retSrc);
            for(int i = 0; i < jsonArray.length(); i++) {
                if (account.equals(jsonArray.getJSONArray(i).getString(9)) &&
                        password.equals(jsonArray.getJSONArray(i).getString(10)))
                {
                    userName = jsonArray.getJSONArray(i).getString(3);
                    Log.e("tag", "比對到第"+i+"筆");
                    storeName(jsonArray.getJSONArray(i).getString(3));
                    Log.e("tag", "人名："+userName);
                    return true;
                }
            }
        }catch (Exception e){
            Log.e("tag", "false");
        }
        return false;
    }



    private boolean login(String account, String password) {
        // TODO login by DB
        sp.edit()
                .putString("account", account)
                .putString("password", password)
                .putBoolean("isLogin", true)
                .commit();
        return true;
    }

    public void storeName(String name){
        this.usn = name;
    }

    public String getUserName (){
        Log.e("tag", "user Name:"+usn);
        return usn;
    }
}
