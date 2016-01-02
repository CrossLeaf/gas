package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.ExTaskListAdapter;
import com.chenghsi.lise.gas.Globals;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.StaffList;
import com.chenghsi.lise.gas.TaskLists;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v4.widget.SwipeRefreshLayout.*;

/**
 * Created by MengHan on 2015/10/28.
 */
public class NewTaskActivity extends Activity {
    protected HttpClient httpclient;
    protected Toolbar toolbar;

    public ExpandableListView list_Task;   //下拉List控件
    public ExTaskListAdapter adapter;
    //新增了夥伴List
    public static ArrayList<StaffList> partnerList;
    private SwipeRefreshLayout swipeRefreshLayout;
    //    private SimpleSwipeRefreshLayout simpleSwipeRefreshLayout;
    ArrayList<ArrayList<TaskLists>> groupList;
    List<Map<String, String>> childList;

    public String user_name;
    public static String user_id;


    private static final String ORDER_FINISH = "2";
    private static final String DODDLE_FINISH = "2";
    Toast showToastMessage;
    SharedPreferences sp;

    //今日抄表與訂單 url
    String url0 = "http://198.245.55.221:8089/ProjectGAPP/php/show_order_dod.php";
    //customer join phone --->customer_id
    String url1 = "http://198.245.55.221:8089/ProjectGAPP/php/db_join.php?tbname1=customer&tbname2=phone&tbID1=customer_id&tbID2=customer_id";
    String url2 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=staff";
    String url3 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=carcyln&where=car_id~";
    String url4 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_task);
        Log.e("task", "----TaskOnCreate----");

        Globals globals = new Globals();
        String staff_id = globals.getUser_id();
        String staff_name = globals.getUser_name();

        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);
        user_name = sp.getString("staff_name", staff_name);
        user_id = sp.getString("staff_id", staff_id);

        Log.e("task", "preference test:" + user_id);
        Log.e("task", "preference test:" + user_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_task);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_Task = (ExpandableListView) findViewById(R.id.expListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }


    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setEnabled(false);
        new AsyncTaskDownLoad().execute(url0, url1, url2, url3, url4);
        showToastMessage = Toast.makeText(this, "Loading...", Toast.LENGTH_LONG);
        showToastMessage.show();
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
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
        showToastMessage.cancel();
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
        swipeRefreshLayout.setEnabled(false);
        Log.e("task", "----onPause----");
    }

    // Refreshing when pulling down
    //下拉更新
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View firstView = view.getChildAt(firstVisibleItem);
            // Enable refreshing event if at the top of listView
            if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
                swipeRefreshLayout.setEnabled(true);
            } else {
                swipeRefreshLayout.setEnabled(false);
            }
        }
    };

    // What to do if refreshing
    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.e("task", "call onRefreshListener...");
            swipeRefreshLayout.setRefreshing(true);
            new AsyncTaskDownLoad().execute(url0, url1, url2, url3, url4);
