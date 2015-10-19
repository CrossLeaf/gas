package com.chenghsi.lise.gas.other;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.db.GasDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class GasPriceActivity extends AbstractList
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_gas_price);
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
        gasDB.startAsyncTask("GasPricing");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                PriceGasListAdapter apt = new PriceGasListAdapter(GasPriceActivity.this);
                listView.setAdapter(apt);
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
        public int getCount()
        {
            return gasDB.getTable(GasDB.PRICE).length();
        }

        @Override
        public Object getItem(int position)
        {
            try
            {
                JSONArray item_price = gasDB.getTableItemByIndex(GasDB.PRICE, position);

                // The adapted data
                ArrayList<String> result = new ArrayList<>();
                result.add( item_price.getString(Constant.PRICE_BUILT_TIME) );
                result.add( item_price.getString(Constant.PRICE_REMARK) ); //TODO
                result.add( item_price.getString(Constant.PRICE_UNIVALENT) );

                return result.toArray(new String[result.size()]);
            }
            catch(JSONException e)
            {
                Log.e("SimpleTaskListAdapter", e.toString());
            }
            return null;
        }

        @Override
        public long getItemId(int arg0)
        {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.adapter_item_gas_price, parent, false);

            try
            {
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_date)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_rate)).setText(item[1]);
                ((TextView) view.findViewById(R.id.tv_price)).setText(item[2]);
            }
            catch (Exception e)
            {
                Log.e("SimpleTaskListAdapter",e.toString());
                ((TextView) view.findViewById(R.id.tv_date)).setText("Error!");
            }

            return view;
        }
    }
}
