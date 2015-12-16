package com.chenghsi.lise.gas;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

//import com.chenghsi.lise.gas.delivery.DeliveryActivity;
import com.chenghsi.lise.gas.delivery.DeliveryScheduleActivity;
import com.chenghsi.lise.gas.other.OtherActivity;
import com.chenghsi.lise.gas.task.NewTaskActivity;
//import com.chenghsi.lise.gas.task.TaskActivity;


public class MainActivity extends TabActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Main", "------Main create----");
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");


        tab1.setIndicator("任務");
        tab1.setContent(new Intent(this, NewTaskActivity.class));

        tab2.setIndicator("配送表");
        tab2.setContent(new Intent(this, DeliveryScheduleActivity.class));

        tab3.setIndicator("其他");
        tab3.setContent(new Intent(this, OtherActivity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Main", "------Main resume----");
        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);
        String user_name = sp.getString("staff_name", null);
        String user_id = sp.getString("staff_id", null);
        if (user_id == null || user_name == null ||
                user_id.isEmpty() || user_name.isEmpty()) {
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            Log.e("Main", "startActivity");
            MainActivity.this.finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Main", "------Main pause----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Main", "------Main stop----");
    }
}
