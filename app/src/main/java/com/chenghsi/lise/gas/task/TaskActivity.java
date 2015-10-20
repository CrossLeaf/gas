package com.chenghsi.lise.gas.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.TestData;
import com.chenghsi.lise.gas.db.GasDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class TaskActivity extends AbstractList
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_task);
        gasDB.setTaskListener(asyncTaskFinishListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gasDB.startAsyncTask("Task");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                SimpleTaskListAdapter apt = new SimpleTaskListAdapter(TaskActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        intent.setClass(TaskActivity.this, DetailedTaskActivity.class);
                        intent.putExtra("position",position);
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
            return gasDB.getTable(GasDB.ORDER).length();
        }

        @Override
        public Object getItem(int position)
        {
            try
            {
                JSONArray item_task = gasDB.getTableItemByIndex(GasDB.ORDER, position);
                String customer_id = item_task.getString(Constant.ORDER_CUSTOMER_ID);
                JSONArray item_customer = gasDB.getTableItemById(GasDB.CUSTOMER, customer_id);

                // The adapted data
                ArrayList<String> result = new ArrayList<>();
                result.add( item_task.getString(Constant.ORDER_PREFER_TIME) );
                result.add( item_task.getString(Constant.ORDER_TASK) );
                result.add( item_customer.getString(Constant.CUSTOMER_NAME) );
                result.add( item_customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS) );
                result.add( item_customer.getString(Constant.CUSTOMER_ID) );
                result.add( item_task.getString(Constant.ORDER_PHONE) );

                return result.toArray(new String[result.size()]);
            }
            catch(JSONException e) {Log.e("SimpleTaskListAdapter",e.toString());}
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.adapter_item_task, parent, false);

            try
            {
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_appointment)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_kindOfTask)).setText(item[1]);
                ((TextView) view.findViewById(R.id.tv_clientName)).setText(item[2]);
                ((TextView) view.findViewById(R.id.tv_address)).setText(item[3]);
                ((TextView) view.findViewById(R.id.tv_contents)).setText(item[4]);
                ((TextView) view.findViewById(R.id.tv_phones)).setText(item[5]);
            }
            catch (Exception e)
            {
                Log.e("SimpleTaskListAdapter",e.toString());
                ((TextView) view.findViewById(R.id.tv_appointment)).setText("Error!");
            }



            final Button btn_accept = (Button) view.findViewById(R.id.btn_accept);


            //TODO
            /*if(TestData.state[position]) btn_accept.setText("李司機");

            btn_accept.setOnClickListener(new Button.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {


                    if (!TestData.state[position])
                    {
                        Toast.makeText(view.getContext(), "任務承接成功", Toast.LENGTH_SHORT).show();
                        btn_accept.setText("李司機");
                        TestData.state[position] = true;
                    }
                    else
                    {
                        Toast.makeText(view.getContext(), "取消承接", Toast.LENGTH_SHORT).show();
                        btn_accept.setText("承接");
                        TestData.state[position] = false;
                    }
                }
            });*/


            return view;
        }
    }
}
