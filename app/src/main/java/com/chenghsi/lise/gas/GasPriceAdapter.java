package com.chenghsi.lise.gas;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MengHan on 2015/10/28.
 */
public class GasPriceAdapter extends BaseAdapter {
    // 定義 LayoutInflater
    private LayoutInflater myInflater;
    // 定義 Adapter 內藴藏的資料容器
    private ArrayList<GasPriceList> list;
    double oldRate = 0.0;

    public GasPriceAdapter(Context context, ArrayList<GasPriceList> list){  //建構子為了動態初始化
        //預先取得 LayoutInflater 物件實體
        myInflater = LayoutInflater.from(context);
        this.list = list;
//        Log.e("listView", list.get(0).getPRICE_BULID_DATE());
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            // 1:將 R.layout.cus_view 實例化
            convertView = myInflater.inflate(R.layout.adapter_item_gas_price, null);

            // 2:建立 UI 標籤結構並存放到 holder
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.price = (TextView)convertView.findViewById(R.id.tv_price);
            holder.rate = (TextView)convertView.findViewById(R.id.tv_rate);

            // 3:注入 UI 標籤結構 --> convertView
            convertView.setTag(holder);

        } else {
            // 取得  UI 標籤結構
            holder = (ViewHolder)convertView.getTag();
        }

        // 4:取得retVal物件資料
        GasPriceList gasList = list.get(position);

        // 5:設定顯示資料
        holder.date.setText(gasList.getPRICE_BULID_DATE());
        holder.price.setText(gasList.getPRICE_20() + "/" + gasList.getPRICE_16());
        double doubleRate = Double.parseDouble(gasList.getPRICE_REMARK());
        if (doubleRate == 0.0) {
            holder.rate.setText("---");
        } else {
            if (oldRate > doubleRate) {
                double rate = oldRate - doubleRate;
                Log.e("GasPrice", "跌:" + rate);
                holder.rate.setText("跌" + rate);
                holder.rate.setTextColor(Color.GREEN);

            } else {
                double rate = doubleRate - oldRate;
                Log.e("GasPrice", "漲:" + rate);
                holder.rate.setText("漲" + rate);
                holder.rate.setTextColor(Color.RED);

            }
        }
        return convertView;
    }
    // UI 標籤結構
    static class ViewHolder {
        TextView date;
        TextView price;
        TextView rate;
    }
}
