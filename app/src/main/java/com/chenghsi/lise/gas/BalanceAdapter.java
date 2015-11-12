package com.chenghsi.lise.gas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.chenghsi.lise.gas.other.NewBalancingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MengHan on 2015/11/9.
 */
public class BalanceAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater myInflater;
    private List<BalanceList> balance;
    private List<BalanceList> mOriginalValues;
    private MyFilter filter;
//    private List<Boolean> check_list;
    int count = 0;

    public BalanceAdapter(Context context, List<BalanceList> balance) {
        myInflater = LayoutInflater.from(context);
        this.balance = balance;
    }

    @Override
    public int getCount() {
        return balance.size();
    }

    @Override
    public Object getItem(int i) {
        return balance.get(i);
    }

    @Override
    public long getItemId(int i) {
        return balance.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.adapter_item_balance, parent, false);
            holder = new ViewHolder(
                    (CheckedTextView) convertView.findViewById(R.id.checkText_name),
                    (TextView) convertView.findViewById(R.id.tv_cylinder1),
                    (TextView) convertView.findViewById(R.id.tv_cylinder2),
                    (TextView) convertView.findViewById(R.id.tv_cylinder3),
                    (TextView) convertView.findViewById(R.id.tv_cylinder4),
                    (TextView) convertView.findViewById(R.id.date),
                    (TextView) convertView.findViewById(R.id.order_date),
                    (TextView) convertView.findViewById(R.id.money),
                    (TextView) convertView.findViewById(R.id.should_money)
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BalanceList balance = (BalanceList) getItem(position);

        String cylinders = balance.getCylinders_list();
        String[] cylinder_list = cylinders.split(",");
        String order_id = balance.getId();
        holder.checkText_name.setText(balance.getName());
        holder.tv_cylinder1.setText("50KG:" + cylinder_list[0]);
        holder.tv_cylinder2.setText("20KG:" + cylinder_list[1]);
        holder.tv_cylinder3.setText("16KG:" + cylinder_list[2]);
        holder.tv_cylinder4.setText("4KG:" + cylinder_list[3]);

        holder.order_date.setText(balance.getOrder_date());
        holder.should_money.setText(balance.getMoney());
        count++;
        Log.e("balance", "balance view:" + position + "\n count:" + count);

        holder.checkText_name.setChecked(NewBalancingActivity.isCheckedMap.get(order_id));

        return convertView;
    }

    private class ViewHolder {
        CheckedTextView checkText_name;
        TextView tv_cylinder1;
        TextView tv_cylinder2;
        TextView tv_cylinder3;
        TextView tv_cylinder4;
        TextView date;
        TextView order_date;
        TextView money;
        TextView should_money;

        public ViewHolder(CheckedTextView checkText_name, TextView tv_cylinder1, TextView tv_cylinder2,
                          TextView tv_cylinder3, TextView tv_cylinder4, TextView date,
                          TextView order_date, TextView money, TextView should_money) {
            this.checkText_name = checkText_name;
            this.tv_cylinder1 = tv_cylinder1;
            this.tv_cylinder2 = tv_cylinder2;
            this.tv_cylinder3 = tv_cylinder3;
            this.tv_cylinder4 = tv_cylinder4;
            this.date = date;
            this.order_date = order_date;
            this.money = money;
            this.should_money = should_money;
        }


    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }

    public class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString();
            FilterResults result = new FilterResults();
            Log.e("tag", "performFiltering");
            Log.e("tag", "constraint:"+constraint);
            if (mOriginalValues == null) {
                synchronized (this) {
                    mOriginalValues = new ArrayList<BalanceList>(balance);
                }
            }

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<BalanceList> filteredItems = new ArrayList<BalanceList>();
                for (int i = 0, l = mOriginalValues.size(); i < l; i++) {
                    BalanceList m = mOriginalValues.get(i);
                    Log.e("tag", "i:"+i);
                    Log.e("tag", "m = "+mOriginalValues.get(i));

                    if (m.getName().contains(constraint)) {
                        filteredItems.add(m);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<BalanceList> list = new ArrayList<BalanceList>(mOriginalValues);
                    result.values = list;
                    result.count = list.size();
                }
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            balance = (ArrayList<BalanceList>) results.values;
            Log.e("tag", "publishResults");
            if (results.count > 0) {
                notifyDataSetChanged();
                Log.e("tag", "notifyDataSetChanged");
            } else {
                notifyDataSetInvalidated();
                Log.e("tag", "notifyDataSetInvalidated");
            }
        }
    }
}
