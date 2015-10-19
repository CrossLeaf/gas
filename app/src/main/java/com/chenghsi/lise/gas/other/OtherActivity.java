package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chenghsi.lise.gas.R;
import com.google.zxing.client.android.CaptureActivity;


public class OtherActivity extends Activity
{
    protected Toolbar toolbar;

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
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, CaptureActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_meterReading(View view)
    {
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, MeterReadingActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_priceGas(View view)
    {
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, GasPriceActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_balancing(View view)
    {
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, BalancingActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_clientInfo(View view)
    {
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, ClientInfoActivity.class);
        startActivity(intent);
    }

    public void onClick_btn_navigation(View view)
    {
        Intent intent = new Intent();
        intent.setClass(OtherActivity.this, RoutePlanningActivity.class);
        startActivity(intent);
    }

    public AlertDialog.Builder AltDlgBuilder_choice()
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
    }
}
