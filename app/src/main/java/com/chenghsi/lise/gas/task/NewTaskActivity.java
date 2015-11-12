package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.ExTaskListAdapter;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.TaskLists;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/10/28.
 */
public class NewTaskActivity extends Activity {

    public ExpandableListView list_Task;   //下拉List控件
    public ExTaskListAdapter adapter;
    ArrayList<ArrayList<TaskLists>> groupList;
    List<Map<String, String>> childList;

    String url2 = "http://198.245.55.221:8089/ProjectGAPP/php/db_join.php?tbname1=customer&tbname2=phone&tbID1=customer_id&tbID2=customer_id";
    //今日抄表與訂單 url
    String url3 = "http://198.245.55.221:8089/ProjectGAPP/php/show_order_dod.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Log.e("task", "----TaskOnCreate----");
        new AsyncTaskDownLoad().execute(url3, url2);
        list_Task = (ExpandableListView) findViewById(R.id.expListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("task", "----onResume----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("task", "----onStop----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("task", "----onPause----");
    }

    public class AsyncTaskDownLoad extends AsyncTask<String, Integer, ArrayList<TaskLists>> {

        private ArrayList<TaskLists> taskListses;
        private ArrayList<TaskLists> listses;

        private String order_day;
        private String order_task;
        private String order_phone;
        private String order_cylinders_list;
        private String order_customer_id;
        private String customer_name;
        private String customer_address;
        private String customer_phone;
        private String order_should_money;
        private String order_status;
        private String order_accept;

        private String doddle_time;
        private String doddle_address;
        private String doddle_customer_id;
        private String doddle_accept;

        @Override
        protected ArrayList<TaskLists> doInBackground(String... urls) {
            try {

                taskListses = new ArrayList<>();
                JSONArray jsonArrayTask = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[1]));
                JSONArray order_doddle;
                JSONArray customer;

                for (int i = 0; i < jsonArrayTask.length(); i++) {
                    order_doddle = jsonArrayTask.getJSONArray(i);  //取得陣列中的每個陣列
                    //判斷是否為抄表
                    if (order_doddle.getInt(0) == -1) {
                        Log.e("task", "抄表作業" + "i:" + i);

                        for (int j = i + 1; j < jsonArrayTask.length(); j++) {   //取出抄表的簡易任務
                            order_doddle = jsonArrayTask.getJSONArray(j);
                            Log.e("task", "j:" + j);

                            doddle_time = order_doddle.getString(Constant.DODDLE_TIME);
                            doddle_address = order_doddle.getString(Constant.DODDLE_ADDRESS);
                            doddle_customer_id = order_doddle.getString(Constant.DODDLE_CUSTOMER_ID);
                            doddle_accept = order_doddle.getString(Constant.DODDLE_ACCEPT);
                            Log.e("task", "基本資料抓取完畢");

                            for (int k = 0; k < jsonArrayCustomer.length(); k++) {
                                customer = jsonArrayCustomer.getJSONArray(k);
                                Log.e("task", "客戶ID：" + doddle_customer_id);
                                Log.e("task", "要比對的ID:" + customer.getString(Constant.CUSTOMER_ID));
                                if (!(doddle_customer_id.equals(customer.getString(Constant.CUSTOMER_ID)))) {
                                    Log.e("task", "ID不同");
                                    continue;
                                }

                                customer_phone = customer.getString(22);
                                customer_name = customer.getString(Constant.CUSTOMER_NAME);
                                customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                                TaskLists list = new TaskLists(doddle_time, "抄錶", customer_phone,
                                        null, customer_name, doddle_address,
                                        null, "", doddle_accept, doddle_customer_id);
                                taskListses.add(list);
                                break;
                            }
                        }
                        break;

                    } else {    //取出訂單 in 簡易任務
                        order_day = order_doddle.getString(Constant.ORDER_DAY);
                        order_task = order_doddle.getString(Constant.ORDER_TASK);
                        order_phone = order_doddle.getString(Constant.ORDER_PHONE);
                        order_cylinders_list = order_doddle.getString(Constant.ORDER_CYLINDERS_LIST);
                        order_customer_id = order_doddle.getString(Constant.ORDER_CUSTOMER_ID);
                        order_should_money = order_doddle.getString(Constant.ORDER_SHOULD_MONEY);
                        order_status = order_doddle.getString(Constant.ORDER_STATUS);
                        order_accept = order_doddle.getString(Constant.ORDER_ACCEPT);

                        Log.e("task", "order_customer_id:" + order_customer_id);
                        for (int j = 0; j < jsonArrayCustomer.length(); j++) {
                            customer = jsonArrayCustomer.getJSONArray(j);
                            if (order_customer_id.equals(customer.getString(0))) {
                                customer_name = customer.getString(Constant.CUSTOMER_NAME);
                                customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                                break;
                            }

                        }
                        //判斷是否有此客戶id
                        if (order_customer_id.equals(null)) {
                            TaskLists list = new TaskLists(order_day, order_task, order_phone,
                                    order_cylinders_list, null, null, order_should_money, order_status,
                                    order_accept, null);
                            taskListses.add(list);
                        } else {
                            TaskLists list = new TaskLists(order_day, order_task, order_phone,
                                    order_cylinders_list, customer_name, customer_address,
                                    order_should_money, order_status, order_accept, order_customer_id);
                            taskListses.add(list);

                            Log.e("task", "name:" + customer_name);
                        }

                    }
                }

                return taskListses;
            } catch (Exception e) {
                Log.e("task", "資料抓取有誤");
            }

            return taskListses;
        }

