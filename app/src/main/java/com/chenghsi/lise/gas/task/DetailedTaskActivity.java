package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.DetailTaskDownLoad;
import com.chenghsi.lise.gas.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class DetailedTaskActivity extends Activity {
    protected Toolbar toolbar;
    private View R_name;
    private View R_address;
    private View R_remnant;
    private View R_allowance;
    private View R_receive;
    private View R_clientPhones;
    private View cylinders;

//    private TextView history;

    private ListView lv_cylinder;
    private Button btn_strikeBalance;
    private Spinner spi_payMethod;
    private Spinner clientPhones;
    private Button btn_finish;
    private EditText cylinder_input;
    private EditText cylinder_num;
    private ImageButton cylinders_down;
    private ImageButton cylinders_up;
    private ImageButton cylinder_num_down;
    private ImageButton cylinder_num_up;

    private int position;

    private String clientName;
    private String address;
    private String phonesNum;
    private String contents;

    //回傳api
    private String customerId;
    private String[] money_pay;

    private String action;
    String[] cylinders_list = {};
    String[] gasKg = {"50", "20", "16", "4"};

    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=order&where=customer_id~";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        Bundle bundle = this.getIntent().getExtras();
        clientName = bundle.getString("clientName");
        address = bundle.getString("address");
        phonesNum = bundle.getString("phones"); //會影響
        contents = bundle.getString("contents");
        customerId = bundle.getString("customerId");


        action = Intent.ACTION_CALL;
        //下載沖帳的時間和金錢
        url += customerId;
        new DetailTaskDownLoad().execute(url);
        Log.e("DetailedTask", "" + position);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_detailed_task);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        R_name = findViewById(R.id.tv2_name);
        R_address = findViewById(R.id.tv2_address);
        R_remnant = findViewById(R.id.tvi_remnant);
        R_allowance = findViewById(R.id.tvi_allowance);
        R_receive = findViewById(R.id.tvi_receive);
        R_clientPhones = findViewById(R.id.tv_spr_phones);
        //MengHan
        cylinders = findViewById(R.id.indexed_task);
//        history = (TextView) findViewById(R.id.text3);
        btn_strikeBalance = (Button) findViewById(R.id.btn_strikeBalance);
        spi_payMethod = (Spinner) findViewById(R.id.spi_payMethod);
        btn_finish = (Button) findViewById(R.id.btn_finish);


        // Set title
        ((TextView) R_name.findViewById(R.id.text1)).setText(R.string.name);
        ((TextView) R_address.findViewById(R.id.text1)).setText(R.string.address);
        ((TextView) R_remnant.findViewById(R.id.text)).setText(R.string.remnant);
        ((TextView) R_allowance.findViewById(R.id.text)).setText(R.string.allowance);
        ((TextView) R_receive.findViewById(R.id.text)).setText(R.string.should_receive);
        ((TextView) R_clientPhones.findViewById(R.id.title)).setText(R.string.phone);
