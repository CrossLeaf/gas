package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.chenghsi.lise.gas.BalanceAdapter;
import com.chenghsi.lise.gas.BalanceList;
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
import java.util.List;

/**
 * Created by MengHan on 2015/11/9.
 */
public class NewBalancingActivity extends Activity {

    ListView listResult;
    List<BalanceList> balance_list = new ArrayList<BalanceList>();
    private BalanceAdapter adapter;
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=order";
    EditText edt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_balance);
        listResult = (ListView) findViewById(R.id.listResult);
        edt_search = (EditText) findViewById(R.id.edt_search);
        new BalanceAsyncDownload().execute(url);

        listResult.setTextFilterEnabled(true);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.e("tag", "onTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.e("tag", "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                NewBalancingActivity.this.adapter.getFilter().filter(arg0);
                Log.e("tag", "afterTextChanged");
            }
        });
    }

    public class BalanceAsyncDownload extends AsyncTask<String, Integer, String[]> {

        private String customer_name;
        private String order_day;
        private String order_cylinders_list;
        private String order_should_money;

        @Override
        protected String[] doInBackground(String... urls) {
            try {

                JSONArray jsonArrayOrder = new JSONArray(getData(urls[0]));
                String[] data = new String[jsonArrayOrder.length()];
                for (int i = 0; i < jsonArrayOrder.length(); i++) {
                    JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                    order_day = order.getString(Constant.ORDER_DAY);
                    Log.e("balance", "day");
                    order_should_money = order.getString(Constant.ORDER_SHOULD_MONEY);
                    Log.e("balance", "money");
                    customer_name = order.getString(Constant.ORDER_CUSTOMER_NAME);
                    Log.e("balance", "name");
                    order_cylinders_list = order.getString(Constant.ORDER_CYLINDERS_LIST);
                    Log.e("balance", "cylinders");
                    BalanceList balanceList = new BalanceList(customer_name, order_cylinders_list, order_day, order_should_money);
                    balance_list.add(balanceList);
                    Log.e("balance", "order_should_money:" + order_should_money);
                }

                return data;

            } catch (Exception e) {
                Log.e("balance", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] data) {
            super.onPostExecute(data);
            adapter = new BalanceAdapter(NewBalancingActivity.this, balance_list);
            listResult.setAdapter(adapter);

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
}
