package com.google.zxing.client.android.history;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by LISE on 2015/6/2.
 */
public class HistoryManager
{
    private DBHelper barrelDBHelper;
    private SQLiteDatabase barrelDB;



    public HistoryManager(Activity activity)
    {
        barrelDBHelper = new DBHelper(activity);
        barrelDB = barrelDBHelper.getWritableDatabase();
    }



    public void create(HistoryItem item)
    {
        barrelDB.insert(DBHelper.TABLE_NAME, null, item.returnAsCV());
    }



    public Cursor read()
    {
        String[] colNames = new String[]{"id","barcode","location","capacity"};
        return barrelDB.query(DBHelper.TABLE_NAME, colNames, null, null, null, null, null);
    }



    public void update(HistoryItem item)
    {
        String searchKey = item.barcode;
        barrelDB.update(DBHelper.TABLE_NAME, item.returnAsCV(), "barcode='"+searchKey+"'", null);
    }



    public void delete()
    {
        barrelDB.delete(DBHelper.TABLE_NAME, "barcode='abcdefg'", null);
    }



    public void clearAllHistory()
    {
        barrelDB.delete(DBHelper.TABLE_NAME, null, null);
    }



    public void addItem(HistoryItem item)
    {
        if( !isBarcodeExist(item.barcode) ) this.create(item);
        else this.update(item);
    }



    public boolean hasHistoryItems()
    {
        Cursor cursor = this.read();
        if(cursor==null) return false;
        return true;
    }



    public Iterator<HistoryItem> getAllItem()
    {
        ArrayList<HistoryItem> allItem = new ArrayList<>();
        HistoryItem item;
        Cursor cursor = this.read();

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0; i<cursor.getCount(); i++)
            {
                item = new HistoryItem( cursor.getString(1) );
                item.location = cursor.getString(2);
                item.capacity = cursor.getString(3);
                allItem.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return allItem.iterator();
    }



    public boolean isBarcodeExist(String barcode)
    {
        boolean isRepeat=false;
        Cursor cursor = this.read();
        if(cursor==null) return false;

        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++)
        {
            if( cursor.getString(1).equals(barcode) ) isRepeat = true;
            cursor.moveToNext();
        }
        cursor.close();

        return isRepeat;
    }
}
