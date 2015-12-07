package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.PaymentList;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by MengHan on 2015/11/13.
 */
public class FactoryActivity extends Activity {

    NumberPicker numPick_fac50;
    NumberPicker numPick_fac20;
    NumberPicker numPick_fac16;
    NumberPicker numPick_fac4;

    NumberPicker numPick_car50;
    NumberPicker numPick_car20;
    NumberPicker numPick_car16;
    NumberPicker numPick_car4;

    TextView tv_fac50;
    TextView tv_fac20;
    TextView tv_fac16;
    TextView tv_fac4;

    TextView tv_car50;
    TextView tv_car20;
    TextView tv_car16;
    TextView tv_car4;


    private String url_fac = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=facylN";
    private String url_car = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=carcylN";
    private String url_payment = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=payment&where=payment_status~0&where=payment_type~%E5%AD%98%E5%85%A5%E6%B0%A3";
    private String[] url = new String[2];

    private int[] fac_list;
    private int[] car_list;
    private String[] fac_car_id = new String[2];
    private String[] fac_car_content = new String[2];
    private String[] fac_content;   //最後要把這回傳回去
    private String[] car_content;
    private String suppliers_id;
    private String car_id;
    public static ArrayList<PaymentList> paymentList;

    //付款 field
    private String total_money;
    private String paymentId;

    int for_loop;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        //有exception可以讓畫面定格
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

