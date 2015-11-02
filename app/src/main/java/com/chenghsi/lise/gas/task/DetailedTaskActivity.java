package com.chenghsi.lise.gas.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chenghsi.lise.gas.R;


public class DetailedTaskActivity extends Activity {
    protected Toolbar toolbar;
    private View R_name;
    private View R_address;
    private View R_remnant;
    private View R_allowance;
    private View R_receive;
    private View R_clientPhones;
    private View gas;
    private View cylinders;

    private TextView history;

    private ListView lv_cylinder;

    private int position;

    private String clientName;
    private String address;
    private String phones;
    private String contents;

    String[] cylinders_list = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        Bundle bundle = this.getIntent().getExtras();
        clientName = bundle.getString("clientName");
        address = bundle.getString("address");
        phones = bundle.getString("phones");
        contents = bundle.getString("contents");

        Log.e("DetailedTask", "" + position);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_detailed_task);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        gas = findViewById(R.id.indexed_task);
        cylinders = findViewById(R.id.indexed_task);

        history = (TextView) findViewById(R.id.text3);
        // Set title

        ((TextView) R_name.findViewById(R.id.text1)).setText(R.string.name);
        ((TextView) R_address.findViewById(R.id.text1)).setText(R.string.address);
        ((TextView) R_remnant.findViewById(R.id.text)).setText(R.string.remnant);
        ((TextView) R_allowance.findViewById(R.id.text)).setText(R.string.allowance);
        ((TextView) R_receive.findViewById(R.id.text)).setText(R.string.should_receive);
        ((TextView) R_clientPhones.findViewById(R.id.title)).setText(R.string.phone);
        history.setText("歷史紀錄");

    }

    @Override
    protected void onResume() {
        super.onResume();

        ((TextView) R_name.findViewById(R.id.text2)).setText(clientName);
        ((TextView) R_address.findViewById(R.id.text2)).setText(address);
//        ((TextView)R_name.findViewById(R.id.text2)).setText(TestData.name[position]);
//        ((TextView)R_address.findViewById(R.id.text2)).setText(TestData.address[position]);

        String phones[] = {this.phones};
        ArrayAdapter apt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) R_clientPhones.findViewById(R.id.spinner)).setAdapter(apt);

        //MengHan另外新增的
        lv_cylinder = (ListView) cylinders.findViewById(R.id.cylinder_listView);
        setListViewHeightBasedOnChildren(lv_cylinder);
        cylinders_list = contents.split(",");   //儲存著瓦斯種類的陣列

        (gas.findViewById(R.id.btn_gas)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetailBaseAdapter apt = new DetailBaseAdapter(DetailedTaskActivity.this);
                lv_cylinder.setAdapter(apt);
            }

        });

    }

    public void btn_history(View view) {
        Intent intent = new Intent();
        intent.setClass(this, HistoryBottleActivity.class);
        startActivity(intent);
    }


    private class DetailBaseAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public DetailBaseAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        String[] gasKg = {"50Kg x ", "20Kg x ", "16Kg x ", "4Kg x "};
        int testCount = 1;

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.adapter_item_detail_cylinder, parent, false);
                viewHolder.cylinder = (TextView) convertView.findViewById(R.id.tv_cylinder);
                viewHolder.number = (EditText) convertView.findViewById(R.id.edt_cylinder_num);
                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            Log.e("detail", "test count:" + testCount);
            //// TODO: 2015/11/1 顯示完整listView
            viewHolder.cylinder.setText(gasKg[position]);
            viewHolder.number.setText(cylinders_list[position]);
            return convertView;
        }


        public class ViewHolder {
            TextView cylinder;
            EditText number;
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
