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


public class ClientInfoActivity extends AbstractList
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_client_info);
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
        gasDB.startAsyncTask("ClientInfo");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                SimpleTaskListAdapter apt = new SimpleTaskListAdapter(ClientInfoActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        intent.setClass(ClientInfoActivity.this, DetailedClientInfoActivity.class);
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

    public class SimpleTaskListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public SimpleTaskListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return gasDB.getTable(GasDB.CUSTOMER).length();
        }

        @Override
        public Object getItem(int position)
        {
            try
            {
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
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.adapter_item_client_info, parent, false);

            try
            {
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_name)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_address)).setText(item[1]);
                //((TextView) view.findViewById(R.id.tv_clientName)).setText(item[2]);
            }
            catch (Exception e)
            {
                Log.e("SimpleTaskListAdapter",e.toString());
                ((TextView) view.findViewById(R.id.tv_appointment)).setText("Error!");
            }

            return view;
        }
    }
}
