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

    public void onClick_btn_barcodeScanning(View view)
    {
        //TODO
        //AlertDialog.Builder altBlgBuilder = AltDlgBuilder_choice();
        //altBlgBuilder.show();
        barcodeScanning_Dialog();
        /*Intent intent = new Intent();
        intent.setClass(OtherActivity.this, CaptureActivity.class);
        startActivity(intent);*/
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
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, NewClientInfoActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_navigation(View view)
    {
        intent.setClass(OtherActivity.this, RoutePlanningActivity.class);
        startActivity(intent);
    }

    public void barcodeScanning_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.barcodeScanning);
        String[] scane = {
                "出任務條碼 刷出", "出任務條碼 刷入",
                "出入氣條碼 刷出", "出入氣條碼 刷入",
                "檢驗場條碼 刷出", "檢驗場條碼 刷入"};
        builder.setItems(scane, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setClass(OtherActivity.this, CaptureActivity.class);
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
      /*public AlertDialog.Builder AltDlgBuilder_choice()
    {
        String[] options =
                {
                        "出任務條碼 刷出","出任務條碼 刷入",
                        "出入氣條碼 刷出","出入氣條碼 刷入",
                        "檢驗廠條碼 刷出","檢驗廠條碼 刷入"
                };

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case 0:
                        //metaHistoryItem.location = "公司";
                        break;
                    case 1:
                        //metaHistoryItem.location = "檢驗廠";
                        break;
                    case 2:
                        //metaHistoryItem.location = "客戶";
                        break;
                }
                //historyManager.addItem(metaHistoryItem);
                //renewListview();
            }
        };

        AlertDialog.Builder altBlgBuilder = new AlertDialog.Builder(OtherActivity.this);
        altBlgBuilder.setTitle("請選擇用途");
        altBlgBuilder.setCancelable(false);
        altBlgBuilder.setItems(options, listener);

        return altBlgBuilder;
    }*/
}
