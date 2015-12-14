package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.chenghsi.lise.gas.GasPriceAdapter;
import com.chenghsi.lise.gas.GasPriceList;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by MengHan on 2015/11/11.
 */
public class NewGasPriceActivity extends Activity {

    protected Toolbar toolbar;
    private GasPriceAdapter adapter;
    private ListView listView;
    private ArrayList<GasPriceList> gasPriceLists = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_gas_price);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        new AsyncGasPriceDownLoad().execute();
        Log.e("tag", "--onCreate--");

        listView = (ListView) findViewById(R.id.lv_price);

    }

    //DownLoad
    public class AsyncGasPriceDownLoad extends AsyncTask<String, Integer, ArrayList<GasPriceList>> {

        private String price_20;
        private String price_16;
        private String price_build_date;
        private String price_remark;
        private String price_rate;

        double oldPrice = 0.0;
        int textColor;

        private String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=price";

        @Override
        protected ArrayList<GasPriceList> doInBackground(String... urlssss) {
            try {

                JSONArray jsonArray = new JSONArray(getJSONData(url));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray content = jsonArray.getJSONArray(i);  //取得陣列中的每個陣列
                    price_20 = content.getString(2);
                    price_16 = content.getString(3);
                    price_build_date = content.getString(5);
                    price_remark = content.getString(8);
                    price_rate = countRate(Double.parseDouble(price_remark));
                    GasPriceList info = new GasPriceList(price_20, price_16, price_build_date, price_rate, textColor);
                    gasPriceLists.add(info);
                    Log.e("async", price_build_date);
                    Log.e("async", price_remark);
                }
            } catch (Exception e) {
                Log.e("async", "資料錯誤");
            }
            return gasPriceLists;
        }


        public String countRate(double newPrice){
            String strRate ;
            double rate;
            Log.e("gasprice", "newPrice:"+newPrice);
            //rate = 差價
                if (newPrice == 0.0) {
                    strRate = "---";
                    setColor(Color.GRAY);
                } else {
                    if (oldPrice > newPrice) {
                        rate = oldPrice - newPrice;
                        Log.e("GasPrice", "跌:" + newPrice);
                        strRate = "跌" + rate;
                        setColor(Color.GREEN);
                        oldPrice = newPrice;
                    } else {
                        rate = newPrice - oldPrice;
                        Log.e("GasPrice", "漲:" + newPrice);
                        strRate = "漲" + rate;
                        setColor(Color.RED);
                        oldPrice = newPrice;
                    }
                }
            return strRate;
        }

        public int setColor(int color){
            return textColor = color;
        }

        @Override
        protected void onPostExecute(ArrayList<GasPriceList> gasPriceLists) {
            super.onPostExecute(gasPriceLists);
            adapter = new GasPriceAdapter(NewGasPriceActivity.this, gasPriceLists);
            listView.setAdapter(adapter);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
