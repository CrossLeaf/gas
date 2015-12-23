package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.DetailTaskDownLoad;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.StaffList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DetailedTaskActivity extends Activity {
    protected Toolbar toolbar;
    private View R_name;
    private View R_address;
    private View R_remnant;

    //    private View R_allowance;
    private View R_receive;
    private View R_clientPhones;
    private View R_staff;
    private View cylinders;

//    private TextView history;

    private Button btn_strikeBalance;
    private Spinner spi_payMethod;
    private Spinner clientPhones;
    private Spinner staffChoice;

    //    private Button btn_finish;
    private EditText gas_residual;
    private EditText receive;

    //    private EditText gas_allowance;
    private EditText cylinder_input;
    private EditText cylinder_num;
    private ImageButton cylinders_down;
    private ImageButton cylinders_up;
    private ImageButton cylinder_num_down;
    private ImageButton cylinder_num_up;
    private TextView total_pay;
    private TextView tv_order_remark;

    //bundle 傳進來的詳細資料
    private String clientName;
    private String address;
    private String phonesNum;
    private String partner;
    private String contents;
    private String gasResidual;
    private String customer_settle_type;
    private String order_status;
    private String order_remark;

    public static String totalPay;
    private String id;
    private String[] data;
    private String[] partner_id;
    private String[] money_pay;
    private boolean[] checkList;
    private String[] orderId;
    public ArrayList<StaffList> partnerList;

    //回傳api 成員
    private String customerId;
    private String order_id;
    private String order_money_credit;
    private String order_cylinders_list;
    private String order_gas_residual;
    private String strId = "";
    private String income_money_real;
    private String order_payment;
    private String order_staff_help;

    //系統撥打電話動作
    private String callAction;

    String[] cylinders_list = {};
    String[] gasKg = {"50", "20", "16", "4"};

    String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=order&where=customer_id~";
    private int cc = 1;
    private SharedPreferences sp;

//    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getString("orderId");
        clientName = bundle.getString("clientName");
        address = bundle.getString("address");
        phonesNum = bundle.getString("phones"); //會影響
        contents = bundle.getString("contents");
        customerId = bundle.getString("customerId");
        totalPay = bundle.getString("totalPay");
        gasResidual = bundle.getString("gasResidual");
        customer_settle_type = bundle.getString("settleType");
        order_status = bundle.getString("orderStatus");
        order_remark = bundle.getString("orderRemark");

        Log.e("detailTask", "customer_settle_type:" + customer_settle_type);
        order_gas_residual = gasResidual;
        order_id = id;

        callAction = Intent.ACTION_CALL;
        //下載沖帳的時間和金錢
        url += customerId;
        new DetailTaskDownLoad().execute(url, id);

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

//        R_allowance = findViewById(R.id.tvi_allowance);
        R_receive = findViewById(R.id.tvi_receive);
        R_clientPhones = findViewById(R.id.tv_spr_phones);

        //MengHan write
        R_staff = findViewById(R.id.tv_spr_staff);
        cylinders = findViewById(R.id.indexed_task);
        btn_strikeBalance = (Button) findViewById(R.id.btn_strikeBalance);
        spi_payMethod = (Spinner) findViewById(R.id.spi_payMethod);
//        btn_finish = (Button) findViewById(R.id.btn_finish);
        total_pay = (TextView) findViewById(R.id.total_pay);
        tv_order_remark = (TextView) findViewById(R.id.tv_order_remark);

        // Set title
        ((TextView) R_name.findViewById(R.id.text1)).setText(R.string.name);
        ((TextView) R_address.findViewById(R.id.text1)).setText(R.string.address);
        ((TextView) R_remnant.findViewById(R.id.text)).setText(R.string.remnant);
//        ((TextView) R_allowance.findViewById(R.id.text)).setText(R.string.allowance);
        ((TextView) R_receive.findViewById(R.id.text)).setText(R.string.should_receive);
        ((TextView) R_clientPhones.findViewById(R.id.title)).setText(R.string.phone);
        ((TextView) R_staff.findViewById(R.id.title)).setText("夥伴");

        gas_residual = (EditText) R_remnant.findViewById(R.id.text2);
        gas_residual.setText(order_gas_residual);
//        gas_allowance = (EditText) R_allowance.findViewById(R.id.text2);
        receive = (EditText) R_receive.findViewById(R.id.text2);
        clientPhones = ((Spinner) R_clientPhones.findViewById(R.id.spinner));
        staffChoice = (Spinner) R_staff.findViewById(R.id.spinner);

        cylinder_input = (EditText) cylinders.findViewById(R.id.text2);
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
        Log.e("detail", "------resume-----");
        ((TextView) R_name.findViewById(R.id.text2)).setText(clientName);
        ((TextView) R_address.findViewById(R.id.text2)).setText(address);

        total_pay.setText(totalPay);
        tv_order_remark.setText(order_remark);
        //打電話
        String phones[] = {"請選擇其他號碼", this.phonesNum};
        ArrayAdapter apt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientPhones.setAdapter(apt);
        //電話撥號
        clientPhones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("detail", "touch" + i);
                i++;
                clientPhones.setOnItemSelectedListener(spnListener);
                return false;
            }
        });

        //選擇夥伴
        partnerList = NewTaskActivity.partnerList;
        String partner[] = new String[partnerList.size()];
        partner_id = new String[partnerList.size()];
        partner[0] = "請選擇其他夥伴";
        partner_id[0] = "0";
        order_staff_help = partner[0];
        //從SharedPreferences拿id
        sp = getSharedPreferences("LoginInfo", this.MODE_PRIVATE);
        String staffId = sp.getString("staff_id", null);
        int k = 1;
        for (int i = 0; i < partnerList.size(); i++) {
            if (staffId.equals(partnerList.get(i).getStaff_id())) {
                continue;
            }
            partner_id[k] = partnerList.get(i).getStaff_id();
            partner[k] = partnerList.get(i).getStaff_name();
            Log.e("detailTask", "夥伴們：" + partner[k]);
            k++;
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, partner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staffChoice.setAdapter(arrayAdapter);
        staffChoice.setOnItemSelectedListener(partnerListener);


        //殘氣輸入狀態監聽
        gas_residual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.e("detail", "residual:" + gas_residual.getText().toString());
                order_gas_residual = gas_residual.getText().toString();
//                flag = 0;
                new ShowUpdate().start();
            }
        });

        /*訂單瓦斯數量設定*/
        order_cylinders_list = contents;
        cylinders_list = contents.split(",");
        Log.e("detail", "----cylinder----");
        cylinder_input.setText(gasKg[0]);
        cylinder_num.setText(cylinders_list[0]);
        cylinder_input.clearFocus();
        cylinder_input.setInputType(InputType.TYPE_NULL);
        cylinder_num.setInputType(InputType.TYPE_NULL);
        /*訂單瓦斯數量設定結束*/

        /*沖帳按鈕*/
        btn_strikeBalance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                data = DetailTaskDownLoad.data;
                Log.e("DetailedTask", "data長度：" + data.length);
                if (customer_settle_type.equals("2")) {
                    Toast.makeText(DetailedTaskActivity.this, "此筆為月結戶", Toast.LENGTH_SHORT).show();
                } else if (data.length == 0) {
                    Toast.makeText(DetailedTaskActivity.this, "無其他沖帳項目", Toast.LENGTH_SHORT).show();
                } else if (order_status.equals("2")) {
                    strId = "";
                    if (cc == 1) {
                        checkList = new boolean[data.length];
                        orderId = DetailTaskDownLoad.order_id;
                        Log.e("DetailedTask", "checkList:" + checkList.length);
                        cc++;
                    }
                    strikeBalance();
                }
            }
        });
        /*沖帳按鈕結束*/

        /*付費方式*/
        if (customer_settle_type.equals("1")) {
            money_pay = new String[]{"現金", "支票", "匯款"};
        } else {
            money_pay = new String[]{"月結"};
        }

        order_payment = money_pay[0];
