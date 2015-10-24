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
import com.chenghsi.lise.gas.db.GasDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class TaskActivity extends AbstractList {
    private String appointment;
    private String kindOfTask;
    private String clientName;
    private String address;
    private String contents;
    private String phones;

    String[] tempAddress;
    boolean[] state;
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_task);
        gasDB.setTaskListener(asyncTaskFinishListener);
//        simpleTaskListAdapter = new SimpleTaskListAdapter();
//        state = new boolean[simpleTaskListAdapter.getCount()];
//        simpleTaskListAdapter = new SimpleTaskListAdapter();
//        tempAddress = new String[simpleTaskListAdapter.getCount()];
//        context = this;
//        tempAddress = new String[count];
//        Log.e("simple", "count:"+ count);
//        state = new boolean[count];
    }

    @Override
    protected void onResume() {
        super.onResume();
        gasDB.startAsyncTask("Task");
    }


    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener() {
        @Override
        public void onAsyncTaskFinish() {
            try {
                final SimpleTaskListAdapter apt = new SimpleTaskListAdapter(TaskActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.setClass(TaskActivity.this, DetailedTaskActivity.class);
                        bundle.putString("appointment", appointment);
                        bundle.putString("kindOfTask", kindOfTask);
                        bundle.putString("clientName", clientName);
                        bundle.putString("address", address);
                        bundle.putString("contents", contents);
                        bundle.putString("phones", phones);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };




    public class SimpleTaskListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public SimpleTaskListAdapter(){}
        public SimpleTaskListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }
        boolean[] state = new boolean[getCount()];

        @Override
        public int getCount() {
            Log.e("SimpleTaskListAdapter","getCount:"+ gasDB.getTable(GasDB.ORDER).length());
//            count = gasDB.getTable(GasDB.ORDER).length();

            return gasDB.getTable(GasDB.ORDER).length();
        }

        @Override
        public Object getItem(int position) {
            try {
                JSONArray item_task = gasDB.getTableItemByIndex(GasDB.ORDER, position);
                String customer_id = item_task.getString(Constant.ORDER_CUSTOMER_ID);
                JSONArray item_customer = gasDB.getTableItemById(GasDB.CUSTOMER, customer_id);

                // The adapted data
                ArrayList<String> result = new ArrayList<>();
                result.add(item_task.getString(Constant.ORDER_PREFER_TIME));
                result.add(item_task.getString(Constant.ORDER_TASK));
                result.add(item_customer.getString(Constant.CUSTOMER_NAME));
                result.add(item_customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS));
                result.add(item_customer.getString(Constant.CUSTOMER_ID));
                result.add(item_task.getString(Constant.ORDER_PHONE));

                return result.toArray(new String[result.size()]);

            } catch (JSONException e) {
                Log.e("SimpleTaskListAdapter", e.toString());
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_item_task, parent, false);
            }

            try {
                String[] item = (String[]) getItem(position);
                appointment = item[0];
                kindOfTask = item[1];
                clientName = item[2];
                address = item[3];
                contents = item[4];
                phones = item[5];
                ((TextView) convertView.findViewById(R.id.tv_appointment)).setText(item[0]);
                ((TextView) convertView.findViewById(R.id.tv_kindOfTask)).setText(item[1]);
                ((TextView) convertView.findViewById(R.id.tv_clientName)).setText(item[2]);
                ((TextView) convertView.findViewById(R.id.tv_address)).setText(item[3]);
                ((TextView) convertView.findViewById(R.id.tv_contents)).setText(item[4]);
                ((TextView) convertView.findViewById(R.id.tv_phones)).setText(item[5]);
            } catch (Exception e) {
                Log.e("SimpleTaskListAdapter", e.toString());
                Log.e("SimpleTaskListAdapter", "error");
            }


            final Button btn_accept = (Button) convertView.findViewById(R.id.btn_accept);


            //TODO 需要取得員工資料，才能改變承接按鈕

            btn_accept.setOnClickListener(new Button.OnClickListener() {
                boolean isMe = true;

                @Override
                public void onClick(View view) {
                    //TODO 之後要改成判斷為該員工id & isMe要改
                    if (btn_accept.getText().equals("承接") && !state[position]) {
                        Toast.makeText(view.getContext(), "任務承接成功", Toast.LENGTH_SHORT).show();
                        btn_accept.setText("李司機");
                        state[position] = true;
                        Log.e("simple", "position:"+position+"\nstate:"+state[position]);
//                        tempAddress[position] = address;
//                        Log.e("SimpleTaskListAdapter", address);
                    } else if (isMe) {
                        Toast.makeText(view.getContext(), "取消承接", Toast.LENGTH_SHORT).show();
                        btn_accept.setText("承接");
                        state[position] = false;
//                        tempAddress[position] = null;
//                        Log.e("SimpleTaskListAdapter", address);
                    } else {
                        Toast.makeText(view.getContext(), "已有人接案", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return convertView;
        }


        /*public class ViewHolder {
            TextView appointment;
            TextView kindOfTask;
            TextView clientName;
            TextView address;
            TextView contents;
            TextView phones;
        }*/
    }


    SimpleTaskListAdapter simpleTaskListAdapter;
    public boolean[] getState() {


        Log.e("simple", "state length:"+state.length);
        return state;
    }
    public String[] getTempAddress() {

        return tempAddress;
    }
}