package com.chenghsi.lise.gas.db;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * This class open the connection to synchronize between app and remote database.
 */

public class GasDB
{
    public static final String DB_URL = "http://198.245.55.221:8089/ProjectGAPP/php/show.php";
    public static final String GET = "?tbname=";
    public static final String ORDER = "order";
    public static final String DELIVERY = "delivery";
    public static final String CUSTOMER = "customer";
    public static final String DODDLE = "doddle";
    public static final String PRICE = "price";
    public static final String BALANCING = "order";
    public static final String STAFF = "staff";
    public static final String BARREL = "barrel";

    private AsyncTaskFinishListener asyncTaskFinishListenerFromUser = null;

    private static JSONObject table_inJSONObject_delivery = null;
    private static JSONObject table_inJSONObject_order = null;
    private static JSONObject table_inJSONObject_customer = null;
    private static JSONObject table_inJSONObject_doddle = null;
    private static JSONObject table_inJSONObject_price = null;
    private static JSONObject table_inJSONObject_staff = null;
    private static JSONObject table_inJSONObject_barrel = null;

    private static JSONArray table_inJSONArray_delivery = null;
    private static JSONArray table_inJSONArray_order = null;
    private static JSONArray table_inJSONArray_customer = null;
    private static JSONArray table_inJSONArray_doddle = null;
    private static JSONArray table_inJSONArray_price = null;
    private static JSONArray table_inJSONArray_staff = null;
    private static JSONArray table_inJSONArray_barrel = null;

    public interface AsyncTaskFinishListener
    {
        void onAsyncTaskFinish();
    }

    public void setTaskListener(AsyncTaskFinishListener asyncTaskFinishListenerFromUser)
    {
        this.asyncTaskFinishListenerFromUser = asyncTaskFinishListenerFromUser;
    }

    // Capture whole table
    public JSONArray getTable(String tableName)
    {
        try
        {
            switch (tableName)
            {
                case ORDER:
                    return table_inJSONArray_order;

                case DELIVERY:
                    return table_inJSONArray_delivery;

                case CUSTOMER:
                    return table_inJSONArray_customer;

                case DODDLE:
                    return table_inJSONArray_doddle;

                case PRICE:
                    return table_inJSONArray_price;

                case BARREL:
                    return table_inJSONArray_barrel;

                default:
                    Log.e("GasDB.get", "Unknown table name: " + tableName);
                    break;
            }
        }
        catch (Exception e){Log.e("GasDB.getTable",e.toString());}
        return null;
    }

    // Capture table item by primary key (ID).
    public JSONArray getTableItemById(String tableName, String id)
    {
        try
        {
            switch (tableName)
            {
                case ORDER:
                    return table_inJSONObject_order.getJSONArray(id);

                case DELIVERY:
                    return table_inJSONObject_delivery.getJSONArray(id);

                case CUSTOMER:
                    return table_inJSONObject_customer.getJSONArray(id);

                case DODDLE:
                    return table_inJSONObject_doddle.getJSONArray(id);

                case PRICE:
                    return table_inJSONObject_price.getJSONArray(id);

                case BARREL:
                    return table_inJSONObject_barrel.getJSONArray(id);

                default:
                    Log.e("GasDB.get", "Unknown table name: " + tableName);
                    break;
            }
        }
        catch (Exception e){Log.e("GasDB.getTableItemById",e.toString());}
        return null;
    }

    // Capture table item by index.
    public JSONArray getTableItemByIndex(String tableName, int id)
    {
        try
        {
            switch (tableName)
            {
                case ORDER:
                    return table_inJSONArray_order.getJSONArray(id);

                case DELIVERY:
                    return table_inJSONArray_delivery.getJSONArray(id);

                case CUSTOMER:
                    return table_inJSONArray_customer.getJSONArray(id);

                case DODDLE:
                    return table_inJSONArray_doddle.getJSONArray(id);

                case PRICE:
                    return table_inJSONArray_price.getJSONArray(id);

                case STAFF:
                    return table_inJSONArray_staff.getJSONArray(id);

                case BARREL:
                    return table_inJSONArray_barrel.getJSONArray(id);

                default:
                    Log.e("GasDB.get", "Unknown table name: " + tableName);
                    break;
            }
        }
        catch (Exception e){Log.e("GasDB.getTableItemById",e.toString());}
        return null;
    }

