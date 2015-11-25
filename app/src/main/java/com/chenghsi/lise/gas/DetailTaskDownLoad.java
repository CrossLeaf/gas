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

/**
 * Created by MengHan on 2015/10/29.
 */

public class DetailTaskDownLoad extends AsyncTask<String, Integer, String[]> {

    public static String[] data;
    private String order_get_time;
    private String order_should_money;
    public static String[] order_id;
    private String oldId;

    @Override
    protected String[] doInBackground(String... urls) {
        try {

            JSONArray jsonArrayOrder = new JSONArray(getData(urls[0]));
            String[] data = new String[jsonArrayOrder.length()-1];
            order_id = new String[jsonArrayOrder.length()-1];
            oldId = urls[1];
            int j =0;
            for (int i = 0; i < jsonArrayOrder.length(); i++) {
                JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                String order_id = order.getString(Constant.ORDER_ID);
                if (order_id.equals(oldId)){
                    continue;
                    //有一個數字i會跳過
                }
                order_get_time = order.getString(Constant.ORDER_GET_TIME);
                order_should_money = order.getString(Constant.ORDER_SHOULD_MONEY);
                data[j]="訂單日期："+order_get_time+"\n金額："+ order_should_money;
                this.order_id[j] = order_id;
                Log.e("task", "order_should_money:"+ order_should_money);
                Log.e("task", "data:::"+ data[j]);
                j++;
            }
            Log.e("task", "data:length::"+ data.length);
            return data;

        } catch (Exception e) {
            Log.e("task", "Detail Task 資料抓取有誤");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] data) {
        super.onPostExecute(data);
        this.data = data;

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
