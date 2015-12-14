package com.chenghsi.lise.gas.delivery;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    protected Toolbar toolbar;
    Spinner spinner_month;
    Spinner spinner_day;
    Spinner spinner_clientName;
    EditText edt_add;
    EditText edt_remark;
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
    private String[] customer_id;
    private String[] cylinder_list;
    private String[] month_list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String[] day_list;
    private String hr = "0";
    private String min = "0";
    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=customer";

    private String cus_id;
    private String cly_name;
    private String cly_time;
    private String cly_50;
    private String cly_20;
    private String cly_16;
    private String cly_4;
    private String ms1_addr;
    private String cly_PS;
    private String cly_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule_detail);
        new AsyncTaskDownLoad().execute(url);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*標題列toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_delivery_schedule_detail);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        numPickHr.setOnValueChangedListener(new NumChangeListener());
        numPickMin.setOnValueChangedListener(new NumChangeListener());

        spinner_clientName.setOnItemSelectedListener(nameSelectListener);

        ArrayAdapter monAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_size, month_list);
//        monAdapter.setDropDownViewResource(R.layout.spinner_text_size);
        spinner_month.setAdapter(monAdapter);
        spinner_month.setSelection(month - 1);
        spinner_month.setOnItemSelectedListener(monSelectListener);
        ArrayAdapter dayAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_size, day_list);
        spinner_day.setAdapter(dayAdapter);
        spinner_day.setSelection(day - 1);
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
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        numPick_50 = (NumberPicker) findViewById(R.id.numPick_50);
        numPick_20 = (NumberPicker) findViewById(R.id.numPick_20);
        numPick_16 = (NumberPicker) findViewById(R.id.numPick_16);
        numPick_4 = (NumberPicker) findViewById(R.id.numPick_4);
        numPickHr = (NumberPicker) findViewById(R.id.numPickHr);
        numPickMin = (NumberPicker) findViewById(R.id.numPickMin);
        tv_year = (TextView) findViewById(R.id.year);

        edt_add.clearFocus();
        edt_remark.clearFocus();


        Bundle bundle = this.getIntent().getExtras();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        day = bundle.getInt("day");

        cly_day = year + "-" + month + "-" + day;

        cylinder_list = new String[]{"0", "0", "0", "0"};
        day_list = new String[31];
        for (int i = 1; i < 32; i++) {
            day_list[i - 1] = String.valueOf(i);
        }

    }

    //月份 監聽事件
    AdapterView.OnItemSelectedListener monSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            month = Integer.parseInt(parent.getSelectedItem().toString());
            cly_day = year + "-" + month + "-" + day;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.e("month:", "未作選取");
        }
    };

    //日期 監聽事件
    AdapterView.OnItemSelectedListener daySelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            day = Integer.parseInt(parent.getSelectedItem().toString());
            cly_day = year + "-" + month + "-" + day;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.e("day:", "未作選取");
        }
    };

    //客戶名稱 監聽事件
    private AdapterView.OnItemSelectedListener nameSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            cly_name = parent.getSelectedItem().toString();
            cus_id = customer_id[position];
            String address = _toAddress(customer_address[position]);
            edt_add.setText(address);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public void btn_schedule(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_ok:
                ms1_addr = edt_add.getText().toString();
                cly_PS = edt_remark.getText().toString();
                cly_50 = cylinder_list[0];
                cly_20 = cylinder_list[1];
                cly_16 = cylinder_list[2];
                cly_4 = cylinder_list[3];
                cly_time = hr + ":" + min;
                Log.e("btn", cus_id);
                Log.e("btn", cly_name);
                Log.e("btn", cly_time);
                Log.e("btn", ms1_addr);
                Log.e("btn", cly_day);
                Log.e("btn", cly_50);
                Log.e("btn", cly_20);
                Log.e("btn", cly_16);
                Log.e("btn", cly_4);
                Log.e("btn", cly_PS);
                new DeliveryUpdate().start();
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    //確定按鈕 post資料
    private class DeliveryUpdate extends Thread {

        @Override
        public void run() {
            String url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_cly.php?cly_name=" + cus_id +
                    "&cly_time=" + cly_time + "&cly_50=" + cly_50 + "&cly_20=" + cly_20 + "&cly_16=" + cly_16 +
                    "&cly_4=" + cly_4 + "&ms1_addr=" + ms1_addr + "&cly_PS=" + cly_PS + "&cly_day=" + cly_day;
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            Log.e("retSrc", "讀取 JSON-1...");
            try {
                HttpResponse response = httpclient.execute(httpget);
                Log.e("retSrc", "讀取 JSON-2...");
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    String retSrc = EntityUtils.toString(resEntity);
                    Toast.makeText(DeliveryScheduleDetailActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    Log.e("retSrc", retSrc);
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {

                httpclient.getConnectionManager().shutdown();
            }
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
                case R.id.numPickHr:
                    hr = String.valueOf(newValue);
                    break;
                case R.id.numPickMin:
                    min = String.valueOf(newValue);
                    break;
            }
        }
    }

    public class AsyncTaskDownLoad extends AsyncTask<String, Integer, String[]> {


        @Override
        protected String[] doInBackground(String... urls) {
            try {
                JSONArray jsonArrayCustomer = new JSONArray(getJSONData(urls[0]));
                customer_id = new String[jsonArrayCustomer.length()];
                customer_name = new String[jsonArrayCustomer.length()];
                customer_address = new String[jsonArrayCustomer.length()];
                JSONArray customer;

                for (int j = 0; j < jsonArrayCustomer.length(); j++) {
                    customer = jsonArrayCustomer.getJSONArray(j);
                    customer_id[j] = customer.getString(Constant.CUSTOMER_ID);
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

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
