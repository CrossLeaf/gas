package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MengHan on 2015/11/9.
 */
public class NewBalancingActivity extends Activity {
    protected Toolbar toolbar;
    ListView listResult;
    EditText edt_search;
    TextView tv_money;
    EditText edt_receive;
    Button btn_strike;
    Spinner spi_payMethod;

    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=order";

    private BalanceAdapter adapter;
    private List<BalanceList> balance_list = new ArrayList<>();
    public static HashMap<String, Boolean> isCheckedMap = new HashMap<>();
    private int total_money;
    private String[] money_pay;

    //回傳api
    private String order_id;
    private String staff_id;
    private String order_payment;
    private String income_money_real;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_balancing);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);

        new BalanceAsyncDownload().execute(url);

        init();

        btn_strike.setOnClickListener(strikeOnClickListener);

        listResult.setTextFilterEnabled(true);

        /*付費方式*/
        money_pay = new String[]{"現金", "支票", "匯款"};
        order_payment = money_pay[0];
        ArrayAdapter<String> payMethod_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, money_pay);
        payMethod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_payMethod.setAdapter(payMethod_adapter);
        spi_payMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                money_pay[i] = adapterView.getSelectedItem().toString();
                Log.e("DetailedTask", money_pay[i]);
                order_payment = money_pay[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("balancing", "--------終於看到沒選的--------");
            }
        });

        /*搜尋*/
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
                if (adapter != null)
                    NewBalancingActivity.this.adapter.getFilter().filter(arg0);
                Log.e("tag", "afterTextChanged");
            }
        });
    }

    /*OnCreate 結束*/
    private void init() {
        listResult = (ListView) findViewById(R.id.listResult);
        edt_search = (EditText) findViewById(R.id.edt_search);
        tv_money = (TextView) findViewById(R.id.tv_money);
        edt_receive = (EditText) findViewById(R.id.edt_receive);
        btn_strike = (Button) findViewById(R.id.btn_strike);
        spi_payMethod = (Spinner) findViewById(R.id.spi_payMethod);

        tv_money.setText("$0");
    }

    /*沖帳按鈕*/
    int count = 1;
    OnClickListener strikeOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            order_id = "";
            int flag = 0;
            for (Map.Entry entry : isCheckedMap.entrySet()) {
                if ((boolean) entry.getValue()) {
                    if (count == 1) {
                        order_id = (String) entry.getKey();
                        count++;
                    } else {
                        order_id += "_" + entry.getKey();
                    }
                    flag = 1;
                    Log.e("balancing", "order_id : " + entry.getKey() + " boolean : " + entry.getValue());
                }
            }
            if (flag == 0) {
                Toast.makeText(NewBalancingActivity.this, "請選擇沖帳項目", Toast.LENGTH_SHORT).show();
            } else if ("".equals(edt_receive.getText().toString().trim())) {
                Toast.makeText(NewBalancingActivity.this, "請輸入實收金額", Toast.LENGTH_SHORT).show();
            } else {

                income_money_real = edt_receive.getText().toString();
                staff_id = sp.getString("staff_id", null);

                Log.e("balancing", "order_id:" + order_id);
                Log.e("balancing", "income_money_real:" + income_money_real);
                Log.e("balancing", "order_payment:" + order_payment);
                Log.e("balancing", "staff_id:" + staff_id);

                new StrikeUpdate().start();
            }
        }
    };

    //沖帳 後台 update
    private class StrikeUpdate extends Thread {
        @Override
        public void run() {
            String url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_income.php?order_id=" + order_id +
                    "&income_money_real=" + income_money_real + "&order_payment=" + order_payment + "&staff_id=" + staff_id;
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            Log.e("retSrc", "讀取 JSON-1...");
            try {
                HttpResponse response = httpclient.execute(httpget);
                Log.e("retSrc", "讀取 JSON-2...");
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    String retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", retSrc);
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {
                Message message = new Message();
                message.what = 0;
                myHandler.sendMessage(message);
                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(NewBalancingActivity.this, "沖帳成功", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class BalanceAsyncDownload extends AsyncTask<String, Void, Void> {

        private String customer_name;
        private String order_day;
        private String order_cylinders_list;
        private String order_should_money;
        private String order_id;
        private String order_strike_balance;
        private String order_status;

        @Override
        protected Void doInBackground(String... urls) {
            try {

                JSONArray jsonArrayOrder = new JSONArray(getData(urls[0]));
                for (int i = 0; i < jsonArrayOrder.length(); i++) {
                    JSONArray order = jsonArrayOrder.getJSONArray(i);  //取得陣列中的每個陣列
                    order_strike_balance = order.getString(Constant.ORDER_STRIKE_BALANCE);
                    order_status = order.getString(Constant.ORDER_STATUS);
                    if (order_strike_balance.equals("1") || !order_status.equals("2")) {
                        Log.e("balance", "跳過:" + i);
                        continue;
                    }
                    order_id = order.getString(Constant.ORDER_ID);
                    order_day = order.getString(Constant.ORDER_DAY);
                    Log.e("balance", "day");
                    order_should_money = order.getString(Constant.ORDER_SHOULD_MONEY);
                    Log.e("balance", "money");
                    customer_name = order.getString(Constant.ORDER_CUSTOMER_NAME);
                    Log.e("balance", "name");
                    order_cylinders_list = order.getString(Constant.ORDER_CYLINDERS_LIST);
                    Log.e("balance", "cylinders");
                    BalanceList balanceList = new BalanceList(order_id, customer_name, order_cylinders_list, order_day, order_should_money);
                    balance_list.add(balanceList);
                    Log.e("balance", "order_should_money:" + order_should_money);
                    isCheckedMap.put(order_id, false);
                }
            } catch (Exception e) {
                Log.e("balance", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void data) {
            super.onPostExecute(data);

            adapter = new BalanceAdapter(NewBalancingActivity.this, balance_list);
            listResult.setAdapter(adapter);
            listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String ord_id = balance_list.get(position).getId();
                    order_should_money = balance_list.get(position).getMoney();

                    if (!isCheckedMap.get(ord_id)) {
                        isCheckedMap.put(ord_id, true);
                        total_money += Integer.valueOf(order_should_money);
                        tv_money.setText("$" + total_money);
                    } else {
                        isCheckedMap.put(ord_id, false);
                        total_money -= Integer.valueOf(order_should_money);
                        tv_money.setText("$" + total_money);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("NewBalance", "------NewBalance STOP------");
//        finish();
    }
}
