package com.chenghsi.lise.gas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MengHan on 2015/11/12.
 */
public class DetailClientInfoAdapter extends BaseAdapter {

    private LayoutInflater myInflater;
    private List<DetailClientInfoList> detailClientInfoLists;
    String[] cylinders_list = {};
    String[] order_day_list = {};
    TextView tv_do_de;

    public DetailClientInfoAdapter(Context context, List<DetailClientInfoList> detailClientInfoLists) {
        myInflater = LayoutInflater.from(context);
        this.detailClientInfoLists = detailClientInfoLists;
    }

    @Override
    public int getCount() {
        return detailClientInfoLists.size();
    }

    @Override
    public Object getItem(int i) {
        return detailClientInfoLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return detailClientInfoLists.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Log.e("client", "getView");

        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.adapter_item_detail_client, parent, false);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.tv_order_day),
                    (TextView) convertView.findViewById(R.id.tv_order_task),
                    (TextView) convertView.findViewById(R.id.tv_50),
                    (TextView) convertView.findViewById(R.id.tv_20),
                    (TextView) convertView.findViewById(R.id.tv_16),
                    (TextView) convertView.findViewById(R.id.tv_4),
                    (TextView) convertView.findViewById(R.id.tv_doddle_this_phase_degree)
            );
            tv_do_de = (TextView) convertView.findViewById(R.id.tv_do_de);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DetailClientInfoList clientList = (DetailClientInfoList) getItem(position);
        order_day_list = clientList.getOrder_time().split("-", 2);
        holder.tv_order_day.setText(order_day_list[0] + "\n" + order_day_list[1]);
        holder.tv_order_task.setText(clientList.getOrder_task());
        cylinders_list = clientList.getOrder_cylinders_list().split(",");
        holder.tv_50.setText(cylinders_list[0]);
        holder.tv_20.setText(cylinders_list[1]);
        holder.tv_16.setText(cylinders_list[2]);
        holder.tv_4.setText(cylinders_list[3]);
        holder.tv_doddle_this_phase_degree.setText(clientList.getDoddle_this_phase_degree());
        tv_do_de.setText("度數");
        return convertView;
    }

    private class ViewHolder {

        TextView tv_order_day; //含抄錶時間
        TextView tv_order_task;
        TextView tv_50;
        TextView tv_20;
        TextView tv_16;
        TextView tv_4;
        TextView tv_doddle_this_phase_degree;

        public ViewHolder(TextView tv_order_day, TextView tv_order_task, TextView tv_50,
                          TextView tv_20, TextView tv_16, TextView tv_4, TextView tv_doddle_this_phase_degree) {
            this.tv_order_day = tv_order_day;
            this.tv_order_task = tv_order_task;
            this.tv_50 = tv_50;
            this.tv_20 = tv_20;
            this.tv_16 = tv_16;
            this.tv_4 = tv_4;
            this.tv_doddle_this_phase_degree = tv_doddle_this_phase_degree;
        }
    }
}
