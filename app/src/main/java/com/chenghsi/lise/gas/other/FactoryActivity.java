package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.Globals;
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

/**
 * Created by MengHan on 2015/11/13.
 */
public class FactoryActivity extends Activity {
    protected Toolbar toolbar;
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

    ImageButton igbtn_check;
    ImageButton igbtn_pay;

    private String url_fac = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=facylN";
    private String url_car = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=carcylN&where=car_id~";
    private String url_payment = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=payment&where=payment_type~%E5%AD%98%E5%85%A5%E6%B0%A3";
    private String[] url = new String[2];
    private String which_call;

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
    private String saveout_id;

    int for_loop;
    private int flag;
    private String facylNId;
    private String carcylNId;

    private Toast showToastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        new AsyncFactoryDownload().execute(url_fac, url_car);
        new PaymentFactoryDownload().execute(url_payment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_saveout);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //有exception可以讓畫面定格
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

                Log.e("Alert", "Lets See if it Works !!!");

            }
        });*/

        fac_content = new String[4];
        car_content = new String[4];
        showToastMessage = Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT);
//        showToastMessage.show();
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

        igbtn_check = (ImageButton) findViewById(R.id.igbtn_check);
        igbtn_pay = (ImageButton) findViewById(R.id.igbtn_pay);

        tv_fac50.setText(fac_content[0]);
        tv_fac20.setText(fac_content[1]);
        tv_fac16.setText(fac_content[2]);
        tv_fac4.setText(fac_content[3]);

        tv_car50.setText(car_content[0]);
        tv_car20.setText(car_content[1]);
        tv_car16.setText(car_content[2]);
        tv_car4.setText(car_content[3]);

        fac_list = new int[]{0, 0, 0, 0};
        car_list = new int[]{0, 0, 0, 0};
        //存工廠id
        fac_car_id[0] = facylNId;
        //存車上id
        fac_car_id[1] = carcylNId;

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

        numPick_fac50.setOnValueChangedListener(new NumChangeListener());
        numPick_fac20.setOnValueChangedListener(new NumChangeListener());
        numPick_fac16.setOnValueChangedListener(new NumChangeListener());
        numPick_fac4.setOnValueChangedListener(new NumChangeListener());

        numPick_car50.setOnValueChangedListener(new NumChangeListener());
        numPick_car20.setOnValueChangedListener(new NumChangeListener());
        numPick_car16.setOnValueChangedListener(new NumChangeListener());
        numPick_car4.setOnValueChangedListener(new NumChangeListener());

        igbtn_check.setOnClickListener(new Btn_check());
        igbtn_pay.setOnClickListener(new Btn_pay());
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
    class Btn_check implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.e("factory", "fac_content:" + fac_content[0]);
            Log.e("factory", "fac_list:" + fac_list[0]);
            Log.e("factory", "car_list:" + car_list[0]);

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
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(FactoryActivity.this, "請輸入數量", Toast.LENGTH_SHORT);
                showToastMessage.show();
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
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(FactoryActivity.this, "請重新輸入", Toast.LENGTH_LONG);
                showToastMessage.show();
                Log.e("factory", "兩者都有輸入");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }


    /*付款*/
    class Btn_pay implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.e("factory", "total pay:" + total_money);
            Log.e("factory", "payment_id:" + saveout_id);
            if (paymentList!=null) {
                Intent intent = new Intent(FactoryActivity.this, FactoryDialogActivity.class);
                intent.putExtra("total_money", total_money);
                intent.putExtra("saveout_id", saveout_id);
                startActivity(intent);
            } else {
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(FactoryActivity.this, "付款項目為空", Toast.LENGTH_SHORT);
                showToastMessage.show();
            }
        }
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
                which_call = "PaymentFactoryDownload";
                Log.e("factory", "付款按鈕downLoad");
                JSONArray jsonArrayPayment = new JSONArray(getJSONData(urls[0]));
                JSONArray payment;
                paymentList = new ArrayList<>();
                Log.e("factory", "陣列長度：" + jsonArrayPayment.length());

                for (int i = 0; i < jsonArrayPayment.length(); i++) {
                    payment = jsonArrayPayment.getJSONArray(i);

                    //尚未付款才存入ArrayList
                    if (payment.getString(Constant.PAYMENT_STATUS).equals("0")) {
                        payment_id = payment.getString(Constant.PAYMENT_ID);
                        Log.e("factory", "id");
                        payment_type = payment.getString(Constant.PAYMENT_TYPE);
                        payment_status = payment.getString(Constant.PAYMENT_STATUS);
                        payment_content = payment.getString(Constant.PAYMENT_CONTENT);
                        payment_build_date = payment.getString(Constant.PAYMENT_BUILD_DATE);
                        payment_money_cash = payment.getString(Constant.PAYMENT_MONEY_CASH);
                        saveout_id = payment.getString(Constant.SAVEOUT_ID);
                        PaymentList list = new PaymentList(payment_id, payment_type, payment_status,
                                payment_content, payment_money_cash, payment_build_date, saveout_id);
                        Log.e("factory", "list");
                        paymentList.add(list);
                        Log.e("factory", "add list");
                    }
                }
            } catch (Exception e) {
                Log.e("factory", "PaymentFactoryDownload 資料抓取有誤");
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
                if (i == 0) {
                    saveout_id = paymentList.get(i).getSaveout_id();
                } else {
                    saveout_id += "_" + paymentList.get(i).getSaveout_id();
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
        Globals g = new Globals();

        @Override
        protected Void doInBackground(String... urls) {
            try {
                which_call = "AsyncFactoryDownload";
                String urlCar = urls[1]+g.getUser_id();
                JSONArray jsonArrayFactory = new JSONArray(getJSONData(urls[0]));
                JSONArray jsonArrayCar = new JSONArray(getJSONData(urlCar));
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
                Log.e("factory", "car_id:"+car_id);
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

            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
            facylNId = facylN_id;
            carcylNId = carcylN_id;
        }


    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("factory", "getIntent");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
                case 1:
                    init();
                    downLoad_init();
                default:
                    break;
            }
        }
    };

    public void downLoad_init() {

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
                Log.e("retSrc", which_call + "的完整資料：" + retSrc);
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("factory", "-----pause-----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("factory", "-----stop-----");
        showToastMessage.cancel();
        finish();
    }
}

