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
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/db_join.php?tbname1=customer&tbname2=phone&tbID1=customer_id&tbID2=customer_id";
    private EditText edt_client;
    private ListView lv_client_info;
    private ClientInfoAdapter adapter;
    private List<ClientInfoList> clientList = new ArrayList<>();
    private List<ClientInfoList> newClientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        new AsyncClientDownLoad().execute(url);
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
//        lv_client_info.setTextFilterEnabled(true);
        lv_client_info.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

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
//        lv_client_info.setFocusable(true);
//        lv_client_info.setFocusableInTouchMode(true);
        lv_client_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("client", "position:" + position);
                newClientList = new ArrayList<>(ClientInfoAdapter.clientInfoLists);
                String customer_id = newClientList.get(position).getId();
                String customer_name = newClientList.get(position).getName();
                Toast.makeText(NewClientInfoActivity.this, "id:" + customer_id, Toast.LENGTH_SHORT).show();
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
        String[] customer_phone = new String[5];
        ArrayList<String > phone_arrayList;
        String old_id;
        int count = 1;

        @Override
        protected List<ClientInfoList> doInBackground(String... urls) {
            try {
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[0]));
                JSONArray customer;
                JSONArray oldCustomer;
                Log.e("client", "array執行成功");

                HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
                int count_clientList = 1;
                for (int i =0; i<jsonArrayCustomer.length(); i++){
                    customer = jsonArrayCustomer.getJSONArray(i);
                    customer_id = customer.getString(Constant.CUSTOMER_ID);
                    String phone = customer.getString(22);
                    if (hashMap.isEmpty() || !hashMap.containsKey(customer_id)){
                        phone_arrayList = new ArrayList<>();
                        phone_arrayList.add("請選擇號碼");
                        phone_arrayList.add(phone);
                        hashMap.put(customer_id, phone_arrayList);
                        Log.e("client", "Initial phone:" + phone);
                    }else {
                        phone_arrayList.add(phone);
                        hashMap.put(customer_id, phone_arrayList);
                        Log.e("client", "Phone:"+phone);
                    }
                    /*假如前一筆ID不等於現在ID，則儲存前一筆ID*/
                    if (i != 0 && !customer_id.equals((jsonArrayCustomer.getJSONArray(i-1)).getString(Constant.CUSTOMER_ID))) {
                        oldCustomer = jsonArrayCustomer.getJSONArray(i-1);
                        //此兩筆為前一筆資料
                        customer_id = oldCustomer.getString(Constant.CUSTOMER_ID);
                        customer_name = oldCustomer.getString(Constant.CUSTOMER_NAME);
                        customer_address = oldCustomer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);

                        ClientInfoList clientInfoList = new ClientInfoList(customer_id, customer_name, customer_address, hashMap.get(customer_id));
                        clientList.add(clientInfoList);
                        Log.e("client", "客戶List儲存筆數:" + count_clientList);
                        count_clientList++;
//                      customer_phone = new String[5];
                    }
                }

                 /*可以將最後一筆拿出FOR迴圈儲存*/
                customer = jsonArrayCustomer.getJSONArray(jsonArrayCustomer.length()-1);
                customer_id = customer.getString(Constant.CUSTOMER_ID);
                customer_name = customer.getString(Constant.CUSTOMER_NAME);
                customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                ClientInfoList clientInfoList = new ClientInfoList(customer_id, customer_name, customer_address, hashMap.get(customer_id));
                clientList.add(clientInfoList);

                /*爛寫法XD*/
                /*for (int i = 0; i < jsonArrayCustomer.length(); i++) {
                    //下一筆資料
                    customer = jsonArrayCustomer.getJSONArray(i+1);
                    //現在資料
                    oldCustomer = jsonArrayCustomer.getJSONArray(i);
                    Log.e("client", "i:" + i);

                    old_id = oldCustomer.getString(Constant.CUSTOMER_ID);
                    Log.e("client", "old_id:" + old_id);
                    customer_id = customer.getString(Constant.CUSTOMER_ID);
                    Log.e("client", "new_id:" + customer_id);

                    //假如現在id等於下一筆id 為了將電話存在陣列上 繼續迴圈
                    if (old_id.equals(customer_id)) {
                        phone = oldCustomer.getString(22);
                        Log.e("client", "if count:" + count);
                        Log.e("client", "id:" + customer_id + "phone:" + phone);
                        customer_phone[count] = phone;
                        count++;
                        if (i+1 == jsonArrayCustomer.length()){
                            phone = customer.getString(22);
                            customer_phone[count] = phone;
                            customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                            customer_name = customer.getString(Constant.CUSTOMER_NAME);
                            store();
                        }else {
                            continue;
                        }
                        //假如現在id不等於下一筆id 儲存此筆資料
                    } else {
                        if (i + 1 == jsonArrayCustomer.length()) {
                            phone = customer.getString(22);
                            customer_phone[count] = phone;
                            customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                            customer_name = customer.getString(Constant.CUSTOMER_NAME);
                        } else {
                            phone = oldCustomer.getString(22);
                            Log.e("client", "else count:" + count);
                            Log.e("client", "id:" + old_id + "phone:" + phone);
                            customer_phone[count] = phone;
                            customer_name = oldCustomer.getString(Constant.CUSTOMER_NAME);
                            Log.e("client", "name:" + customer_name);
                            customer_address = oldCustomer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                            Log.e("client", "address:" + customer_address);
                        }
                    }
                    store();
                    Log.e("client", "--forLoop end--");
                }*/



                /*舊方法*/
                /*for (int i = 1; i < jsonArrayCustomer.length(); i++) {
                    customer_phone[0] = "請選擇號碼";
                    //下一筆資料
                    customer = jsonArrayCustomer.getJSONArray(i);
                    //現在資料
                    oldCustomer = jsonArrayCustomer.getJSONArray(i - 1);
                    Log.e("client", "i:" + i);

                    //假如整筆資料是最後一筆 直接存
                    if (i == jsonArrayCustomer.length() + 1) {
//                        old_id = oldCustomer.getString(Constant.CUSTOMER_ID);
//                        Log.e("client", "old_id:" + old_id);
                        customer_id = customer.getString(Constant.CUSTOMER_ID);
                        Log.e("client", "customer_id:" + customer_id);
                        phone = customer.getString(22);
//                        Log.e("client", "phone:" + phone);
                        customer_phone[count] = phone;
                        customer_address = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                        customer_name = customer.getString(Constant.CUSTOMER_NAME);
                    } else {    //如果不是整筆資料的最後一筆
                        old_id = oldCustomer.getString(Constant.CUSTOMER_ID);
                        Log.e("client", "old_id:" + old_id);
                        customer_id = customer.getString(Constant.CUSTOMER_ID);
                        Log.e("client", "new_id:" + customer_id);

                        //假如現在id等於下一筆id 為了將電話存在陣列上 繼續迴圈
                        if (old_id.equals(customer_id)) {
                            phone = oldCustomer.getString(22);
                            Log.e("client", "if count:" + count);
                            Log.e("client", "id:" + customer_id + "phone:" + phone);
                            customer_phone[count] = phone;
                            count++;
                            continue;
                            //假如現在id不等於下一筆id 儲存此筆資料
                        } else {
                            phone = oldCustomer.getString(22);
                            Log.e("client", "else count:" + count);
                            Log.e("client", "id:" + old_id + "phone:" + phone);
                            customer_phone[count] = phone;
                            customer_name = oldCustomer.getString(Constant.CUSTOMER_NAME);
                            Log.e("client", "name:" + customer_name);
                            customer_address = oldCustomer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                            Log.e("client", "address:" + customer_address);
                        }
                    }
                    count = 1;
                    int a = 0;
                    int j = 0;
                    for (String str : customer_phone) {
                        Log.e("client", "a：" + a);
                        if (customer_phone[a] == null) {
                            customer_phone[a] = "";
                            Log.e("client", "customer_phone[" + a + "]=null");
                            break;
                        } else {
                            j++;
                        }
                        a++;
                        Log.e("client", "phone：" + str);
                    }
                    String phone[] = new String[j];

                    for (int k = 0; k < j; k++) {
                        phone[k] = customer_phone[k];
                    }

                    ClientInfoList clientInfoList = new ClientInfoList(customer_id, customer_name, customer_address, phone);
                    clientList.add(clientInfoList);
                    customer_phone = new String[5];
                    Log.e("client", "--forLoop end--");
                }*/

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

