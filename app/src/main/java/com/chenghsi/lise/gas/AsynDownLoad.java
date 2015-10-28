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
import java.util.HashMap;

/**
 * Created by MengHan on 2015/10/26.
 */
public class AsynDownLoad extends AsyncTask<String, ArrayList<GasPriceList>, ArrayList<GasPriceList>> {

    private ArrayList<GasPriceList> gasPriceLists;
    private String price_20;
    private String price_16;
    private String price_bulid_date;
    private String price_remark;

    @Override
    protected ArrayList<GasPriceList> doInBackground(String... urls) {
        try {
            gasPriceLists = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(getJSONData(urls[0]));
//            HashMap<String,JSONArray> map = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray content = jsonArray.getJSONArray(i);  //取得陣列中的每個陣列
                price_20 = content.getString(2);
                price_16 = content.getString(3);
                price_bulid_date = content.getString(5);
                price_remark = content.getString(8);
                GasPriceList info = new GasPriceList(price_20, price_16, price_bulid_date, price_remark);
                gasPriceLists.add(info);
                Log.e("async", price_bulid_date);
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(ArrayList<GasPriceList>... values) {
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

    public ArrayList<GasPriceList> getList() {
        return gasPriceLists;
    }

}
