package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class DoddleActivity extends Activity {

    String customer_id;
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=doddle&where=customer_id~";
    ListView listView;
    ArrayList<HashMap<String,String>> itemList ;
    SimpleAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doddle);
        Intent intent = this.getIntent();
        customer_id = intent.getStringExtra("customerId");
        url+=customer_id;
        Log.e("doddle", url);
        new AsyncDoddleDownload().execute(url);
        listView = (ListView) findViewById(R.id.listView_doddle);
        itemList = new ArrayList<>();
    }

    public class AsyncDoddleDownload extends AsyncTask<String, Integer, Integer>{

        String doddle_time;
        String doddle_this_phase_degree;
        @Override
        protected Integer doInBackground(String[] urls) {
            try {
                JSONArray jsonArrayDoddle = new JSONArray(getJSONData(urls[0]));
                JSONArray doddleArray;
                //目前是當作有抄表任務時，也會在doddle新增一筆當日的抄表所以要扣掉當日抄表項目
                for (int i=0; i<jsonArrayDoddle.length()-1; i++){
                    doddleArray = jsonArrayDoddle.getJSONArray(i);
                    doddle_time = doddleArray.getString(Constant.DODDLE_TIME);
                    doddle_this_phase_degree = doddleArray.getString(Constant.DODDLE_THIS_PHASE_DEGREE);
                    HashMap<String,String> doddleMap = new HashMap<>();
                    doddleMap.put("time", doddle_time);
                    doddleMap.put("degree", doddle_this_phase_degree);
                    Log.e("doddle", doddleMap.get("time"));
                    itemList.add(doddleMap);
                }
            }catch (Exception e){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            adapter = new SimpleAdapter(DoddleActivity.this,itemList ,R.layout.adapter_item_doddle_history,
                    new String[]{"time", "degree"},new int[]{R.id.tv_doddle_time, R.id.tv_doddle_degree} );
            listView.setAdapter(adapter);
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
