package com.chenghsi.lise.gas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;


public class LoginActivity extends Activity {
    SharedPreferences sp;
    EditText et_account;
    EditText et_password;
    String account;
    String password;
    String retSrc = "";

    private String staff_name;
    private String staff_id;

    Toast showToastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("login", "------login create-----");
        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);
        et_account = (EditText) findViewById(R.id.account);
        et_password = (EditText) findViewById(R.id.password);
        new LoginThread().start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("login", "------login resume-----");
        //呼叫讀取偏好資料
        readPref();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("login", "------login pause-----");

        //儲存偏好資料
        restorePref();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("login", "------login stop-----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("login", "------login destroy-----");
    }

    private void restorePref() {
        account = et_account.getText().toString();
        password = et_password.getText().toString();

        //取得偏好編輯模式
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("account", account);
        edit.putString("password", password);
        edit.putString("staff_id", staff_id);
        edit.putString("staff_name", staff_name);
        //確認儲存
        //使用apply()非同步更新作業
        edit.apply();
    }

    //讀取偏好資料
    public void readPref() {
        account = sp.getString("account", "");
        password = sp.getString("password", "");
        et_account.setText(account);
        et_password.setText(password);

    }

    //按鈕事件
    public void onClick_btn_login(View view) {
        Log.e("login", "account, password:" + et_account.getText().toString().isEmpty() + et_password.getText().toString().isEmpty());
        Log.e("login", "account text:" + et_account.getText().toString().equals(""));
        if (verification()) {
            /*Globals globals = (Globals)this.getApplicationContext();
            globals.setUser_id(staff_id);
            globals.setUser_name(staff_name);*/
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            if (showToastMessage!=null) {
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(this, R.string.login_failure, Toast.LENGTH_SHORT);
                showToastMessage.show();
            }else {
                showToastMessage = Toast.makeText(this, R.string.login_failure, Toast.LENGTH_SHORT);
                showToastMessage.show();
            }
        }
    }

    private class LoginThread extends Thread {
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

    /*驗證帳號密碼*/
    public boolean verification() {
        try {
            String account = et_account.getText().toString();
            String password = et_password.getText().toString();
            if (account.isEmpty() || password.isEmpty()) {
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(this, "帳號或密碼為空", Toast.LENGTH_SHORT);
                showToastMessage.show();
                return false;
            }
            JSONArray jsonArray = new JSONArray(retSrc);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (account.equals(jsonArray.getJSONArray(i).getString(9)) &&
                        password.equals(jsonArray.getJSONArray(i).getString(10))) {
                    staff_id = jsonArray.getJSONArray(i).getString(0);  //staff_id
                    staff_name = jsonArray.getJSONArray(i).getString(3);  //staff_name

                    Log.e("tag", "staff_id:"+staff_id);
                    Log.e("tag", "人名：" + staff_name);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("tag", "false");
        }
        return false;
    }
    /*驗證帳號密碼 結束*/



}
