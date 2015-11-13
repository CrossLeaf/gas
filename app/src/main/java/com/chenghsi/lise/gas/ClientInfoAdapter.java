package com.chenghsi.lise.gas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import com.chenghsi.lise.gas.other.NewClientInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MengHan on 2015/11/12.
 */
public class ClientInfoAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater myInflater;
    public static List<ClientInfoList> clientInfoLists;
    private List<ClientInfoList> mOriginalValues;
    private MyFilter filter;
    private Spinner clientPhones;
    private String action = Intent.ACTION_CALL;
    private Context context;

    public ClientInfoAdapter(Context context, List<ClientInfoList> clientInfoLists) {
        myInflater = LayoutInflater.from(context);
        this.clientInfoLists = clientInfoLists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return clientInfoLists.size();
    }

    @Override
    public Object getItem(int i) {
        return clientInfoLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return clientInfoLists.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Log.e("client", "getView");

        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.adapter_item_client_info, parent, false);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.tv_name),
                    (TextView) convertView.findViewById(R.id.tv_address)
            );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClientInfoList clientList = (ClientInfoList) getItem(position);
        String add = _toAddress(clientList.getAddress());
        holder.tv_name.setText(clientList.getName());
        holder.tv_address.setText(add);
        //TODO 電話號碼呈現
        /*String[] phone_num = clientList.getPhone();
        Log.e("client", "phone number:"+phone_num[0]);
        String phones[] = new String[clientList.getPhone().length+1];
        phones[0] = "請選擇號碼";
        for (int i = 0; i < clientList.getPhone().length;i++){
            phones[i+1] = phone_num[i];
        }
        clientPhones = (Spinner) convertView.findViewById(R.id.spi_phone_number);
        ArrayAdapter<String> apt = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, phones);
        apt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientPhones.setAdapter(apt);
        //電話撥號
        clientPhones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clientPhones.setOnItemSelectedListener(spnListener);
                return false;
            }
        });
*/
        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_address;


        public ViewHolder(TextView tv_name, TextView tv_address) {
            this.tv_name = tv_name;
            this.tv_address = tv_address;
        }
    }
    //電話 spinner 點按動作
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
                context.startActivity(intent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Todo
        }
    };

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
            Log.e("tag", "constraint:" + constraint);
            if (mOriginalValues == null) {
                synchronized (this) {
                    mOriginalValues = new ArrayList<>(clientInfoLists);
                }
            }

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ClientInfoList> filteredItems = new ArrayList<>();
                for (int i = 0, l = mOriginalValues.size(); i < l; i++) {
                    ClientInfoList m = mOriginalValues.get(i);
                    Log.e("tag", "i:" + i);
                    Log.e("tag", "m = " + mOriginalValues.get(i));

                    if (m.getName().contains(constraint)) {
                        filteredItems.add(m);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ClientInfoList> list = new ArrayList<>(mOriginalValues);
                    result.values = list;
                    result.count = list.size();
                }
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clientInfoLists = (ArrayList<ClientInfoList>) results.values;
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

    public String _toAddress(String address) {
        try {
            String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
            String temp = "";
            String[] address_arr = address.split("_");
            Log.e("add", "addLength : " + address_arr.length);
            for (int i = 0; i < address_arr.length; i++) {
                if (!address_arr[i].equals("") && address_arr[i] != null) {
                    temp += address_arr[i] + addr_name[i];
                }
            }
            return temp;
        } catch (Exception e) {
            return null;
        }

    }
}
