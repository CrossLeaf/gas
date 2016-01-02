package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.HashMap;
import java.util.List;


/**
 * Created by MengHan on 2015/11/12.
 */
public class NewClientInfoActivity extends Activity {
    protected Toolbar toolbar;
    String url0 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=phone";
    String url1 = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer";
    //TODO 新增一客戶url
    private EditText edt_client;
    private ListView lv_client_info;
    private ClientInfoAdapter adapter;
    private List<ClientInfoList> clientList = new ArrayList<>();
    private List<ClientInfoList> newClientList;
    Toast showToastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        new AsyncClientDownLoad().execute(url0, url1);
        setContentView(R.layout.activity_client_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_client_info);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edt_client = (EditText) findViewById(R.id.edt_search_client);
        lv_client_info = (ListView) findViewById(R.id.lv_client_info);
        lv_client_info.setTextFilterEnabled(true);
        lv_client_info.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        showToastMessage = Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT);
        showToastMessage.show();
        Log.e("client", "lv_client_info 初始化");
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
                if (adapter != null) {
                    NewClientInfoActivity.this.adapter.getFilter().filter(arg0);
                    Log.e("tag", "afterTextChanged");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv_client_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("client", "position:" + position);
                newClientList = new ArrayList<>(ClientInfoAdapter.clientInfoLists);
                String customer_id = newClientList.get(position).getId();
                String customer_name = newClientList.get(position).getName();
                Intent intent = new Intent();
                intent.putExtra("customerId", customer_id);
                intent.putExtra("customerName", customer_name);
                intent.setClass(NewClientInfoActivity.this, NewDetailClientInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private class AsyncClientDownLoad extends AsyncTask<String, Integer, List<ClientInfoList>> {

        String customer_id;
        String customer_name;
        String customer_address;
        String phone;
        ArrayList<String> phone_arrayList;

        @Override
        protected List<ClientInfoList> doInBackground(String... urls) {
            try {
                JSONArray jsonArrayPhone = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[1]));
                JSONArray all_phone_jsonArray;
                JSONArray all_cus_jsonArray;

                Log.e("client", "array執行成功");

                HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

                /*phone 儲存*/
                for (int i=0; i<jsonArrayPhone.length(); i++){
                    all_phone_jsonArray = jsonArrayPhone.getJSONArray(i);
                    phone = all_phone_jsonArray.getString(Constant.PHONE_NUMBER);
                    customer_id = all_phone_jsonArray.getString(Constant.PHONE_CUSTOMER_ID);
                    Log.e("newClient", "customer_id:"+customer_id);
                    if (hashMap.get(customer_id) == null || !hashMap.containsKey(customer_id)) {
                        phone_arrayList = new ArrayList<>();
                        phone_arrayList.add("請選擇號碼");
                        phone_arrayList.add(phone);
                        hashMap.put(customer_id, phone_arrayList);
                        Log.e("client", "Initial phone:" + phone);
                    } else {
                        phone_arrayList = hashMap.get(customer_id);
                        phone_arrayList.add(phone);
                        hashMap.put(customer_id, phone_arrayList);
                        Log.e("client", "Phone:" + phone);
                    }
                }

                /*沒有電話的客戶*/
                for (int j = 0; j < jsonArrayCustomer.length(); j++) {
                    all_cus_jsonArray = jsonArrayCustomer.getJSONArray(j);
                    String cus_id = all_cus_jsonArray.getString(Constant.CUSTOMER_ID);
                    customer_name = all_cus_jsonArray.getString(Constant.CUSTOMER_NAME);
                    customer_address = all_cus_jsonArray.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                    if (hashMap.get(cus_id) == null){
                        Log.e("newClient", "cus_id:"+cus_id);
                        Log.e("newClient", "customer_name:" + customer_name);
                        ClientInfoList clientObj = new ClientInfoList(cus_id, customer_name, customer_address,null);
                        clientList.add(clientObj);
                    }else {
                        ClientInfoList clientObj = new ClientInfoList(cus_id, customer_name, customer_address,hashMap.get(cus_id));
                        clientList.add(clientObj);
                    }
                }
                return clientList;
            } catch (Exception e) {
                Log.e("client", "資料抓取有誤");
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

    @Override
    protected void onStop() {
        super.onStop();
    }

}

