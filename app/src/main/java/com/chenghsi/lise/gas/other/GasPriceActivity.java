package com.chenghsi.lise.gas.other;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chenghsi.lise.gas.AbstractList;
import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.GasPriceAdapter;
import com.chenghsi.lise.gas.GasPriceList;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.db.GasDB;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class GasPriceActivity extends AbstractList {
//    private ArrayList<GasPriceList> list;
    private GasPriceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.title_activity_gas_price);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        new AsynGasPriceDownLoad().execute();
        Log.e("tag", "--onCreate--");

        //去找一下listView
        listView = (ListView) findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);

    }
    /*public ArrayList<GasPriceList> gasLists(){
        AsynGasPriceDownLoad asynDownLoad = new AsynGasPriceDownLoad();
        list = new ArrayList<>();
        list = asynDownLoad.getList();
        return list;

    }*/

    @Override
    protected void onResume() {
        super.onResume();
//        gasDB.startAsyncTask("GasPricing");
    }

    private GasDB.AsyncTaskFinishListener asyncTaskFinishListener = new GasDB.AsyncTaskFinishListener() {
        @Override
        public void onAsyncTaskFinish() {
            try {
                PriceGasListAdapter apt = new PriceGasListAdapter(GasPriceActivity.this);
                listView.setAdapter(apt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class PriceGasListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        String oldItem = "0";

        public PriceGasListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return gasDB.getTable(GasDB.PRICE).length();
        }

        @Override
        public Object getItem(int position) {
            try {
                JSONArray item_price = gasDB.getTableItemByIndex(GasDB.PRICE, position);

                // The adapted data
                ArrayList<String> result = new ArrayList<>();
                result.add(item_price.getString(Constant.PRICE_BULID_DATE));    //item[0]
                result.add(item_price.getString(Constant.PRICE_REMARK));        //item[1]
                result.add(item_price.getString(Constant.PRICE_16));            //item[2]
                result.add(item_price.getString(Constant.PRICE_20));            //item[3]
                Log.e("gasPrice", String.valueOf(item_price));
                //將ArrayList轉乘Array 回傳
                return result.toArray(new String[result.size()]);

            } catch (JSONException e) {
                Log.e("SimpleTaskListAdapter", "----"+e.toString()+"----");
            }
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.adapter_item_gas_price, parent, false);
            Log.e("GasPrice", oldItem);
            try {
                TextView tv_rate;
                String[] item = (String[]) getItem(position);
                ((TextView) view.findViewById(R.id.tv_date)).setText(item[0]);
                ((TextView) view.findViewById(R.id.tv_price)).setText(item[2] + "/" + item[3]);
                tv_rate = (TextView) findViewById(R.id.tv_rate);
                double doubleOldItem = Double.parseDouble(oldItem);
                double doubleItem = Double.parseDouble(item[1]);
                Log.e("GasPrice", "doubleItem:"+doubleItem+","+"doubleOldItem:"+doubleOldItem);
                if (doubleItem == 0.0) {
                    tv_rate.setText("---");
                } else {
                    if (doubleOldItem > doubleItem) {
                        double rate = doubleOldItem - doubleItem;
                        Log.e("GasPrice", "跌:" + rate);
                        tv_rate.setText("跌" + rate);
                        tv_rate.setTextColor(getResources().getColor(R.color.SpringGreen));

                    } else {
                        double rate = doubleItem - doubleOldItem;
                        Log.e("GasPrice", "漲:" + rate);
                        tv_rate.setText("漲" + rate);
                        tv_rate.setTextColor(getResources().getColor(R.color.Red));

                    }
                }
                Log.e("GasPrice", "date:" + item[0] + "rate:" + item[1]);
                oldItem = item[1];
                Log.e("GasPrice", "oldItem" + oldItem);
            } catch (Exception e) {
                Log.e("SimpleTaskListAdapter", e.toString());
                ((TextView) view.findViewById(R.id.tv_date)).setText("Error!");
            }

            return view;
        }
    }

    public class AsynGasPriceDownLoad extends AsyncTask<String, ArrayList<GasPriceList>, ArrayList<GasPriceList>> {

        private ArrayList<GasPriceList> gasPriceLists;
        private String price_20;
        private String price_16;
        private String price_build_date;
        private String price_remark;
        private String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=price";
        protected ListView listView;

        @Override
        protected ArrayList<GasPriceList> doInBackground(String... urls) {
            try {
                gasPriceLists = new ArrayList<>();

                JSONArray jsonArray = new JSONArray(getJSONData(url));
//            HashMap<String,JSONArray> map = new HashMap<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray content = jsonArray.getJSONArray(i);  //取得陣列中的每個陣列
                    price_20 = content.getString(2);
                    price_16 = content.getString(3);
                    price_build_date = content.getString(5);
                    price_remark = content.getString(8);
                    GasPriceList info = new GasPriceList(price_20, price_16, price_build_date, price_remark);
                    gasPriceLists.add(info);
                    Log.e("async", price_build_date);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GasPriceList> gasPriceLists) {
            super.onPostExecute(gasPriceLists);
            adapter = new GasPriceAdapter(GasPriceActivity.this, gasPriceLists);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(ArrayList<GasPriceList>... values) {
            super.onProgressUpdate(values);
        }

        //取得JSON資料
        private String getJSONData(String url) {
            String retSrc = "";
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            Log.e("retSrc", "讀取 JSON-1...");
            try {
                HttpResponse response = httpclient.execute(httpget);
                Log.e("retSrc", "讀取 JSON-2...");
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "讀取 JSON-3...");
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    retSrc = "Did not work!";
                }

            } catch (Exception e) {
                Log.e("retSrc", "讀取JSON Error...");
                return null;
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
            return retSrc;
        }

        public ArrayList<GasPriceList> getList() {
            return gasPriceLists;
        }

    }
}
