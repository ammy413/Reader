package com.jksoft.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/6/2.
 */
public class DataBaseHelper extends SQLiteOpenHelper {


    final static int VERSION = 1;

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    public DataBaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表结构
        System.out.println(" create dababase ing ");
        db.execSQL(" create table bookmarks " +
                "(" +
                "bookname varchar(40), " +
                "fontsize int ," +
                "pageNo int," +
                "totalPages)");
        db.execSQL(" create table bookconfig (" +
                "bookname varchar(40), " +
                "fontsize int," +
                "pageNo int," +
                "startNo int) ");
        // 记录当前正在读哪本书
        db.execSQL(" create table bookname ( " +
                " bookname varchar2(40), filename varchar2(40) )  ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(" drop table version  ");
//        db.execSQL(" drop table user     ");
    }
}