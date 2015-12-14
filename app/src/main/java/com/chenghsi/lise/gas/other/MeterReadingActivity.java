package com.chenghsi.lise.gas.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.db.GasDB;
import com.chenghsi.lise.gas.delivery.DeliveryScheduleActivity;
import com.chenghsi.lise.gas.task.DetailedTaskActivity;

import org.json.JSONArray;


public class MeterReadingActivity extends AbstractList
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_meter_reading);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
        gasDB.setTaskListener(asyncTaskFinishListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gasDB.startAsyncTask("MeterReading");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                MeterReadingListAdapter apt = new MeterReadingListAdapter(MeterReadingActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
//                        Intent intent = new Intent();
//                        intent.setClass(DeliveryListActivity.this, DeliveryScheduleActivity.class);
//                        startActivity(intent);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public class MeterReadingListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public MeterReadingListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View item = inflater.inflate(R.layout.adapter_item_meter_reading, parent, false);

            TextView text_clientName = (TextView) item.findViewById(R.id.tv_clientName);
            TextView text_address = (TextView) item.findViewById(R.id.tv_address);

            text_clientName.setText("好好迪");
            text_address.setText("新北市信義區基隆路一段400號十樓之二");


            return item;
        }
    }
}
