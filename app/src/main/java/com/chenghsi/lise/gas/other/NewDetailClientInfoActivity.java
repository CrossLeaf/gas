package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.DetailClientInfoAdapter;
import com.chenghsi.lise.gas.DetailClientInfoList;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MengHan on 2015/11/12.
 */
public class NewDetailClientInfoActivity extends Activity {

    String orderUrl = "http://198.245.55.221:8089/ProjectGAPP/php/db_join.php?tbname1=order&tbname2=customer&tbID1=customer_id&tbID2=customer_id&where=order.customer_id~";
    String doddleUrl = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=doddle&where=customer_id~";
    private ListView lv_detail_client;
    private DetailClientInfoAdapter adapter;
    private List<DetailClientInfoList> clientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail_info);
        Intent intent = getIntent();
        String customer_id = intent.getStringExtra("customerId");
        orderUrl += customer_id;
        doddleUrl+=customer_id;
        Log.e("client", orderUrl);
        new AsyncClientDownLoad().execute(orderUrl,doddleUrl);
        lv_detail_client = (ListView) findViewById(R.id.lv_detail_client);
    }

    class AsyncClientDownLoad extends AsyncTask<String, Integer, List<DetailClientInfoList>> {

        String order_day;
        String order_task;
        String order_cylinders_list;
        String doddle_degree;
        String doddle_time;

        @Override
        protected List<DetailClientInfoList> doInBackground(String... urls) {
            try {
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayDoddle = new JSONArray(getJSONData(urls[1]));
                JSONArray order;
                JSONArray doddle;

                for (int i = 0; i < jsonArrayCustomer.length(); i++) {
                    order = jsonArrayCustomer.getJSONArray(i);
                    order_day = order.getString(Constant.ORDER_DAY);
                    order_task = order.getString(Constant.ORDER_TASK);
                    order_cylinders_list = order.getString(Constant.ORDER_CYLINDERS_LIST);
                    DetailClientInfoList clientInfoList = new DetailClientInfoList(order_day, order_task, order_cylinders_list,"--");
                    clientList.add(clientInfoList);
                }
                for (int j = 0; j< jsonArrayDoddle.length(); j++){
                    doddle = jsonArrayDoddle.getJSONArray(j);
                    doddle_time = doddle.getString(Constant.DODDLE_TIME);
                    doddle_degree = doddle.getString(Constant.DODDLE_THIS_PHASE_DEGREE);
                    DetailClientInfoList clientInfoList = new DetailClientInfoList(doddle_time, "抄錶", "--,--,--,--", doddle_degree);
                    clientList.add(clientInfoList);
                }

                return clientList;
            } catch (Exception e) {
                Log.e("delivery", "資料抓取有誤");
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<DetailClientInfoList> arrayList) {
            super.onPostExecute(arrayList);
            Log.e("client", "Do onPostExecute");
            adapter = new DetailClientInfoAdapter(NewDetailClientInfoActivity.this, clientList);
            lv_detail_client.setAdapter(adapter);
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
    }
}