        @Override
        protected void onPostExecute(ArrayList<TaskLists> taskListses) {
            super.onPostExecute(taskListses);
            listses = taskListses;
            getData(taskListses);
            Log.e("task", "總列表size：" + listses.size());
            adapter = new ExTaskListAdapter(NewTaskActivity.this, list_Task, groupList, childList);
            list_Task.setAdapter(adapter);
            list_Task.setOnGroupClickListener(new OnItemClickListener());
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);
        }

        //取得JSON資料
        private String getJSONData(String url) {
            String retSrc = "";
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                }

            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
                return null;
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
            return retSrc;

        }

        public void getData(ArrayList<TaskLists> listses) {
            groupList = new ArrayList<>();
            groupList.add(listses);
            Log.e("task", "listses size;" + listses.size());
            childList = new ArrayList<>();
            Map<String, String> childMap = new HashMap<>();
            childMap.put("scanIn", "掃入");
            childMap.put("scanOut", "掃出");
//            childMap.put("setting", "設定");
            childMap.put("finish", "結案");
            childList.add(childMap);
            Log.e("task", "getData 做完");
        }
    }

    class OnItemClickListener implements ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
            if (expandableListView.isGroupExpanded(groupPosition)) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                ArrayList<TaskLists> list = groupList.get(0);
                TaskLists taskLists = list.get(groupPosition);
                if (taskLists.getOrder_task().equals("抄錶")) {
                    intent.setClass(NewTaskActivity.this, DoddleActivity.class);
                    intent.putExtra("customerId", taskLists.getCustomer_id());
                    startActivity(intent);
                } else {
                    //轉換字串
                    String add = _toAddress(taskLists.getCustomer_address());

                    //傳值到細項Intent
                    intent.setClass(NewTaskActivity.this, DetailedTaskActivity.class);
                    bundle.putString("appointment", taskLists.getOrder_prefer_time());
                    bundle.putString("kindOfTask", taskLists.getOrder_task());
                    bundle.putString("clientName", taskLists.getCustomer_name());
                    bundle.putString("address", add);
                    bundle.putString("contents", taskLists.getOrder_cylinders_list());
                    bundle.putString("phones", taskLists.getOrder_phone());
                    bundle.putString("customerId", taskLists.getCustomer_id());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
            return true;
        }

        public String _toAddress(String address) {
            try {
                String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
                String temp = "";
                String[] address_arr = address.split("_");
                Log.e("notifyData", "addLength : " + address_arr.length);
                for (int i = 0; i < address_arr.length; i++) {
                    if (!address_arr[i].equals("") && address_arr[i] != null) {
                        temp += address_arr[i] + addr_name[i];
                    }
                }
                return temp;
            } catch (Exception e) {
                return null;
            }

        }
    }
}
