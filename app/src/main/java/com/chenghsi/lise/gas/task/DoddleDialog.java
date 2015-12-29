package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONException;

public class DoddleDialog extends Activity {

    TextView tv_pre_degree;
    EditText edt_doddle;
    Button btn_cancel;
    Button btn_confirm;

    String up_doddle_id;
    String up_degree;
    String doddle_prev_degree;

    Toast showToast;

    String url;
    String retSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doddle_dialog);
        tv_pre_degree = (TextView) findViewById(R.id.doddle_prev_degree);
        edt_doddle = (EditText) findViewById(R.id.edt_doddle);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        //取得 up_doddle_id
        Intent intent = getIntent();
        up_doddle_id = intent.getStringExtra("doddle_id");
        String customer_id = intent.getStringExtra("customer_id");
        url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=doddle&where=customer_id~"+customer_id;
        new Update().start();
        Log.e("doddleDialog","doddle_id:"+up_doddle_id);
        Log.e("doddleDialog", "cus_id:"+customer_id);

        btn_confirm.setOnClickListener(new ConfirmListener());
        btn_cancel.setOnClickListener(new CancelListener());
    }

    private class ConfirmListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            up_degree = edt_doddle.getText().toString();
            url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_dod.php?" +
                    "doddle_id=" + up_doddle_id + "&doddle_this_phase_degree=" + up_degree;
            if (!up_degree.equals("")) {
                new Update().start();
                finish();
            } else {
                showToast = Toast.makeText(DoddleDialog.this, "請輸入抄錶度數", Toast.LENGTH_SHORT);
                showToast.show();
            }
        }
    }

    private class CancelListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class Update extends Thread {

        @Override
        public void run() {
            HttpClient httpclient = null;
            try {
                HttpGet httpget;
                httpclient = new DefaultHttpClient();
                HttpResponse response;
                httpget = new HttpGet(url);
                response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                    Log.e("retSrc", "完整資料：" + retSrc);
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {
                doddle_prev_degree(retSrc);
                assert httpclient != null;
                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    private void doddle_prev_degree(String degree){
        try {
            JSONArray jsonArray = new JSONArray(degree);
            JSONArray doddleArray = jsonArray.getJSONArray(jsonArray.length()-2);
            doddle_prev_degree = doddleArray.getString(Constant.DODDLE_THIS_PHASE_DEGREE);
            Message message = new Message();
            message.what = 0;
            myHandler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_pre_degree.setText(doddle_prev_degree);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (showToast != null) {
            showToast.cancel();
        }
    }

}
