package com.chenghsi.lise.gas.other;

import android.app.Activity;
//import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chenghsi.lise.gas.R;
import com.google.zxing.client.android.CaptureActivity;

import java.util.Arrays;
import java.util.List;


public class OtherActivity extends Activity
{
    protected Toolbar toolbar;
    Intent intent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_other);

    }

    //出入氣按鈕
    public void onClick_btn_barcodeScanning(View view)
    {

//        barcodeScanning_Dialog();
//        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, FactoryActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_meterReading(View view)
    {
        intent.setClass(OtherActivity.this, MeterReadingActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_priceGas(View view)
    {
        intent.setClass(OtherActivity.this, NewGasPriceActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_balancing(View view)
    {
        intent.setClass(OtherActivity.this, NewBalancingActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_clientInfo(View view)
    {
//        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, NewClientInfoActivity.class);
        startActivity(intent);
    }

    //掃描
    public void onClick_btn_navigation(View view)
    {
        /*intent.setClass(OtherActivity.this, RoutePlanningActivity.class);
        startActivity(intent);*/
        barcodeScanning_Dialog();
    }

    public void barcodeScanning_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.barcodeScanning);
        String[] scane = {"檢驗場條碼 刷入",
                "檢驗場條碼 刷出", "更新狀態"};

        builder.setItems(scane, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    //掃入
                    case 0:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        break;
                    //掃出
                    case 1:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        intent.putExtra("flag", 2);
                        startActivity(intent);
                        break;
                    //更新狀態
                    case 2:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        intent.putExtra("flag", 3);
                        startActivity(intent);
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

}