//        history.setText("歷史紀錄");

        clientPhones = ((Spinner) R_clientPhones.findViewById(R.id.spinner));

        cylinder_input = (EditText) cylinders.findViewById(R.id.cylinder_input);
        cylinder_num = (EditText) cylinders.findViewById(R.id.cylinder_num);
        cylinders_down = (ImageButton) cylinders.findViewById(R.id.cylinders_down);
        cylinders_up = (ImageButton) cylinders.findViewById(R.id.cylinders_up);
        cylinder_num_down = (ImageButton) cylinders.findViewById(R.id.cylinder_num_down);
        cylinder_num_up = (ImageButton) cylinders.findViewById(R.id.cylinder_num_up);
    }

    //紀錄 spinner touch 事件發生次數
    int i = 1;

    @Override
    protected void onResume() {
        super.onResume();


        ((TextView) R_name.findViewById(R.id.text2)).setText(clientName);
        ((TextView) R_address.findViewById(R.id.text2)).setText(address);
//        ((TextView)R_name.findViewById(R.id.text2)).setText(TestData.name[position]);
//        ((TextView)R_address.findViewById(R.id.text2)).setText(TestData.address[position]);

        String phones[] = {"請選擇其他號碼", this.phonesNum};
        ArrayAdapter apt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientPhones.setAdapter(apt);

        //電話撥號
        clientPhones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("tag", "touch" + i);
                i++;
                clientPhones.setOnItemSelectedListener(spnListener);
                return false;
            }
        });

        //訂單瓦斯數量
        cylinders_list = contents.split(",");
        Log.e("tag", "----cylinder----");
        cylinder_input.setText(gasKg[0]);
        cylinder_num.setText(cylinders_list[0]);
        cylinder_input.clearFocus();
        cylinder_input.setInputType(InputType.TYPE_NULL);
        cylinder_num.setInputType(InputType.TYPE_NULL);

        //沖帳按鈕
        btn_strikeBalance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                strikeBalance();
            }
        });

        money_pay = new String[]{"現金", "支票", "匯款"};
        //TODO 付費方式
        String[] payMethod_array = {"現金", "支票", "匯款"};
        ArrayAdapter<String> payMethod_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, payMethod_array);
        payMethod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_payMethod.setAdapter(payMethod_adapter);
        spi_payMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                money_pay[i] = adapterView.getSelectedItem().toString();
                Log.e("DetailedTask", money_pay[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final boolean[] credit = {false};



       /* btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }

    private Spinner.OnItemSelectedListener spnListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //取得選項內容
            String tel = parent.getSelectedItem().toString();
            if (tel == null || tel.equals("")) {
                Log.e("callphone", "電話欄位為空");
            } else if (tel.equals("請選擇其他號碼")) {
                Log.e("callphone", "點到號碼");
//                Toast.makeText(DetailedTaskActivity.this, "請選擇電話號碼", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("callphone", "打電話出去");
                Uri uri = Uri.parse("tel:" + tel);
                Intent intent = new Intent(action, uri);
                startActivity(intent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Todo
        }
    };


    //TODO 瓦斯調整後需紀錄
    //計算點擊上or下
    int j = 0;
    int cylinder_temp = 0;
    //計算目前為陣列的哪個
    int which;

    //瓦斯種類調整
    public void btn_gasAdjust(View view) {
        switch (view.getId()) {
            case R.id.cylinders_up:
                Log.e("tag", "cy up被點選");
                j--;
                if (j == -1) {
                    j = 3;
                }
                which = j % gasKg.length;
                cylinder_input.setText(gasKg[which]);
                cylinder_num.setText(cylinders_list[which]);
                break;
            case R.id.cylinders_down:
                j++;
                which = j % gasKg.length;
                cylinder_input.setText(gasKg[which]);
                cylinder_num.setText(cylinders_list[which]);
                break;
            case R.id.cylinder_num_up:
                which = j % gasKg.length;
                cylinder_temp = Integer.parseInt(cylinders_list[which]);
                cylinders_list[which] = String.valueOf(cylinder_temp + 1);
                cylinder_num.setText(cylinders_list[which]);
                break;
            case R.id.cylinder_num_down:
                which = j % gasKg.length;
                cylinder_temp = Integer.parseInt(cylinders_list[which]);
                if (cylinder_temp == 0) {
                    break;
                } else {
                    cylinders_list[which] = String.valueOf(cylinder_temp - 1);
                    cylinder_num.setText(cylinders_list[which]);
                    break;
                }
        }
    }

    //TODO 沖帳的多選項dialog
    private void strikeBalance() {
        DetailTaskDownLoad detailTaskDownLoad = new DetailTaskDownLoad();
        final String[] data = detailTaskDownLoad.data;
        final boolean[] checkList = new boolean[data.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Log.e("detail", "do diaLog");

        builder.setTitle("選擇沖帳項目")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMultiChoiceItems(data, checkList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        checkList[which] = isChecked;
                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();

    }

    //TODO 賒銷現銷按鈕動作

    public void btn_money_credit(View view) {
    }

    public void btn_money_cash(View view) {
    }


    //TODO 還要修改 carcy_ln 的api

    private class Update extends Thread {
        @Override
        public void run() {
            String url = "";
            url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=order" +
                    "&tb_where_name=條件欄位名稱&tb_where_val=條件值&tb_td=修改的欄位名稱1|修改的欄位名稱2" +
                    "&tb_val=修改的欄位值1|修改的欄位值2";
            String retSrc;

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
                    Log.e("retSrc", "完整資料：" + retSrc);
                }

            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        }
    }
}
