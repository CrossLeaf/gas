package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.ExTaskListAdapter;
import com.chenghsi.lise.gas.LoginActivity;
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
    private ArrayList<TaskLists> list;
    ArrayList<ArrayList<TaskLists>> groupList;
    List<Map<String, String>> childList;

    String url1 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=order";
    String url2 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer&where=customer_id~";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Log.e("task", "----TaskOnCreate----");
        new AsyncTaskDownLoad().execute(url1, url2);
        list_Task = (ExpandableListView) findViewById(R.id.expListView);

//        list_Task.setAdapter(new ExTaskListAdapter(NewTaskActivity.this, list_Task, groupList, childList));
//        list_Task.setOnGroupExpandListener(new OnListItemExpandListener(list_Task));
    }

    public class AsyncTaskDownLoad extends AsyncTask<String, Integer, ArrayList<TaskLists>> {

        private ArrayList<TaskLists> taskListses;
        private ArrayList<TaskLists> listses;

        private String order_prefer_time;
        private String order_task;
        private String order_phone;
        private String order_cylinders_list;
        private String order_customer_id;
        private String customer_name;
        private String customer_address;

        private String order_status;
        private String order_accept;
        //可能還要新增承接者姓名

        @Override
        protected ArrayList<TaskLists> doInBackground(String... urls) {
            try {
                taskListses = new ArrayList<>();
                String customerURL = urls[1];

                JSONArray jsonArrayOrder = new JSONArray(getJSONData(urls[0]));

                for (int i = 0; i < jsonArrayOrder.length(); i++) {
                    JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                    order_prefer_time = order.getString(Constant.ORDER_PREFER_TIME);
                    order_task = order.getString(Constant.ORDER_TASK);
                    order_phone = order.getString(Constant.ORDER_PHONE);
                    order_cylinders_list = order.getString(Constant.ORDER_CYLINDERS_LIST);
                    order_customer_id = order.getString(Constant.ORDER_CUSTOMER_ID);
                    order_status = order.getString(Constant.ORDER_STATUS);
                    order_accept = order.getString(Constant.ORDER_ACCEPT);
                    Log.e("task", "order_customer_id:" + order_customer_id);
                    Log.e("task", urls[1] + order_customer_id);
                    //如果直接用urls[1]+字串會出錯
                    JSONArray jsonArrayCustomer = new JSONArray(getJSONData(customerURL + order_customer_id));

                    //判斷是否有此客戶id
                    if (jsonArrayCustomer.length() == 0) {
                        TaskLists list = new TaskLists(order_prefer_time, order_task, order_phone, order_cylinders_list, null, null, order_status, order_accept);
                        taskListses.add(list);
                    } else {
                        JSONArray customer = jsonArrayCustomer.getJSONArray(0);
                        customer_name = customer.getString(Constant.CUSTOMER_NAME);
                        customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);


                        TaskLists list = new TaskLists(order_prefer_time, order_task, order_phone, order_cylinders_list, customer_name, customer_address, order_status, order_accept);
                        taskListses.add(list);
                        Log.e("task", "name:" + customer_name);
                    }
                }
                return taskListses;

            } catch (Exception e) {
                Log.e("task", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TaskLists> taskListses) {
            super.onPostExecute(taskListses);
            listses = taskListses;
            getData();
            ExTaskListAdapter adapter = new ExTaskListAdapter();
            list_Task.setAdapter(new ExTaskListAdapter(NewTaskActivity.this, list_Task, groupList, childList));
            list_Task.setOnGroupExpandListener(new OnListItemExpandListener());
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
            Log.e("retSrc", "讀取 JSON-1...");
            try {
                HttpResponse response = httpclient.execute(httpget);
                Log.e("retSrc", "讀取 JSON-2...");
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "讀取 JSON-3...");
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

        public void getData() {
            groupList = new ArrayList<>();
            groupList.add(listses);
            Log.e("task", "listses size;" + listses.size());
            childList = new ArrayList<Map<String, String>>();
            Map<String, String> childMap = new HashMap<>();
            childMap.put("scanIn", "掃入");
            childMap.put("scanOut", "掃出");
            childMap.put("setting", "設定");
            childMap.put("finish", "結案");
            childList.add(childMap);
            Log.e("task", "getData 做完");
        }
    }


    private class OnListItemExpandListener implements ExpandableListView.OnGroupExpandListener {
        private String userName = LoginActivity.usn;

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public void onGroupExpand(int groupPosition) {
            //點開listView 的權限
            ArrayList<TaskLists> list = groupList.get(0);
            final TaskLists taskLists = list.get(groupPosition);

            if (!(taskLists.getOrder_status().equals("true")) || !taskLists.getOrder_accept().equals(userName)) {
                list_Task.collapseGroup(groupPosition);
            } else {

            }
        }
    }
}
