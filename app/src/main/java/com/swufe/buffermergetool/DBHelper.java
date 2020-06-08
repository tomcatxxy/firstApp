package com.swufe.buffermergetool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION=4;
    private static final String DB_NAME="myta.db";
    public static final String TB_NAME1="tb_recruit";
    public static final String TB_NAME2="tb_needs";
    public static final String TB_NAME3="tb_internship";
    public static final String TB_NAME4="tb_notices_swufe";
    public static final String TB_NAME5="tb_notices_it";
    public static final String TB_NAME6="tb_lecture";
    public static final String TB_NAME7="tb_front";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public DBHelper(Context context){ super(context,DB_NAME,null,VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TB_NAME1+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME2+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME3+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME4+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME5+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME6+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
        db.execSQL("CREATE TABLE "+TB_NAME7+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURDATA TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
