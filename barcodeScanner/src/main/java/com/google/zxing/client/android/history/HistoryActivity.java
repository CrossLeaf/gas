package com.google.zxing.client.android.history;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.google.zxing.client.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by LISE on 2015/6/3.
 */
public class HistoryActivity extends ListActivity
{
    private HistoryManager historyManager;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onResume()
    {
        super.onResume();
        historyManager = new HistoryManager(this);
        reloadHistoryItem();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (historyManager.hasHistoryItems())
        {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.history, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }



    private void reloadHistoryItem()
    {
        /*ArrayList<String> values = new ArrayList<>();
        Cursor cursor = historyManager.read();

        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++)
        {
            values.add(cursor.getString(1));
            cursor.getString(2);
            cursor.moveToNext();
        }
        cursor.close();

        ArrayAdapter<String> apt = new ArrayAdapter<>(this,android.R.layout.simple_list_item_2,values);
        setListAdapter(apt);*/

        ArrayList<HashMap<String,String>> values = new ArrayList<>();
        Cursor cursor = historyManager.read();

        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++)
        {
            HashMap<String,String> item = new HashMap<>();

            item.put("barcode",cursor.getString(1));
            item.put("location", cursor.getString(2));
            values.add(item);

            cursor.getString(2);
            cursor.moveToNext();
        }
        cursor.close();

        SimpleAdapter apt = new SimpleAdapter(this,values,android.R.layout.simple_list_item_2,
                new String[]{"barcode","location"},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(apt);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int i = item.getItemId();
        if (i == R.id.menu_history_clear_text)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.msg_sure);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int i2)
                {
                    historyManager.clearAllHistory();
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton(R.string.button_cancel, null);
            builder.show();

        } else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
