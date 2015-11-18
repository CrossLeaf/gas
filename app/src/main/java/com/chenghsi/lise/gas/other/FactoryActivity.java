package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
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

/**
 * Created by MengHan on 2015/11/13.
 */
public class FactoryActivity extends Activity {

    NumberPicker numPick_fac50;
    NumberPicker numPick_fac20;
    NumberPicker numPick_fac16;
    NumberPicker numPick_fac4;

    TextView tv_car50;
    TextView tv_car20;
    TextView tv_car16;
    TextView tv_car4;


    String url_fac = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=facylN";
    String url_car = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=carcylN";

    int[] fac_list;
    String[] fac_car_id = new String[2];
    String[] fac_car_content = new String[2];
    String[] fac_content;   //最後要把這回傳回去
    String[] car_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory);
        init();
        new AsyncFactoryDownload().execute(url_fac, url_car);
        numPick_fac50.setOnValueChangedListener(new NumChangeListener());
        numPick_fac20.setOnValueChangedListener(new NumChangeListener());
        numPick_fac16.setOnValueChangedListener(new NumChangeListener());
        numPick_fac4.setOnValueChangedListener(new NumChangeListener());


    }

    private void init() {
        tv_car4 = (TextView) findViewById(R.id.tv_car4);
        tv_car16 = (TextView) findViewById(R.id.tv_car16);
        tv_car20 = (TextView) findViewById(R.id.tv_car20);
        tv_car50 = (TextView) findViewById(R.id.tv_car50);

        numPick_fac50 = (NumberPicker) findViewById(R.id.numPick_fac50);
        numPick_fac20 = (NumberPicker) findViewById(R.id.numPick_fac20);
        numPick_fac16 = (NumberPicker) findViewById(R.id.numPick_fac16);
        numPick_fac4 = (NumberPicker) findViewById(R.id.numPick_fac4);

        numPick_fac50.setMaxValue(99);
        numPick_fac50.setMinValue(0);
        numPick_fac20.setMaxValue(99);
        numPick_fac20.setMinValue(0);
        numPick_fac16.setMaxValue(99);
        numPick_fac16.setMinValue(0);
        numPick_fac4.setMaxValue(99);
        numPick_fac4.setMinValue(0);

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
            }
        }
    }

    //TODO 工廠數量改變後按下 傳至車上
    public void btn_check(View view) {
        int car_50 = Integer.parseInt(car_content[0]) + (Integer.parseInt(fac_content[0]) - fac_list[0]);
        Log.e("factory", "50KG:" + car_50);
        tv_car50.setText(String.valueOf(car_50));

        int car_20 = Integer.parseInt(car_content[1]) + (Integer.parseInt(fac_content[1]) - fac_list[1]);
        tv_car20.setText(String.valueOf(car_20));

        int car_16 = Integer.parseInt(car_content[2]) + (Integer.parseInt(fac_content[2]) - fac_list[2]);
        tv_car16.setText(String.valueOf(car_16));

        int car_4 = Integer.parseInt(car_content[3]) + (Integer.parseInt(fac_content[3]) - fac_list[3]);
        tv_car4.setText(String.valueOf(car_4));
        fac_car_content[0] = fac_list[0] + "," + fac_list[1] + "," + fac_list[2] + "," + fac_list[3];
        fac_car_content[1] = car_50 + "," + car_20 + "," + car_16 + "," + car_4;

        new CheckUpdate().start();
    }

    //TODO 付款
    public void btn_pay(View view) {

    }

    private class CheckUpdate extends Thread {

        @Override
        public void run() {
            String[] tb_name = new String[]{"facyln", "carcyln"};
            String tb_where_name[] = new String[]{"facyln_id", "carcyln_id"};
            String tb_td[] = new String[]{"facyln_content", "carcyln_content"};
            for (int i = 0; i < fac_car_id.length; i++) {
                String url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name="+tb_name[i]+
                        "&tb_where_name=" + tb_where_name[i] + "&tb_where_val=" + fac_car_id[i] +
                        "&tb_td="+tb_td[i]+"&tb_val=" + fac_car_content[i];
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                Log.e("retSrc", "讀取 JSON-1...");
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    Log.e("retSrc", "讀取 JSON-2...");
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        Log.e("retSrc", "rrrr");
                        String retSrc = EntityUtils.toString(resEntity);
                        Toast.makeText(FactoryActivity.this, "完成", Toast.LENGTH_SHORT).show();
                        Log.e("retSrc", retSrc);
                    }
                } catch (Exception e) {
                    Log.e("retSrc", "讀取JSON Error...");
                } finally {

                    httpclient.getConnectionManager().shutdown();
                }
            }
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

                //目前只有一筆資料才這麼做
                factory = jsonArrayFactory.getJSONArray(0);
                facylN_id = factory.getString(Constant.FACYLN_ID);
                facylN_content = factory.getString(Constant.FACYLN_CONTENT);

                car = jsonArrayCar.getJSONArray(0);
                carcylN_id = car.getString(Constant.CARCYLN_ID);
                carcylN_content = car.getString(Constant.CARCYLN_CONTENT);

            } catch (Exception e) {
                Log.e("delivery", "資料抓取有誤");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fac_content = facylN_content.split(",");
            car_content = carcylN_content.split(",");

            numPick_fac50.setValue(Integer.parseInt(fac_content[0]));
            numPick_fac20.setValue(Integer.parseInt(fac_content[1]));
            numPick_fac16.setValue(Integer.parseInt(fac_content[2]));
            numPick_fac4.setValue(Integer.parseInt(fac_content[3]));

            fac_list = new int[]{Integer.parseInt(fac_content[0]), Integer.parseInt(fac_content[1])
                    , Integer.parseInt(fac_content[2]), Integer.parseInt(fac_content[3])};

            tv_car50.setText(car_content[0]);
            tv_car20.setText(car_content[1]);
            tv_car16.setText(car_content[2]);
            tv_car4.setText(car_content[3]);

            fac_car_id[0] = facylN_id;
            fac_car_id[1] = carcylN_id;
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
