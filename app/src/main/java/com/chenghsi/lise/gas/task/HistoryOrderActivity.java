/*
package com.chenghsi.lise.gas.task;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.R;


public class HistoryOrderActivity extends AbstractList
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_history_order);
    }

    protected void reloadList()
    {
        HistoryClientOrdersAdapter apt = new HistoryClientOrdersAdapter(this);
        listView.setAdapter(apt);
    }

    public class HistoryClientOrdersAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public HistoryClientOrdersAdapter(Context context)
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

            View item = inflater.inflate(R.layout.adapter_item_history_order, parent, false);
            */
/*TextView tv_date = (TextView) item.findViewById(R.id.tv_date);
            TextView tv_capacity = (TextView) item.findViewById(R.id.tv_capacity);
            TextView tv_numBottle = (TextView) item.findViewById(R.id.tv_numBottle);

            tv_date.setText("104/05/05");
            tv_capacity.setText("20kg");
            tv_numBottle.setText("+10");*//*


            return item;
        }
    }
}
*/