    // Renew local database from user's requirement.
    // Different requirement will start single/multiple asyncTask.
    public void startAsyncTask(String kind)
    {
        String[] tableName;
        String[] url;

        switch (kind)
        {
            case "Task":
                tableName = new String[2];
                url = new String[2];
                tableName[0] = ORDER;
                tableName[1] = CUSTOMER;
                url[0] = DB_URL + GET + ORDER;
                url[1] = DB_URL + GET + CUSTOMER;

                new asyncTask().execute(tableName, url);
                break;

            case "Delivery":
                tableName = new String[3];
                url = new String[3];
                tableName[0] = DELIVERY;
                tableName[1] = ORDER;
                tableName[2] = CUSTOMER;
                url[0] = DB_URL + GET + DELIVERY;
                url[1] = DB_URL + GET + ORDER;
                url[2] = DB_URL + GET + CUSTOMER;

                new asyncTask().execute(tableName, url);
                break;

            case "Balancing":
                tableName = new String[3];
                url = new String[3];
                tableName[0] = "delivery";
                tableName[1] = "order";
                tableName[2] = "customer";
                url[0] = DB_URL + GET + DELIVERY;
                url[1] = DB_URL + GET + ORDER;
                url[2] = DB_URL + GET + CUSTOMER;

                new asyncTask().execute(tableName, url);
                break;

            case "ClientInfo":
                tableName = new String[1];
                url = new String[1];
                tableName[0] = CUSTOMER;
                url[0] = DB_URL + GET + CUSTOMER;

                new asyncTask().execute(tableName, url);
                break;

            case "GasPricing":
                tableName = new String[1];
                url = new String[1];
                tableName[0] = PRICE;
                url[0] = DB_URL + GET + PRICE;

                new asyncTask().execute(tableName, url);
                break;

            case "MeterReading":
                tableName = new String[2];
                url = new String[2];
                tableName[0] = DODDLE;
                tableName[1] = CUSTOMER;
                url[0] = DB_URL + GET + DODDLE;
                url[1] = DB_URL + GET + CUSTOMER;

                new asyncTask().execute(tableName, url);
                break;

            //http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=staff
            case "Staff":
                tableName = new String[1];
                url = new String[1];
                tableName[0] = STAFF;
                url[0] = DB_URL + GET + STAFF;
                Log.e("tag", "執行staffDB");
                new asyncTask().execute(tableName, url);
                break;

            default:
                Log.e("GasDB.startAsyncTask","Unknown user task: " + kind);
                break;
        }
    }

    // Renew locale database from remote
    private class asyncTask extends AsyncTask<String[], Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String[]... params)
        {
            try
            {
                for(int j=0; j<params[0].length; j++)
                {
                    String tableName = params[0][j];
                    URL url = new URL(params[1][j]);
                    HttpURLConnection connection;

                    // Connection
                    Log.d("GasDB.asyncTask", "Start connection: " + url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    Log.d("GasDB.asyncTask", "Connection successfully.");


                    // Read Data
                    Log.d("GasDB.asyncTask", "Start to get data.");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String jsonString = reader.readLine();
                    reader.close();
                    Log.d("GasDB.asyncTask", "Data reading successfully");


                    // JSONArray to JSONObject
                    JSONArray table_ArrayForm = new JSONArray(jsonString);
                    HashMap<String,JSONArray> map = new HashMap<>();
                    for(int i=0; i<table_ArrayForm.length(); i++)
                    {
                        JSONArray content = table_ArrayForm.getJSONArray(i);
                        String id = content.getString(0);
                        map.put(id, content);
                    }
                    JSONObject table_mapForm = new JSONObject(map);


                    storeTable(tableName, table_ArrayForm, table_mapForm);
                }
                return true;
            }
            catch(Exception e) {Log.e("GasDB.asyncTask", e.toString());}
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status)
        {
            Log.d("GasDB.asyncTask","Start onAsyncTaskFinish()");
            if(asyncTaskFinishListenerFromUser != null && status)
            {
                asyncTaskFinishListenerFromUser.onAsyncTaskFinish();
            }
            else
            {
                Log.e("GasDB.asyncTask","asyncTask fails!");
            }
        }

        private void storeTable(String tableName, JSONArray content1, JSONObject content2)
        {
            switch (tableName)
            {
                case DELIVERY:
                    table_inJSONArray_delivery = content1;
                    table_inJSONObject_delivery = content2;
                    break;

                case ORDER:
                    table_inJSONArray_order = content1;
                    table_inJSONObject_order = content2;
                    break;

                case CUSTOMER:
                    table_inJSONArray_customer = content1;
                    table_inJSONObject_customer = content2;
                    break;

                case
                        DODDLE:
                    table_inJSONArray_doddle = content1;
                    table_inJSONObject_doddle = content2;
                    break;

                case PRICE:
                    table_inJSONArray_price = content1;
                    table_inJSONObject_price = content2;
                    break;
                case STAFF:
                    table_inJSONArray_staff = content1;
                    table_inJSONObject_staff = content2;
                default:
                    Log.e("GasDB.renewTable", "Unknown table name: " + tableName);
                    break;
            }
        }
    }
}
