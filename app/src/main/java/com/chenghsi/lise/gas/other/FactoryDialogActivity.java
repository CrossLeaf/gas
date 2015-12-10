package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.FactoryDialogAdapter;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

/**
 * Created by MengHan on 2015/12/7.
 */
public class FactoryDialogActivity extends Activity {

    TextView tv_pay;
    TextView tv_real_pay;
    EditText edt_residual;
    Button btn_confirm;
    Button btn_cancel;
    ListView lv_payment;

    private FactoryDialogAdapter adapter;
    private String total_money;
    private String real_pay;
    private String saveout_id;
    private String saveout_univalent;
    private String[] saveoutId_array;
    private String retSrc;

    String url;

    private int intResidual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_factory_payment);


        Intent intent = getIntent();
        total_money = intent.getStringExtra("total_money");
        saveout_id = intent.getStringExtra("saveout_id");
        Log.e("fac dia", "total:" + total_money);
        try {
            saveoutId_array = saveout_id.split("_");
        }catch (RuntimeException runTimeException){
            Log.e("fac dia", "saveout_id only one");
            saveoutId_array = new String[1];
            saveoutId_array[0] = saveout_id;
        }
            Log.e("fac dia", "saveoutId_array[0]:" + saveoutId_array[0]);
            url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=saveout&where=saveout_id~" + saveoutId_array[0];
            new Update().start();


        lv_payment = (ListView) findViewById(R.id.lv_payment);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_real_pay = (TextView) findViewById(R.id.tv_real_pay);
        edt_residual = (EditText) findViewById(R.id.edt_residual);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        tv_pay.setText(total_money);
        tv_real_pay.setText(total_money);

        adapter = new FactoryDialogAdapter(this, FactoryActivity.paymentList);
        lv_payment.setAdapter(adapter);

        edt_residual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    JSONArray jsonArray = new JSONArray(retSrc);
                    saveout_univalent = jsonArray.getJSONArray(0).getString(6);
                    Log.e("fac dia", "saveout_univalent:" + saveout_univalent);

                    String residual = edt_residual.getText().toString();
                    Log.e("fac dia", "residual:" + residual);
                    if (residual.equals("")) {
                        intResidual = 0;
                    } else {
                        intResidual = Integer.parseInt(residual);
                    }
                    int int_residual_pay =  intResidual*Integer.parseInt(saveout_univalent);
                    int int_total_money = Integer.parseInt(total_money);
                    real_pay = String.valueOf(int_total_money - int_residual_pay);
                    Log.e("fac dia", "real_pay:" + real_pay);
                    //殘氣折讓金額=瓦斯殘氣 * 存入氣ID[0]的saveout_univalent欄位
                    //應付金額=(總入氣-瓦斯殘氣) * 存入氣ID[0]的saveout_univalent欄位 + 其他筆付款的應付金額
                    //實際付款金額 ＝ 應付金額 - 殘氣折讓金額
                    tv_real_pay.setText(real_pay);
                } catch (Exception e) {
                    Log.e("fac dia", "exception:" + e.getMessage());
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 之後新增 payment_staff_id
                String residual = String.valueOf(intResidual);
                url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_pay_facy.php?payment_id=" + saveout_id +
                        "&money_real=" + real_pay + "&gas_residual=" + residual;
                new Update().start();
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    class Update extends Thread {


        @Override
        public void run() {

            Log.e("fac dia", "url:" + url);

            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                Log.e("retSrc", "讀取 JSON-2...");
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("factory retSrc:", retSrc);
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
