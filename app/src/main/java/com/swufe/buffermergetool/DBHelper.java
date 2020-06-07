package com.swufe.buffermergetool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION=3;
    private static final String DB_NAME="mydata.db";
    public static final String TB_NAME1="tb_videos";
    public static final String TB_NAME2="tb_notices";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public DBHelper(Context context){ super(context,DB_NAME,null,VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TB_NAME1+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME2+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
