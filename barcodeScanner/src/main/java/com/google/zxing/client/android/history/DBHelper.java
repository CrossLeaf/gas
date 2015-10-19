package com.google.zxing.client.android.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LISE on 2015/6/2.
 */
public class DBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME ="barcode_scanner_history.db";//= "gas.db";
    public static final String TABLE_NAME = "barrel";
    public static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase database;



    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT"+", "+
                "barcode TEXT NO NULL"+", " +
                "location TEXT NO NULL"+", " +
                "capacity TEXT NO NULL"+
                ");";
        db.execSQL(SQL);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL);
    }
}
