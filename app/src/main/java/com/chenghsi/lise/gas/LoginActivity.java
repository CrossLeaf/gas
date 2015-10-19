package com.chenghsi.lise.gas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity
{
    SharedPreferences sharedPreferences;
    EditText et_account;
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("LoginInfo",0);
        et_account = (EditText) findViewById(R.id.account);
        et_password = (EditText) findViewById(R.id.password);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(isLogin())
        {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClick_btn_login(View view)
    {
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();

        if(login(account, password))
        {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, R.string.login_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLogin()
    {
        return sharedPreferences.getBoolean("isLogin",false);
    }

    private boolean login(String account, String password)
    {
        // TODO login by DB

        sharedPreferences.edit()
                .putString("account", account)
                .putString("password", password)
                .putBoolean("isLogin", true)
                .commit();
        return true;
    }
}