//            adapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    };

    public class AsyncTaskDownLoad extends AsyncTask<String, Integer, ArrayList<TaskLists>> {

        private ArrayList<TaskLists> taskListses;
        private ArrayList<TaskLists> listses;
        private ArrayList<StaffList> staffLists;

        private String order_id;
        private String order_day;
        private String order_task;
        private String order_phone;
        private String order_cylinders_list;
        private String order_customer_id;
        private String order_customer_name;
        private String order_address;
        private String customer_phone;
        private String order_should_money;
        private String order_status;
        private String order_accept;
        private String order_gas_residual;
        private String customer_settle_type;
        private String order_remark;

        private String doddle_id;
        private String doddle_time;
        private String doddle_address;
        private String doddle_customer_id;
        private String doddle_accept;
        private String doddle_status;
        private String doddle_remark;

        private String staff_id;
        private String staff_name;

        //        private String car_id ;
        private String carcyln_content;

        @Override
        protected ArrayList<TaskLists> doInBackground(String... urls) {
            try {

                String url_car = urls[3] + user_id;

                taskListses = new ArrayList<>();
                staffLists = new ArrayList<>();
                JSONArray jsonArrayTask = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayCustomerPhone = new JSONArray(getJSONData(urls[1]));
                JSONArray jsonArrayStaff = new JSONArray(getJSONData(urls[2]));
                JSONArray jsonArrayCarcyln = new JSONArray(getJSONData(url_car));
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[4]));

                JSONArray order_doddle;
                JSONArray customerAndPhone;
                JSONArray staff;
                JSONArray carcyln;
                JSONArray customer;

                TaskLists task_list;
                int index = 0;
                if (jsonArrayCarcyln.length() > 0) {
                    carcyln = jsonArrayCarcyln.getJSONArray(jsonArrayCarcyln.length() - 1);
                    carcyln_content = carcyln.getString(Constant.CARCYLN_CONTENT);
                    Log.e("newTask", "carcyln_content:" + carcyln_content);
                }else {
                    Message message = new Message();
                    message.what = 0;
                    myHandler.sendMessage(message);
                    throw new Exception("車上無鋼瓶");
                }
                for (int j = 0; j < jsonArrayStaff.length(); j++) {
                    staff = jsonArrayStaff.getJSONArray(j);
                    staff_id = staff.getString(0);
                    staff_name = staff.getString(3);
                    StaffList list = new StaffList(staff_id, staff_name);
                    staffLists.add(list);
                }

                for (int i = 0; i < jsonArrayTask.length(); i++) {
                    boolean hasCustomer = false;
                    Log.e("task", "index:" + index);
                    order_doddle = jsonArrayTask.getJSONArray(i);  //取得陣列中的每個陣列
                    //判斷是否為抄表
                    if (order_doddle.getInt(0) == -1) {
//                            Log.e("task", "抄表作業" + "i:" + i);
                        for (int j = i + 1; j < jsonArrayTask.length(); j++) {   //取出抄表的簡易任務
                            hasCustomer = false;
                            order_doddle = jsonArrayTask.getJSONArray(j);
                            if (order_doddle.getString(Constant.DODDLE_STATUS).equals(DODDLE_FINISH)) {
                                continue;
                            }
                            doddle_id = order_doddle.getString(Constant.DODDLE_ID);
                            doddle_time = order_doddle.getString(Constant.DODDLE_TIME);
                            doddle_address = order_doddle.getString(Constant.DODDLE_ADDRESS);
                            doddle_customer_id = order_doddle.getString(Constant.DODDLE_CUSTOMER_ID);
                            doddle_accept = order_doddle.getString(Constant.DODDLE_ACCEPT);
                            doddle_status = order_doddle.getString(Constant.DODDLE_STATUS);
                            doddle_remark = order_doddle.getString(Constant.DODDLE_REMARK);
//                                Log.e("task", "基本資料抓取完畢");

                            for (int k = 0; k < jsonArrayCustomerPhone.length(); k++) {
                                customerAndPhone = jsonArrayCustomerPhone.getJSONArray(k);
//                                    Log.e("task", "客戶ID：" + doddle_customer_id);
//                                    Log.e("task", "要比對的ID:" + customerAndPhone.getString(Constant.CUSTOMER_ID));
                                if ((doddle_customer_id.equals(customerAndPhone.getString(Constant.CUSTOMER_ID)))) {
                                    customer_phone = customerAndPhone.getString(24);
                                    order_customer_name = customerAndPhone.getString(Constant.CUSTOMER_NAME);
                                    hasCustomer = true;
//                                order_address = customerAndPhone.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                                    break;
                                }
                            }
                            /*如果在customer join phone 裡沒客戶資料 在這取得客戶資料*/
                            if (!hasCustomer) {
                                for (int l=0;l<jsonArrayCustomer.length();l++) {
                                    customer = jsonArrayCustomer.getJSONArray(l);
                                    if (doddle_customer_id.equals(customer.getString(Constant.CUSTOMER_ID))) {
                                        order_customer_name = customer.getString(Constant.CUSTOMER_NAME);
                                        customer_phone = "";
                                        break;
                                    }
                                }
                            }

                            task_list = new TaskLists(doddle_id, doddle_time, "抄錶", customer_phone,
                                    null, order_customer_name, doddle_address, null,
                                    null, doddle_status, doddle_accept, doddle_customer_id, null, doddle_remark, null);
                            taskListses.add(task_list);

                                /*做自接任務優先排列*/
                            if (taskListses.size() - 1 > 0 && doddle_accept.equals(user_id)) {
                                Collections.swap(taskListses, index, taskListses.size() - 1);
                                Log.e("task", "第:" + (taskListses.size() - 1));
                            }
                            if (doddle_accept.equals(user_id)) {
                                index++;
                            }
                        }
                        break;

                    } else {    //取出訂單 in 簡易任務
                        if (order_doddle.getString(Constant.ORDER_STATUS).equals(ORDER_FINISH)) {
                            continue;
                        }
                        order_id = order_doddle.getString(Constant.ORDER_ID);
                        order_day = order_doddle.getString(Constant.ORDER_DAY);
                        order_task = order_doddle.getString(Constant.ORDER_TASK);
                        order_phone = order_doddle.getString(Constant.ORDER_PHONE);
                        order_cylinders_list = order_doddle.getString(Constant.ORDER_CYLINDERS_LIST);
                        order_customer_id = order_doddle.getString(Constant.ORDER_CUSTOMER_ID);
                        order_should_money = order_doddle.getString(Constant.ORDER_SHOULD_MONEY);
                        order_status = order_doddle.getString(Constant.ORDER_STATUS);
                        order_accept = order_doddle.getString(Constant.ORDER_ACCEPT);
                        order_gas_residual = order_doddle.getString(Constant.ORDER_GAS_RESIDUAL);
                        order_remark = order_doddle.getString(Constant.ORDER_REMARK);
                        order_address = order_doddle.getString(Constant.ORDER_ADDRESS);
                        order_customer_name = order_doddle.getString(Constant.ORDER_CUSTOMER_NAME);
//                        Log.e("task", "order_customer_id:" + order_customer_id);

                        for (int j = 0; j < jsonArrayCustomerPhone.length(); j++) {
                            customerAndPhone = jsonArrayCustomerPhone.getJSONArray(j);
//                            Log.e("task", "customer_id:" + customerAndPhone.getString(0));
                            if (order_customer_id.equals(customerAndPhone.getString(0))) {
                                //儲存電話方法
//                                    storePhone(jsonArrayCustomerPhone, j);
                                if (order_customer_name.equals("") || order_address.equals("")) {
                                    order_customer_name = customerAndPhone.getString(Constant.CUSTOMER_NAME);
                                    order_address = customerAndPhone.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                                }
                                //TODO check一下 settle_type 的影響
                                customer_settle_type = customerAndPhone.getString(Constant.CUSTOMER_SETTLE_TYPE);
                                hasCustomer = true;
                                break;
                            }
                        }

                        if (!hasCustomer){
                            for (int l=0;l<jsonArrayCustomer.length();l++) {
                                customer = jsonArrayCustomer.getJSONArray(l);
                                if (order_customer_id.equals(customer.getString(Constant.CUSTOMER_ID))) {
                                    customer_settle_type = customer.getString(Constant.CUSTOMER_SETTLE_TYPE);
                                    break;
                                }
                            }
                        }

                        //判斷是否有此客戶id
                        if (order_customer_id.equals("") || order_customer_id == null) {
                            task_list = new TaskLists(order_id, order_day, order_task, order_phone,
                                    order_cylinders_list, null, null, null, order_should_money, order_status,
                                    order_accept, null, order_gas_residual, order_remark, carcyln_content);
                        } else {
                            task_list = new TaskLists(order_id, order_day, order_task, order_phone,
                                    order_cylinders_list, order_customer_name, order_address, customer_settle_type,
                                    order_should_money, order_status, order_accept, order_customer_id, order_gas_residual, order_remark, carcyln_content);

                                Log.e("task", "name:" + order_customer_name);
                                Log.e("task", "customerSettleType:"+customer_settle_type);
                        }

                        taskListses.add(task_list);

                        if (taskListses.size() - 1 > 0 && order_accept.equals(user_id)) {
                            Collections.swap(taskListses, index, taskListses.size() - 1);
                            Log.e("task", "第:" + (taskListses.size() - 1));
                        }
                        if (order_accept.equals(user_id)) {
                            index++;
                        }
                    }
                }
                return taskListses;
            } catch (Exception e) {
                Log.e("task", e.getMessage());
                Log.e("task", "NewTask資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TaskLists> taskListses) {
            super.onPostExecute(taskListses);
            if (taskListses != null) {
                listses = taskListses;
                getData(taskListses);
                Log.e("task", "總列表size：" + listses.size());
                adapter = new ExTaskListAdapter(NewTaskActivity.this, list_Task, groupList, childList, staffLists);
                list_Task.setAdapter(adapter);
                list_Task.setOnGroupClickListener(new OnItemClickListener());
                // Initializing listView
                list_Task.setOnScrollListener(onScrollListener);
//            adapter.notifyDataSetChanged();
                partnerList = staffLists;
            }
            swipeRefreshLayout.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        //取得JSON資料
        private String getJSONData(String url) {
            String retSrc = "";
            HttpGet httpget = new HttpGet(url);
            httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
//                    Log.e("retSrc", "完整資料：" + retSrc);
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
            childMap.put("finish", "結案");
            childMap.put("doddle", "抄錶");
            childList.add(childMap);
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
                    bundle.putString("orderId", taskLists.getOrder_doddle_id());
                    bundle.putString("appointment", taskLists.getOrder_prefer_time());
                    bundle.putString("kindOfTask", taskLists.getOrder_task());
                    bundle.putString("clientName", taskLists.getCustomer_name());
                    bundle.putString("address", add);
                    bundle.putString("contents", taskLists.getOrder_cylinders_list());
                    //TODO 傳入的phone可以修改
                    bundle.putString("phones", taskLists.getOrder_phone());
                    bundle.putString("customerId", taskLists.getCustomer_id());
                    bundle.putString("totalPay", taskLists.getOrder_should_money());
                    bundle.putString("gasResidual", taskLists.getOrder_gas_residual());
                    bundle.putString("settleType", taskLists.getCustomer_settle_type());
                    bundle.putString("orderStatus", taskLists.getOrder_doddle_status());
                    bundle.putString("orderRemark", taskLists.getOrder_remark());

                    Log.e("bundle", "customerSettleType:" + taskLists.getCustomer_settle_type());
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

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showToastMessage = Toast.makeText(NewTaskActivity.this, "此員工車上無鋼瓶", Toast.LENGTH_LONG);
                    showToastMessage.show();
                    break;
                default:
                    break;
            }
        }
    };
}
