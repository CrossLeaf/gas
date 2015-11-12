package com.chenghsi.lise.gas.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.BalanceList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.db.GasDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ClientInfoActivity extends AbstractList
{
    EditText edt_search;
    SimpleTaskListAdapter apt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_client_info);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        gasDB.setTaskListener(asyncTaskFinishListener);
        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setVisibility(View.VISIBLE);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.e("tag", "onTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.e("tag", "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                ClientInfoActivity.this.apt.getFilter().filter(arg0);
                Log.e("tag", "afterTextChanged");
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gasDB.startAsyncTask("ClientInfo");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener()
    {
        @Override
        public void onAsyncTaskFinish()
        {
            try
            {
                apt = new SimpleTaskListAdapter(ClientInfoActivity.this);
                listView.setAdapter(apt);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent intent = new Intent();
                        intent.setClass(ClientInfoActivity.this, DetailedClientInfoActivity.class);
                        startActivity(intent);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public class SimpleTaskListAdapter extends BaseAdapter implements Filterable
    {
        private MyFilter filter;
        private LayoutInflater inflater;
        private List<String> mOriginalValues;
        private ArrayList<String> result;
        public SimpleTaskListAdapter(Context context)
        {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return gasDB.getTable(GasDB.CUSTOMER).length();
        }

        @Override
        public Object getItem(int position)
        {
            try
            {
                JSONArray item_customer = gasDB.getTableItemByIndex(GasDB.CUSTOMER, position);

                result = new ArrayList<>();
                result.add( item_customer.getString(Constant.CUSTOMER_NAME) );
                result.add( item_customer.getString(Constant.CUSTOMER_CONTACT_ADDRESS) );
                result.add( "phone" ); // TODO

                return result.toArray(new String[result.size()]);
            }
            catch(JSONException e)
            {
                Log.e("SimpleTaskListAdapter", e.toString());
            }
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = inflater.inflate(R.layout.adapter_item_client_info, parent, false);

            try
            {
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_name)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_address)).setText(_toAddress(item[1]));
                //((TextView) view.findViewById(R.id.tv_clientName)).setText(item[2]);
            }
            catch (Exception e)
            {
                Log.e("SimpleTaskListAdapter",e.toString());
                ((TextView) view.findViewById(R.id.tv_appointment)).setText("Error!");
            }

            return view;
        }
        public String _toAddress(String address) {
            try {
                String[] addr_name = new String[]{"", "", "巷", "弄", "號", "樓", "室"};
                String temp = "";
                String[] address_arr = address.split("_");
                Log.e("notifyData", "addLength : " + address_arr.length);
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
                FilterResults filterResult = new FilterResults();
                Log.e("tag", "performFiltering");
                Log.e("tag", "constraint:"+constraint);
                if (mOriginalValues == null) {
                    synchronized (this) {
                        mOriginalValues = new ArrayList<> (result);
                    }
                }

                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<String> filteredItems = new ArrayList<>();
                    for (int i = 0, l = mOriginalValues.size(); i < l; i++) {
                        String m = mOriginalValues.get(i);
                        Log.e("tag", "i:"+i);
                        Log.e("tag", "m = "+mOriginalValues.get(i));

                        if (m.contains(constraint)) {
                            filteredItems.add(m);
                        }
                    }
                    filterResult.count = filteredItems.size();
                    filterResult.values = filteredItems;
                } else {
                    synchronized (this) {
                        ArrayList<String> list = new ArrayList<String>(mOriginalValues);
                        filterResult.values = list;
                        filterResult.count = list.size();
                    }
                }

                return filterResult;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                result = (ArrayList<String>) results.values;
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
}
