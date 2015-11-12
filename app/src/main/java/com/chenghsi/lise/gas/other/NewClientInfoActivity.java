package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chenghsi.lise.gas.ClientInfoAdapter;
import com.chenghsi.lise.gas.ClientInfoList;
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
 * Created by MengHan on 2015/11/12.
 */
public class NewClientInfoActivity extends Activity {
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer";
    private EditText edt_client;
    private ListView lv_client_info;
    private ClientInfoAdapter adapter;
    private List<ClientInfoList> clientList = new ArrayList<>();
    private List<ClientInfoList> newClientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AsyncClientDownLoad().execute(url);
        setContentView(R.layout.activity_client_info);
        edt_client = (EditText) findViewById(R.id.edt_search_client);
        lv_client_info = (ListView) findViewById(R.id.lv_client_info);
        lv_client_info.setTextFilterEnabled(true);
        lv_client_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("client", "position:" + position);
                newClientList = new ArrayList<>(ClientInfoAdapter.clientInfoLists);
                String customer_id = newClientList.get(position).getId();
//                Toast.makeText(NewClientInfoActivity.this, "id:" + customer_id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("customerId", customer_id);
                intent.setClass(NewClientInfoActivity.this,NewDetailClientInfoActivity.class);
                startActivity(intent);
            }
        });
        edt_client.addTextChangedListener(new TextWatcher() {
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
                NewClientInfoActivity.this.adapter.getFilter().filter(arg0);
                Log.e("tag", "afterTextChanged");
            }
        });

    }

    private class AsyncClientDownLoad extends AsyncTask<String, Integer, List<ClientInfoList>> {

        String customer_id;
        String customer_name;
        String customer_address;

        @Override
        protected List<ClientInfoList> doInBackground(String... urls) {
            try {
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[0]));
                JSONArray customer;

                for (int i = 0; i < jsonArrayCustomer.length(); i++) {
                    customer = jsonArrayCustomer.getJSONArray(i);
                    customer_id = customer.getString(Constant.CUSTOMER_ID);
                    customer_name = customer.getString(Constant.CUSTOMER_NAME);
                    customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                    ClientInfoList clientInfoList = new ClientInfoList(customer_id, customer_name, customer_address);
                    clientList.add(clientInfoList);
                }

                return clientList;
            } catch (Exception e) {
                Log.e("delivery", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ClientInfoList> clientList) {
            super.onPostExecute(clientList);
            Log.e("client", "Do onPostExecute");
            adapter = new ClientInfoAdapter(NewClientInfoActivity.this, clientList);
            lv_client_info.setAdapter(adapter);
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

