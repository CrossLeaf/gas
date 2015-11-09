package com.chenghsi.lise.gas.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.db.GasDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class BalancingActivity extends AbstractList
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_balancing);
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
        gasDB.startAsyncTask("Balancing");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                PriceGasListAdapter apt = new PriceGasListAdapter(BalancingActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        intent.setClass(BalancingActivity.this, DetailedBalancingActivity.class);
                        startActivity(intent);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private class PriceGasListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public PriceGasListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 5;
        }

        @Override
        public Object getItem(int position) {
            try
            {
                JSONArray item_balancing = gasDB.getTableItemByIndex(GasDB.BALANCING, position);
                JSONArray item_customer = gasDB.getTableItemByIndex(GasDB.CUSTOMER, position);
                ArrayList<String> result = new ArrayList<>();
                result.add( item_customer.getString(Constant.CUSTOMER_NAME) );
                result.add( item_customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS) );
                result.add( "phone" ); // TODO

                return result.toArray(new String[result.size()]);
            }
            catch(JSONException e)
            {
                Log.e("SimpleTaskListAdapter", e.toString());
            }
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
            View item = inflater.inflate(R.layout.adapter_item_balancing, parent, false);

            // TODO 客戶名稱
            ((TextView) item.findViewById(R.id.tv_name)).setText("范先生");
            ((TextView) item.findViewById(R.id.tv_phones)).setText("0932123456");
            ((TextView) item.findViewById(R.id.tv_address)).setText("花蓮縣吉安鄉民治路九巷15號");
            ((TextView) item.findViewById(R.id.tv_date)).setText("2015/10/20");
            ((TextView) item.findViewById(R.id.tv_money)).setText("5030" + " 元");



            return item;
        }
    }
}