//        String[] payMethod_array = {"現金", "支票", "匯款"};
        ArrayAdapter<String> payMethod_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, money_pay);
        payMethod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_payMethod.setAdapter(payMethod_adapter);
        spi_payMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                money_pay[i] = adapterView.getSelectedItem().toString();
                order_payment = money_pay[i];
                Log.e("DetailedTask", money_pay[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*付費方式結束*/
    }
    /*OnResume 結束*/

    /*電話監聽事件*/
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
                Intent intent = new Intent(callAction, uri);
                startActivity(intent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    /*電話監聽事件結束*/

    /*選擇夥伴監聽事件*/
    private Spinner.OnItemSelectedListener partnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            partner = parent.getSelectedItem().toString();
            order_staff_help = partner_id[position];

            Log.e("detailedTask", "夥伴名稱：" + partner);
            Log.e("detailedTask", "夥伴ID:" + order_staff_help);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    /*選擇夥伴監聽事件結束*/

    /*瓦斯數量調整*/

    //計算點擊上or下
    int j = 0;

    int cylinder_temp = 0;

    //計算目前為陣列的哪個
    int which;

    //瓦斯種類調整
    public void btn_gasAdjust(View view) {
//        flag = 0;
        switch (view.getId()) {
            case R.id.cylinders_up:
                Log.e("detail", "cy up被點選");
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

        order_cylinders_list = cylinders_list[0] + "," + cylinders_list[1] + "," + cylinders_list[2] + "," + cylinders_list[3];
        Log.e("detail", "cylinders_list:" + order_cylinders_list);
        new ShowUpdate().start();
    }
    /*瓦斯數量調整結束*/


    /*沖帳的多選項dialog*/
    private void strikeBalance() {
        order_id = id;
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
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkList.length; i++) {
                            if (checkList[i]) {
                                strId += "_" + orderId[i];
                            }
                        }
                        order_id += strId;
                        new ShowUpdate().start();
                    }
                })
                .create().show();
    }
    /*沖帳結束*/

    /*現銷*/
    public void btn_money_cash(View view) {
        order_money_credit = "0";
        if (order_gas_residual.equals("0")) {
        } else {
            order_gas_residual = gas_residual.getText().toString();
        }
        if (receive.getText().toString().trim().equals("") || receive.getText().toString().trim().equals("0")) {
            Toast.makeText(this, "請輸入實收金額", Toast.LENGTH_SHORT).show();
        } else {
            income_money_real = receive.getText().toString();


            for (int k = 0; k < cylinders_list.length; k++) {
                if (k == 0) {
                    order_cylinders_list = cylinders_list[k];
                } else {
                    order_cylinders_list += "," + cylinders_list[k];
                }
            }
            Log.e("cash", order_id);
            Log.e("cash", order_money_credit);
            Log.e("cash", order_staff_help);
            Log.e("cash", order_cylinders_list);
            Log.e("cash", order_payment);
            Log.e("cash", order_gas_residual);
            Log.e("cash", income_money_real);
            new Update().start();
            Toast.makeText(DetailedTaskActivity.this, "Loading...", Toast.LENGTH_LONG).show();
        }
    }

    /*賒銷*/
    public void btn_money_credit(View view) {
        if (receive.getText().toString().trim().equals("") || receive.getText().toString().trim().equals("0")){
            order_money_credit = "1";
            for (int k = 0; k < cylinders_list.length; k++) {
                if (k == 0) {
                    order_cylinders_list = cylinders_list[k];
                } else {
                    order_cylinders_list += "," + cylinders_list[k];
                }
            }
            if (!order_gas_residual.equals("0")) {
                order_gas_residual = gas_residual.getText().toString();
            }
            new Update().start();
            Toast.makeText(DetailedTaskActivity.this, "Loading...", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(DetailedTaskActivity.this, "注意！有輸入實收", Toast.LENGTH_SHORT).show();
        }
    }


    private class Update extends Thread {

        @Override
        public void run() {

            String url = "";
            if (order_money_credit.equals("0")) {
                Log.e("detailTask", "呼叫 現銷");
                url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_cash_credit.php?order_id=" + order_id +
                        "&order_money_credit=" + order_money_credit + "&order_cylinders_list=" + order_cylinders_list +
                        "&order_gas_residual=" + order_gas_residual + "&income_money_real=" + income_money_real +
                        "&order_payment=" + java.net.URLEncoder.encode(order_payment) + "&order_staff_help=" + order_staff_help;
            } else {
                Log.e("detailTask", "呼叫 賒銷");
                url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_cash_credit.php?order_id=" + order_id +
                        "&order_money_credit=" + order_money_credit + "&order_cylinders_list=" + order_cylinders_list +
                        "&order_gas_residual=" + order_gas_residual + "&order_payment=" + java.net.URLEncoder.encode(order_payment) +
                        "&order_staff_help=" + order_staff_help;
            }
            Log.e("detailTask", "update url:" + url);
            String retSrc;
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "完整的資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                    Log.e("retSrc", "不完整資料：" + retSrc);
                }
            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
            } finally {
                finish();
                httpclient.getConnectionManager().shutdown();
            }
        }
    }

    //顯示應付金額的api
    private class ShowUpdate extends Thread {

        @Override
        public void run() {
            totalPay = "";
            String url;
            Log.e("detail", "order_id:" + order_id);
            url = "http://198.245.55.221:8089/ProjectGAPP/php/show_cash_credit.php?order_id=" + order_id +
                    "&order_cylinders_list=" + order_cylinders_list + "&order_gas_residual=" + order_gas_residual;
            String retSrc;
            Log.e("detail", "url:" + url);
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("detail", "show update:"+retSrc);
                    //將字串中的數字取出
                    String subStr =retSrc.substring(12);
                    Log.e("detail", "subStr:"+subStr);
                    totalPay = subStr.trim();
                    /*正則表示式*/
                    /*Pattern p = Pattern.compile("[0-9]");
                    Matcher m = p.matcher(retSrc);
                    totalPay = "";
                    while (m.find()) {
                        totalPay += m.group();
                    }*/
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                    Log.e("retSrc", "完整資料：" + retSrc);
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
                    total_pay.setText(totalPay);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("detail", "------pause-----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("detail", "------stop-----");
//        finish();
    }
}
