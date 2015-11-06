package com.chenghsi.lise.gas;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.chenghsi.lise.gas.delivery.DeliveryActivity;
import com.chenghsi.lise.gas.delivery.DeliveryScheduleActivity;
import com.chenghsi.lise.gas.other.OtherActivity;
import com.chenghsi.lise.gas.task.NewTaskActivity;
import com.chenghsi.lise.gas.task.TaskActivity;


public class MainActivity extends TabActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");

        Intent intent = getIntent();

        tab1.setIndicator("任務");
        tab1.setContent(new Intent(this, NewTaskActivity.class).putExtra("userName", intent.getStringExtra("userName")));

        tab2.setIndicator("配送表");
        tab2.setContent(new Intent(this, DeliveryScheduleActivity.class));

        tab3.setIndicator("其他");
        tab3.setContent(new Intent(this, OtherActivity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

    }
}
