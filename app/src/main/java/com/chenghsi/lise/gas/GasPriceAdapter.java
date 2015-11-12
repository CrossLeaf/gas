package com.chenghsi.lise.gas;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chenghsi.lise.gas.other.NewGasPriceActivity;

import java.util.ArrayList;

/**
 * Created by MengHan on 2015/10/28.
 */
public class GasPriceAdapter extends BaseAdapter {
    // 定義 LayoutInflater
    private LayoutInflater myInflater;
    // 定義 Adapter 內藴藏的資料容器
    private ArrayList<GasPriceList> list;

    //建構子
    public GasPriceAdapter(Context context, ArrayList<GasPriceList> list) {
        //預先取得 LayoutInflater 物件實體
        myInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GasPriceList getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            // 1:將 R.layout.cus_view 實例化
            convertView = myInflater.inflate(R.layout.adapter_item_gas_price, null);

            // 2:建立 UI 標籤結構並存放到 holder
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.rate = (TextView) convertView.findViewById(R.id.tv_rate);

            // 3:注入 UI 標籤結構 --> convertView
            convertView.setTag(holder);

        } else {
            // 取得  UI 標籤結構
            holder = (ViewHolder) convertView.getTag();
        }

        // 4:取得gasList物件資料
        GasPriceList gasList = list.get(position);

        // 5:設定顯示資料
        Log.e("gasprice", "顯示資料");
        holder.date.setText(gasList.getPriceBulidDate());
        holder.price.setText(gasList.getPrice20() + "/" + gasList.getPrice16());
        holder.rate.setText(gasList.getPriceRemark());
        holder.rate.setTextColor(gasList.getRateColor());
        return convertView;
    }

    // UI 標籤結構
    static class ViewHolder {
        TextView date;
        TextView price;
        TextView rate;
    }
}
