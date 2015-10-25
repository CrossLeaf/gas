package com.chenghsi.lise.gas.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.LoginActivity;
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

    String userName;
    String[] tempAddress;
    boolean[] tempState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_task);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gasDB.setTaskListener(asyncTaskFinishListener);
//        simpleTaskListAdapter = new SimpleTaskListAdapter();
//        tempState = new boolean[simpleTaskListAdapter.getCount()];
//        simpleTaskListAdapter = new SimpleTaskListAdapter();
//        tempAddress = new String[simpleTaskListAdapter.getCount()];
//        tempAddress = new String[count];
//        Log.e("simple", "count:"+ count);
//        tempState = new boolean[count];
//        Log.e("simple",userName);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        gasDB.startAsyncTask("Task");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        int count = 1;
        boolean[] state = new boolean[getCount()];
        //回傳給TaskActivity 用的成員
        boolean[] returnState = new boolean[getCount()];
        String[] returnAdd = new String[getCount()];


        //----
        public SimpleTaskListAdapter(){

        }
        public SimpleTaskListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
//            Log.e("SimpleTaskListAdapter", "getCount:" + gasDB.getTable(GasDB.ORDER).length());
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

            ViewHolder viewHolder ;
            if (convertView == null) {
                count++;
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.adapter_item_task, parent, false);
                viewHolder.appointment = (TextView) convertView.findViewById(R.id.tv_appointment);
                viewHolder.kindOfTask = (TextView) convertView.findViewById(R.id.tv_kindOfTask);
                viewHolder.clientName = (TextView) convertView.findViewById(R.id.tv_clientName);
                viewHolder.address = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.contents = (TextView) convertView.findViewById(R.id.tv_contents);
                viewHolder.phones = (TextView) convertView.findViewById(R.id.tv_phones);
                viewHolder.btn_accept = (Button) convertView.findViewById(R.id.btn_accept);
                convertView.setTag(viewHolder);



            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Log.e("simple", "getView:" + count);

                viewHolder.btn_accept.setTag(position);
                viewHolder.btn_accept.setText("承接");
            if (state[position]) {
                Log.e("simple", "被點選");
                viewHolder.btn_accept.setTag(position);
                viewHolder.btn_accept.setText(userName);
            }

            final ViewHolder finalViewHolder1 = viewHolder;

            viewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((int) finalViewHolder1.btn_accept.getTag()==position){
                        if (!state[position]) {
                            String[] item = (String[]) getItem(position);
                            state[position] = true;
                            Log.e("simple", "點選狀態:" + position + "," + state[position]);
                            finalViewHolder1.btn_accept.setText(userName);

                            returnState[position] = state[position];
                            returnAdd[position] = item[3];
                            Log.e("simple", item[3]);
                        }else {
                            state[position] = false;
                            Log.e("simple", "點選狀態:" + position + "," + state[position]);
                            finalViewHolder1.btn_accept.setText("承接");
                            returnState[position] = state[position];
                        }
                    }
                }
            });

            try {
                String[] item = (String[]) getItem(position);
                appointment = item[0];
                kindOfTask = item[1];
                clientName = item[2];
                address = item[3];
                contents = item[4];
                phones = item[5];
                viewHolder.appointment.setText(item[0]);
                viewHolder.kindOfTask.setText(item[1]);
                viewHolder.clientName.setText(item[2]);
                viewHolder.address.setText(item[3]);
                viewHolder.contents.setText(item[4]);
                viewHolder.phones.setText(item[5]);
            } catch (Exception e) {

                Log.e("SimpleTaskListAdapter", e.toString());
//                viewHolder.appointment.setText("error...");
                Log.e("SimpleTaskListAdapter", "error");
            }
            return convertView;
        }

        public class ViewHolder {
            TextView appointment;
            TextView kindOfTask;
            TextView clientName;
            TextView address;
            TextView contents;
            TextView phones;
            Button btn_accept;
        }
        public boolean[] getState(){
            return returnState;
        }
        public String[] getAdd(){
            return  returnAdd;
        }
    }
    //for RoutePlanning
    public boolean[] getTempState() {
        SimpleTaskListAdapter simpleTaskListAdapter = new SimpleTaskListAdapter();
        tempState = simpleTaskListAdapter.getState();
        Log.e("simple", "tempState length:" + tempState.length);
        return tempState;
    }

    public String[] getTempAddress() {
        SimpleTaskListAdapter simpleTaskListAdapter = new SimpleTaskListAdapter();
        tempAddress = simpleTaskListAdapter.getAdd();
        return tempAddress;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actionByMenuItem(item);
        return true;
    }

    private void actionByMenuItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_gas_logout:
                Log.e("tag", "Logout");
                Toast.makeText(this, "登出", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_gas_quit:
                Toast.makeText(this, "Quit", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

    }

}