                Log.e("Alert", "Lets See if it Works !!!");

            }
        });*/
        init();
        Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show();

        new AsyncFactoryDownload().execute(url_fac, url_car);
        new PaymentFactoryDownload().execute(url_payment);

        numPick_fac50.setOnValueChangedListener(new NumChangeListener());
        numPick_fac20.setOnValueChangedListener(new NumChangeListener());
        numPick_fac16.setOnValueChangedListener(new NumChangeListener());
        numPick_fac4.setOnValueChangedListener(new NumChangeListener());

        numPick_car50.setOnValueChangedListener(new NumChangeListener());
        numPick_car20.setOnValueChangedListener(new NumChangeListener());
        numPick_car16.setOnValueChangedListener(new NumChangeListener());
        numPick_car4.setOnValueChangedListener(new NumChangeListener());

    }

    private void init() {
        tv_fac4 = (TextView) findViewById(R.id.tv_fac4);
        tv_fac16 = (TextView) findViewById(R.id.tv_fac16);
        tv_fac20 = (TextView) findViewById(R.id.tv_fac20);
        tv_fac50 = (TextView) findViewById(R.id.tv_fac50);

        tv_car4 = (TextView) findViewById(R.id.tv_car4);
        tv_car16 = (TextView) findViewById(R.id.tv_car16);
        tv_car20 = (TextView) findViewById(R.id.tv_car20);
        tv_car50 = (TextView) findViewById(R.id.tv_car50);

        numPick_fac50 = (NumberPicker) findViewById(R.id.numPick_fac50);
        numPick_fac20 = (NumberPicker) findViewById(R.id.numPick_fac20);
        numPick_fac16 = (NumberPicker) findViewById(R.id.numPick_fac16);
        numPick_fac4 = (NumberPicker) findViewById(R.id.numPick_fac4);

        numPick_car50 = (NumberPicker) findViewById(R.id.numPick_car50);
        numPick_car20 = (NumberPicker) findViewById(R.id.numPick_car20);
        numPick_car16 = (NumberPicker) findViewById(R.id.numPick_car16);
        numPick_car4 = (NumberPicker) findViewById(R.id.numPick_car4);

        numPick_fac50.setMaxValue(99);
        numPick_fac50.setMinValue(0);
        numPick_fac20.setMaxValue(99);
        numPick_fac20.setMinValue(0);
        numPick_fac16.setMaxValue(99);
        numPick_fac16.setMinValue(0);
        numPick_fac4.setMaxValue(99);
        numPick_fac4.setMinValue(0);

        numPick_car50.setMaxValue(99);
        numPick_car50.setMinValue(0);
        numPick_car20.setMaxValue(99);
        numPick_car20.setMinValue(0);
        numPick_car16.setMaxValue(99);
        numPick_car16.setMinValue(0);
        numPick_car4.setMaxValue(99);
        numPick_car4.setMinValue(0);

        fac_content = new String[4];
        car_content = new String[4];
    }

    public class NumChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
            switch (numberPicker.getId()) {
                case R.id.numPick_fac50:
                    Log.e("factory", "old:" + oldValue);
                    Log.e("factory", "new:" + newValue);
                    fac_list[0] = newValue;
                    break;
                case R.id.numPick_fac20:
                    fac_list[1] = newValue;
                    break;
                case R.id.numPick_fac16:
                    fac_list[2] = newValue;
                    break;
                case R.id.numPick_fac4:
                    fac_list[3] = newValue;
                    break;

                case R.id.numPick_car50:
                    Log.e("factory", "old:" + oldValue);
                    Log.e("factory", "new:" + newValue);
                    car_list[0] = newValue;
                    break;
                case R.id.numPick_car20:
                    car_list[1] = newValue;
                    break;
                case R.id.numPick_car16:
                    car_list[2] = newValue;
                    break;
                case R.id.numPick_car4:
                    car_list[3] = newValue;
                    break;
            }
        }
    }

    /*check button*/
    public void btn_check(View view) {

        int fac_50 = Integer.parseInt(fac_content[0]) + fac_list[0];
        fac_50 = fac_50 - car_list[0];
        Log.e("factory", "50KG:" + fac_50);
        tv_fac50.setText(String.valueOf(fac_50));

        int fac_20 = Integer.parseInt(fac_content[1]) + fac_list[1];
        fac_20 = fac_20 - car_list[1];
        tv_fac20.setText(String.valueOf(fac_20));

        int fac_16 = Integer.parseInt(fac_content[2]) + fac_list[2];
        fac_16 = fac_16 - car_list[2];
        tv_fac16.setText(String.valueOf(fac_16));

        int fac_4 = Integer.parseInt(fac_content[3]) + fac_list[3];
        fac_4 = fac_4 - car_list[3];
        tv_fac4.setText(String.valueOf(fac_4));

        int car_50 = Integer.parseInt(car_content[0]) + car_list[0];
        Log.e("factory", "50KG:" + car_50);
        tv_car50.setText(String.valueOf(car_50));

        int car_20 = Integer.parseInt(car_content[1]) + car_list[1];
        tv_car20.setText(String.valueOf(car_20));

        int car_16 = Integer.parseInt(car_content[2]) + car_list[2];
        tv_car16.setText(String.valueOf(car_16));

        int car_4 = Integer.parseInt(car_content[3]) + car_list[3];
        tv_car4.setText(String.valueOf(car_4));

        fac_car_content[0] = fac_list[0] + "," + fac_list[1] + "," + fac_list[2] + "," + fac_list[3];
        fac_car_content[1] = car_list[0] + "," + car_list[1] + "," + car_list[2] + "," + car_list[3];

        if (fac_car_content[0].equals("0,0,0,0") && fac_car_content[1].equals("0,0,0,0")) {
            Toast.makeText(this, "請輸入數量", Toast.LENGTH_SHORT).show();
        } else if (fac_car_content[0].equals("0,0,0,0")) {
            for_loop = 1;
            flag = 1;
            new Update().start();
            Log.e("factory", "flag = 1");

        } else if (fac_car_content[1].equals("0,0,0,0")) {
            for_loop = 1;
            flag = 0;
            new Update().start();

        } else { //兩者都有輸入的情況
            for_loop = 2;
            flag = 0;
        }
    }

    //TODO 付款
    public void btn_pay(View view) {
        Log.e("factory", "total pay:" + total_money);
        Log.e("factory", "payment_id:" + paymentId);
        Intent intent = new Intent(this, FactoryDialogActivity.class);
        intent.putExtra("total_money", total_money);
        intent.putExtra("payment_id", paymentId);
        startActivity(intent);
    }

    private class Update extends Thread {

        @Override
        public void run() {

            //送鋼瓶至工廠
            url[0] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_facy.php?suppliers_id=" + suppliers_id +
                    "&car_id=" + car_id + "&facylN_content=" + fac_car_content[0] + "&inout_type=1";

            //從工廠取鋼瓶
            url[1] = "http://198.245.55.221:8089/ProjectGAPP/php/upd_facy.php?suppliers_id=" + suppliers_id +
                    "&car_id=" + car_id + "&facylN_content=" + fac_car_content[1] + "&inout_type=-1";

            Log.e("retSrc", "Update url:" + url[flag]);
            for (int i = 0; i < for_loop; i++) {
                HttpGet httpget = new HttpGet(url[flag]);
                HttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    Log.e("retSrc", "讀取 JSON-2...");
                    HttpEntity resEntity = response.getEntity();
                    if (for_loop == 2) {
                        flag++;
                        Log.e("factory", "進入for-loop");
                        continue;
                    }

                    if (resEntity != null) {
                        String retSrc = EntityUtils.toString(resEntity);
                        Log.e("factory retSrc:", retSrc);
                    }


                } catch (Exception e) {
                    Log.e("retSrc", "讀取JSON Error...");
                }
                Message message = new Message();
                message.what = 0;
                myHandler.sendMessage(message);
                httpclient.getConnectionManager().shutdown();
            }
        }


    }

    //TODO 11/30解決
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("factory", "getIntent");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
            }
        }
    };

    private class PaymentFactoryDownload extends AsyncTask<String, Integer, Void> {

        String payment_id;
        String payment_type;
        String payment_status;
        String payment_content;
        String payment_money_cash;
        String payment_build_date;


        @Override
        protected Void doInBackground(String... urls) {
            try {
                Log.e("factory", "付款按鈕downLoad");
                JSONArray jsonArrayPayment = new JSONArray(getJSONData(urls[0]));
                JSONArray payment;
                paymentList = new ArrayList<>();
                for (int i = 0; i < jsonArrayPayment.length(); i++) {
                    payment = jsonArrayPayment.getJSONArray(i);
                    payment_id = payment.getString(Constant.PAYMENT_ID);
                    Log.e("factory", "id");
                    payment_type = payment.getString(Constant.PAYMENT_TYPE);
                    Log.e("factory", "type");
                    payment_status = payment.getString(Constant.PAYMENT_STATUS);
                    Log.e("factory", "status");
                    payment_content = payment.getString(Constant.PAYMENT_CONTENT);
                    Log.e("factory", "content");
                    payment_build_date = payment.getString(Constant.PAYMENT_BUILD_DATE);
                    Log.e("factory", "date");
                    payment_money_cash = payment.getString(Constant.PAYMENT_MONEY_CASH);
                    Log.e("factory", "cash");
                    PaymentList list = new PaymentList(payment_id, payment_type, payment_status,
                            payment_content, payment_money_cash, payment_build_date);
                    Log.e("factory", "list");
                    paymentList.add(list);
                    Log.e("factory", "add list");
                }
            } catch (Exception e) {
                Log.e("factory", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int money = 0;
            for (int i = 0; i < paymentList.size(); i++) {
                int money_cash = Integer.parseInt(paymentList.get(i).getPayment_money_cash());
                money += money_cash;
                if (i==0){
                    paymentId = paymentList.get(i).getPayment_id();
                }else {
                    paymentId += ","+paymentList.get(i).getPayment_id();
                }
            }
            total_money = String.valueOf(money);

        }
    }

    private class AsyncFactoryDownload extends AsyncTask<String, Integer, Void> {

        String facylN_id;
        String facylN_content;
        String carcylN_id;
        String carcylN_content;


        @Override
        protected Void doInBackground(String... urls) {
            try {

                JSONArray jsonArrayFactory = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayCar = new JSONArray(getJSONData(urls[1]));
                JSONArray factory;
                JSONArray car;

                //目前只抓最後一筆資料
                int i = jsonArrayFactory.length() - 1;
                int j = jsonArrayCar.length() - 1;

                factory = jsonArrayFactory.getJSONArray(i);
                facylN_id = factory.getString(Constant.FACYLN_ID);
                suppliers_id = factory.getString(Constant.SUPPLIERS_ID);
                facylN_content = factory.getString(Constant.FACYLN_CONTENT);

                car = jsonArrayCar.getJSONArray(j);
                carcylN_id = car.getString(Constant.CARCYLN_ID);
                car_id = car.getString(Constant.CAR_ID);
                carcylN_content = car.getString(Constant.CARCYLN_CONTENT);
                Log.e("factory", "執行完畢");


            } catch (Exception e) {
                Log.e("factory", "資料抓取有誤");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fac_content = facylN_content.split(",");
            car_content = carcylN_content.split(",");

            tv_fac50.setText(fac_content[0]);
            tv_fac20.setText(fac_content[1]);
            tv_fac16.setText(fac_content[2]);
            tv_fac4.setText(fac_content[3]);

            fac_list = new int[]{0, 0, 0, 0};
            car_list = new int[]{0, 0, 0, 0};

            tv_car50.setText(car_content[0]);
            tv_car20.setText(car_content[1]);
            tv_car16.setText(car_content[2]);
            tv_car4.setText(car_content[3]);

            //存工廠id
            fac_car_id[0] = facylN_id;
            //存車上id
            fac_car_id[1] = carcylN_id;
        }

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

