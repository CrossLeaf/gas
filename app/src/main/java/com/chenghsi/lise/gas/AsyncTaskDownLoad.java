package com.chenghsi.lise.gas;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by MengHan on 2015/10/29.
 */

/*
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
    public static boolean status;
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

                Log.e("task", "order_customer_id:"+order_customer_id);
                Log.e("task", urls[1]+order_customer_id);
                //如果直接用urls[1]+字串會出錯
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(customerURL+order_customer_id));

                //判斷是否有此客戶id
                if (jsonArrayCustomer.length()==0 ){
                    TaskLists list = new TaskLists(order_prefer_time, order_task, order_phone, order_cylinders_list, null, null);
                    taskListses.add(list);
                }else {
                    JSONArray customer = jsonArrayCustomer.getJSONArray(0);
                    customer_name = customer.getString(Constant.CUSTOMER_NAME);
                    customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);

                    TaskLists list = new TaskLists(order_prefer_time, order_task, order_phone, order_cylinders_list, customer_name, customer_address);
                    taskListses.add(list);
                    Log.e("task", "name:" + customer_name);
                }
                status = true;
                return taskListses;
            }
        } catch (Exception e) {
            Log.e("task", "資料抓取有誤");
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<TaskLists> taskListses) {
        super.onPostExecute(taskListses);
        listses = taskListses;


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
    public ArrayList<TaskLists> getList() {
        return listses;
    }

}*/
