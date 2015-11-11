package com.chenghsi.lise.gas.delivery;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

public class DeliveryScheduleDetailActivity extends Activity {

    Spinner spinner_month;
    Spinner spinner_day;
    Spinner spinner_clientName;
    EditText edt_add;
    TextView tv_year;
    NumberPicker numPick_50;
    NumberPicker numPick_20;
    NumberPicker numPick_16;
    NumberPicker numPick_4;
    NumberPicker numPickHr;
    NumberPicker numPickMin;

    int year, month, day;

    private String[] customer_name;
    private String[] customer_address;
    private String[] cylinder_list;
    private String[] month_list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String[] day_list;
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule_detail);
        new AsyncTaskDownLoad().execute(url);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        numPick_50.setMaxValue(99);
        numPick_50.setMinValue(0);
        numPick_20.setMaxValue(99);
        numPick_20.setMinValue(0);
        numPick_16.setMaxValue(99);
        numPick_16.setMinValue(0);
        numPick_4.setMaxValue(99);
        numPick_4.setMinValue(0);
        numPickHr.setMaxValue(23);
        numPickHr.setMinValue(0);
        numPickMin.setMaxValue(59);
        numPickMin.setMinValue(0);

        tv_year.setText(year + "年");
        numPick_50.setOnValueChangedListener(new NumChangeListener());
        numPick_20.setOnValueChangedListener(new NumChangeListener());
        numPick_16.setOnValueChangedListener(new NumChangeListener());
        numPick_4.setOnValueChangedListener(new NumChangeListener());
        spinner_clientName.setOnItemSelectedListener(nameSelectListener);

        ArrayAdapter monAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_size, month_list);
//        monAdapter.setDropDownViewResource(R.layout.spinner_text_size);
        spinner_month.setAdapter(monAdapter);
        spinner_month.setOnItemSelectedListener(monSelectListener);
        ArrayAdapter dayAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_size, day_list);
//        monAdapter.setDropDownViewResource(R.layout.spinner_text_size);
        spinner_day.setAdapter(dayAdapter);
        spinner_day.setOnItemSelectedListener(daySelectListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        spinner_month = (Spinner) findViewById(R.id.spinner_month);
        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        spinner_clientName = (Spinner) findViewById(R.id.spinner_clientName);
        edt_add = (EditText) findViewById(R.id.edt_add);
        numPick_50 = (NumberPicker) findViewById(R.id.numPick_50);
        numPick_20 = (NumberPicker) findViewById(R.id.numPick_20);
        numPick_16 = (NumberPicker) findViewById(R.id.numPick_16);
        numPick_4 = (NumberPicker) findViewById(R.id.numPick_4);
        numPickHr = (NumberPicker) findViewById(R.id.numPickHr);
        numPickMin = (NumberPicker) findViewById(R.id.numPickMin);
        tv_year = (TextView) findViewById(R.id.year);
        edt_add.clearFocus();

        cylinder_list = new String[4];

        Bundle bundle = this.getIntent().getExtras();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        day = bundle.getInt("day");

        day_list = new String[31];
        for (int i = 1; i < 32; i++) {
            day_list[i - 1] = String.valueOf(i);
        }

    }

    //月份 監聽事件
    AdapterView.OnItemSelectedListener monSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinner_month.setSelection(month - 1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    //日期 監聽事件
    AdapterView.OnItemSelectedListener daySelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinner_day.setSelection(day - 1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    //客戶名稱 監聽事件
    private AdapterView.OnItemSelectedListener nameSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String address = _toAddress(customer_address[position]);
            edt_add.setText(address);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    //取消與確認 button
    public void btn_schedule(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_ok:
                finish();
                break;
        }
    }

    public class NumChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            switch (numberPicker.getId()) {
                case R.id.numPick_50:
                    cylinder_list[0] = String.valueOf(newValue);
                    break;
                case R.id.numPick_20:
                    cylinder_list[1] = String.valueOf(newValue);
                    break;
                case R.id.numPick_16:
                    cylinder_list[2] = String.valueOf(newValue);
                    break;
                case R.id.numPick_4:
                    cylinder_list[3] = String.valueOf(newValue);
                    break;
            }
        }
    }

    public class AsyncTaskDownLoad extends AsyncTask<String, Integer, String[]> {


        @Override
        protected String[] doInBackground(String... urls) {
            try {

                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[0]));
                customer_name = new String[jsonArrayCustomer.length()];
                customer_address = new String[jsonArrayCustomer.length()];
                JSONArray customer;

                for (int j = 0; j < jsonArrayCustomer.length(); j++) {
                    customer = jsonArrayCustomer.getJSONArray(j);
                    customer_name[j] = customer.getString(Constant.CUSTOMER_NAME);
                    customer_address[j] = customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS);
                    Log.e("delivery", "cus_name" + customer_name[j]);
                }

                return customer_name;
            } catch (Exception e) {
                Log.e("delivery", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] cus_name) {
            super.onPostExecute(cus_name);
            ArrayAdapter apt = new ArrayAdapter<>(DeliveryScheduleDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, cus_name);
            apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_clientName.setAdapter(apt);
            edt_add.setText(customer_address[0]);
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

    public String _toAddress(String address) {
        try {
            String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
            String temp = "";
            String[] address_arr = address.split("_");
            Log.e("add", "addLength : " + address_arr.length);
            for (int i = 0; i < address_arr.length; i++) {
                if (!address_arr[i].equals("") && address_arr[i] != null) {
                    temp += address_arr[i] + addr_name[i];
                }
            }
            return temp;
        } catch (Exception e) {
            return null;
        }

    }

}