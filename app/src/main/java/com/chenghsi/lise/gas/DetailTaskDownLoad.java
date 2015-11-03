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

public class DetailTaskDownLoad extends AsyncTask<String, Integer, String[]> {

    public static String[] data;
    private String order_get_time;
    private String order_money_credit;

    @Override
    protected String[] doInBackground(String... urls) {
        try {

            JSONArray jsonArrayOrder = new JSONArray(getData(urls[0]));
            String[] data = new String[jsonArrayOrder.length()];
            for (int i = 0; i < jsonArrayOrder.length(); i++) {
                JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                order_get_time = order.getString(Constant.ORDER_GET_TIME);
                order_money_credit = order.getString(Constant.ORDER_MONEY_CREDIT);
                data[i]="訂單日期："+order_get_time+"\n金額："+order_money_credit;

                Log.e("task", "order_money_credit:"+ order_money_credit);
                Log.e("task", "data:::"+ data[i]);
            }

            return data;

        } catch (Exception e) {
            Log.e("task", "資料抓取有誤");
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
