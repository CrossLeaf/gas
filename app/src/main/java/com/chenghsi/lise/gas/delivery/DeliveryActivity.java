package com.chenghsi.lise.gas.delivery;

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
import com.chenghsi.lise.gas.task.DetailedTaskActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class DeliveryActivity extends AbstractList
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_delivery);
        gasDB.setTaskListener(asyncTaskFinishListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gasDB.startAsyncTask("Delivery");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                DeliveryListAdapter apt = new DeliveryListAdapter(DeliveryActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        intent.setClass(DeliveryActivity.this, DeliveryScheduleActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public class DeliveryListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;


        public DeliveryListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return gasDB.getTable(GasDB.DELIVERY).length();
        }

        @Override
        public Object getItem(int position)
        {
            try
            {
                JSONArray item_delivery = gasDB.getTableItemByIndex(GasDB.DELIVERY, position);

                String order_id = item_delivery.getString(Constant.DELIVERY_ORDER_ID);
                Log.e("tag","orderId:"+order_id);
                JSONArray item_order = gasDB.getTableItemById(GasDB.ORDER, order_id);

                String customer_id = item_order.getString(Constant.ORDER_CUSTOMER_ID);
                Log.e("tag","customerId:"+customer_id);
                JSONArray item_customer = gasDB.getTableItemById(GasDB.CUSTOMER, customer_id);

//                String phone_number =;
//                JSONArray phone_number = gasDB.getTableItemById(GasDB.PHONE,phone_number);

                ArrayList<String> result = new ArrayList<>();
                result.add( item_customer.getString(Constant.CUSTOMER_NAME) );
                result.add( item_customer.getString(Constant.CUSTOMER_EMAIL) ); //TODO phone
                result.add( item_customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS) );

                return result.toArray(new String[result.size()]);

            }
            catch(JSONException e) {Log.e("DeliveryListAdapter",e.toString());}
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.adapter_item_delivery, parent, false);

            try
            {
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_clientName)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_phones)).setText(item[1]);
                ((TextView) view.findViewById(R.id.tv_address)).setText(item[2]);
                Log.e("tag", item[0]+","+item[1]+","+item[2]);
            }
            catch (Exception e)
            {
                Log.e("SimpleTaskListAdapter",e.toString());
                ((TextView) view.findViewById(R.id.tv_clientName)).setText("Error!");
            }

            return view;
        }
    }
}
