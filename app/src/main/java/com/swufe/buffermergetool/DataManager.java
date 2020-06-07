package com.swufe.buffermergetool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private DBHelper dbHelper;

    public DataManager(Context context) {
        dbHelper=new DBHelper(context);
    }

    public void add(DataItem item,String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("CURNAME",item.getCurName());
        values.put("CURDATA",item.getCurData());
        db.insert(TBNAME,null,values);
        db.close();
    }

    public void addAll(List<DataItem> list,String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        for(DataItem item:list){
            ContentValues values=new ContentValues();
            values.put("CURNAME",item.getCurName());
            values.put("CURDATA",item.getCurData());
            db.insert(TBNAME,null,values);
        }
        db.close();
    }

    public List<DataItem> listAll(String TBNAME){
        List<DataItem> rateList=null;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query(TBNAME,null,null,null,null,null,null);
        if(cursor!=null){
            rateList=new ArrayList<DataItem>();
            while(cursor.moveToNext()){
                DataItem item=new DataItem();
                item.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                item.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
                item.setCurRate(cursor.getString(cursor.getColumnIndex("CURDATA")));

                rateList.add(item);
            }
            cursor.close();
        }
        db.close();
        return rateList;
    }

    public void delete(int id,String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(TBNAME,"ID=?",new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAll(String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }

    public  void update(DataItem item,String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("CURNAME",item.getCurName());
        values.put("CURDATA",item.getCurData());
        db.update(TBNAME,values,"ID=?",new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public DataItem findById(int id,String TBNAME){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query(TBNAME,null,"ID=?",new String[]{String.valueOf(id)},null,null,null);
        DataItem rateItem=null;
        if(cursor!=null&&cursor.moveToFirst()){
            rateItem=new DataItem();
            rateItem.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            rateItem.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
            rateItem.setCurRate(cursor.getString(cursor.getColumnIndex("CURDATA")));
            cursor.close();
        }
        db.close();
        return rateItem;
    }
}
