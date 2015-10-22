package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.TestData;


public class DetailedTaskActivity extends Activity {
    protected Toolbar toolbar;
    private View R_name;
    private View R_address;
    private View R_remnant;
    private View R_allowance;
    private View R_receive;
    private View R_clientPhones;
    private int position;
    private String clientName;
    private String address;
    private String phones;
    private String contents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        Bundle bundle = this.getIntent().getExtras();
        clientName = bundle.getString("clientName");
        address = bundle.getString("address");
        phones = bundle.getString("phones");
        contents = bundle.getString("contents");
        Log.e("DetailedTask", "" + position);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_detailed_task);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        R_name = findViewById(R.id.tv2_name);
        R_address = findViewById(R.id.tv2_address);
        R_remnant = findViewById(R.id.tvi_remnant);
        R_allowance = findViewById(R.id.tvi_allowance);
        R_receive = findViewById(R.id.tvi_receive);
        R_clientPhones = findViewById(R.id.tv_spr_phones);

        // Set title

        ((TextView) R_name.findViewById(R.id.text1)).setText(R.string.name);
        ((TextView) R_address.findViewById(R.id.text1)).setText(R.string.address);
        ((TextView) R_remnant.findViewById(R.id.text)).setText(R.string.remnant);
        ((TextView) R_allowance.findViewById(R.id.text)).setText(R.string.allowance);
        ((TextView) R_receive.findViewById(R.id.text)).setText(R.string.should_receive);
        ((TextView) R_clientPhones.findViewById(R.id.title)).setText(R.string.phone);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO Test data
        ((TextView)R_name.findViewById(R.id.text2)).setText(clientName);
        ((TextView)R_address.findViewById(R.id.text2)).setText(address);
//        ((TextView)R_name.findViewById(R.id.text2)).setText(TestData.name[position]);
//        ((TextView)R_address.findViewById(R.id.text2)).setText(TestData.address[position]);

        String phones[] = {"0931234567", "0800222333", "1234999333"};
        ArrayAdapter apt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //(Spinner) spr_clientPhones.setAdapter(apt);
        ((Spinner) R_clientPhones.findViewById(R.id.spinner)).setAdapter(apt);

    }
}
