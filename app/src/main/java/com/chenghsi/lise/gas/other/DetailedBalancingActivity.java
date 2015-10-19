package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.chenghsi.lise.gas.R;


public class DetailedBalancingActivity extends Activity
{
    protected Toolbar toolbar;
    private View R_name;
    private View R_address;
    private View R_phones;
    private View R_date;
    private View R_indexed_task;
    private View R_price_recommand;
    private View R_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_balancing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_detailed_balancing);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        R_name = findViewById(R.id.tv2_name);
        R_address = findViewById(R.id.tv2_address);
        R_phones = findViewById(R.id.tv_spr_phones);
        R_date = findViewById(R.id.tv2_date);
        R_indexed_task = findViewById(R.id.indexed_task);
        R_price_recommand = findViewById(R.id.tv2_price_recommand);
        R_receive = findViewById(R.id.tvi_receive);

        // Set title

        ((TextView)R_name.findViewById(R.id.text1)).setText("姓名");
        ((TextView)R_address.findViewById(R.id.text1)).setText("地址");
        ((TextView)R_phones.findViewById(R.id.title)).setText("電話");
        ((TextView)R_date.findViewById(R.id.text1)).setText("日期");
        ((TextView)R_price_recommand.findViewById(R.id.text1)).setText("應收");
        ((TextView)R_receive.findViewById(R.id.text)).setText("實收");
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // TODO Test data
        ((TextView)R_name.findViewById(R.id.text2)).setText("王先生");
        ((TextView)R_address.findViewById(R.id.text2)).setText("新北市信義區基隆路400號10樓之2");

        String phones[] = {"0931234567","0800222333","1234999333"};
        ArrayAdapter apt = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //(Spinner) spr_clientPhones.setAdapter(apt);
        ((Spinner)R_phones.findViewById(R.id.spinner)).setAdapter(apt);


        ((TextView)R_date.findViewById(R.id.text2)).setText("2015/10/20");
        ((TextView)R_price_recommand.findViewById(R.id.text2)).setText("5356"+" 元");
    }
}
