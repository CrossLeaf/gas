package com.chenghsi.lise.gas.task;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.R;


public class HistoryBottleActivity extends AbstractList
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_history_bottle);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    protected void reloadList()
    {
        HistoryBottleAdapter apt = new HistoryBottleAdapter(this);
        listView.setAdapter(apt);
    }

    public class HistoryBottleAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public HistoryBottleAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 30;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View item = inflater.inflate(R.layout.adapter_item_history_bottle, parent, false);

            TextView tv_date = (TextView) item.findViewById(R.id.tv_date);
            TextView tv_capacity = (TextView) item.findViewById(R.id.tv_capacity);
            TextView tv_numBottle = (TextView) item.findViewById(R.id.tv_numBottle);

            tv_date.setText("104/05/05");
            tv_capacity.setText("20kg");
            tv_numBottle.setText("+10");

            return item;
        }
    }
}