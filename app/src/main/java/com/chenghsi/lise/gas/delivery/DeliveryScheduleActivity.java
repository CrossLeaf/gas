package com.chenghsi.lise.gas.delivery;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chenghsi.lise.gas.R;

import java.util.Calendar;
import java.util.Date;

public class DeliveryScheduleActivity extends Activity {
    protected Toolbar toolbar;
    CalendarView calendarView;
    ImageButton fab;
    int Myear, Mmonth, Mday;
    Calendar c = Calendar.getInstance();
    int prevDay = c.get(Calendar.DAY_OF_MONTH);
    int prevMonth = c.get(Calendar.MONTH)+1;
    int prevYear = c.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_delivery_schedule);
//      toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        fab = (ImageButton) findViewById(R.id.fab);
        calendarView = (CalendarView) findViewById(R.id.calendarView);

        Log.e("calendar", "MaxDate:" + calendarView.getMaxDate());

//        Date d = new Date(prevYear, prevMonth, prevDay);
//        c.setTime(d);
        Myear = prevYear;
        Mmonth = prevMonth;
        Mday = prevDay;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {

                Log.e("calendar", "點擊日期");
                Myear = year;
                Mmonth = month + 1;
                Mday = dayOfMonth;
//                Toast.makeText(DeliveryScheduleActivity.this, "日期：" + Myear + "/" + Mmonth + "/" + Mday, Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String date = Myear + "/" + Mmonth + "/" + Mday;
                    Toast.makeText(DeliveryScheduleActivity.this, date, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("year", Myear);
                bundle.putInt("month", Mmonth);
                bundle.putInt("day", Mday);
                intent.setClass(DeliveryScheduleActivity.this, DeliveryScheduleDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
