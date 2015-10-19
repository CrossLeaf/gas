package com.google.zxing.client.android.history;

import android.content.ContentValues;

/**
 * Created by LISE on 2015/6/2.
 */
public class HistoryItem
{
    public String barcode;
    public String location;
    public String capacity;



    public HistoryItem(String barcode)
    {
        this.barcode = barcode;
        location = null;
        capacity = null;
    }



    public ContentValues returnAsCV()
    {
        ContentValues cv = new ContentValues();
        cv.put("barcode",barcode);
        cv.put("location",location);
        cv.put("capacity",capacity);
        return cv;
    }
}
