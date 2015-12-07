package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenghsi.lise.gas.FactoryDialogAdapter;
import com.chenghsi.lise.gas.R;

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

    private int intResidual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_factory_payment);


        Intent intent = getIntent();
        total_money = intent.getStringExtra("total_money");
        Log.e("fac dia", "total:" + total_money);

        lv_payment = (ListView) findViewById(R.id.lv_payment);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_real_pay = (TextView) findViewById(R.id.tv_real_pay);
        edt_residual = (EditText) findViewById(R.id.edt_residual);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        tv_pay.setText(total_money);
        edt_residual.setText("0");
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
                String residual = edt_residual.getText().toString();
                Log.e("fac dia", "residual:"+ residual);
                if (residual.equals("")){
                    intResidual=0;
                }else {
                    intResidual = Integer.parseInt(residual);
                }
                int int_total_money = Integer.parseInt(total_money);
                real_pay = String.valueOf(int_total_money - intResidual);
                Log.e("fac dia", "real_pay:"+ real_pay);
                tv_real_pay.setText(real_pay);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
