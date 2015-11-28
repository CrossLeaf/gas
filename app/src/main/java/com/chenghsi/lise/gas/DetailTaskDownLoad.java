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

public class DetailTaskDownLoad extends AsyncTask<String, Integer, ArrayList<String>> {

    public static String[] data;
    private String order_get_time;
    private String order_should_money;
    public static String[] order_id;
    private ArrayList<String> orderId;
    private String oldId;

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        try {
            JSONArray jsonArrayOrder = new JSONArray(getData(urls[0]));
            ArrayList<String> data = new ArrayList<>();
            orderId = new ArrayList<>();
            oldId = urls[1];
            int j =0;
            for (int i = 0; i < jsonArrayOrder.length(); i++) {
                JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                String order_id = order.getString(Constant.ORDER_ID);
                String order_strike_balance = order.getString(Constant.ORDER_STRIKE_BALANCE);
                if (order_id.equals(oldId) ||order_strike_balance.equals("1")){
                    continue;
                }
                order_get_time = order.getString(Constant.ORDER_GET_TIME);
                order_should_money = order.getString(Constant.ORDER_SHOULD_MONEY);
                data.add("訂單日期："+order_get_time+"\n金額："+ order_should_money);
                orderId.add(order_id);
                Log.e("task", "order_should_money:"+ order_should_money);
                Log.e("task", "data:::"+ data.get(j));
                j++;
            }
            Log.e("task", "data:length::"+ data.size());
            return data;
        } catch (Exception e) {
            Log.e("task", "Detail Task 資料抓取有誤");
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> listData) {
        super.onPostExecute(listData);
        order_id = new String[orderId.size()];
        order_id = orderId.toArray(order_id);
        data = new String[listData.size()];
        data = listData.toArray(data);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    //取得JSON資料
    private String getData(String url) {
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
}
