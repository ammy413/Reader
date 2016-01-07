package com.jksoft.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jackey on 2015/6/14.
 */
public class EBookDBOper {
    // 查询书签
    public static int readBookMarksPageNo(DataBaseHelper dbHelper, String bookname, float fontsize) {
        int pageNo;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cur = db.query("bookmarks",
                new String[]{"pageNo"},
                "bookname = ? and fontsize = ? ",
                new String[]{bookname, fontsize + ""},
                "", "", "");

        if (cur.moveToNext()) {
            pageNo = cur.getInt(cur.getColumnIndex("pageNo"));
        } else {
            pageNo = 1;
        }
        return pageNo;
    }

    // 读文件名，上次在读的文件
    public static HashMap<String, String> readBookMarksBookname(DataBaseHelper dbHelper) {
        HashMap<String, String> map = new HashMap<String, String>();
        String bookname, filename;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cur =

        db.rawQuery(" select bookname, filename from bookname ", new String[]{});
        if (cur.moveToNext()) {
            bookname = cur.getString(cur.getColumnIndex("bookname"));
            filename = cur.getString(cur.getColumnIndex("filename"));
        } else {
            bookname = "";
            filename = "";
        }
        map.put("bookname", bookname);
        map.put("filename", filename);
        return map;
    }

    public static int readBookMarksTotalPages(DataBaseHelper dbHelper, String bookname, float fontsize) {
        int totalPages;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cur = db.query("bookmarks",
                new String[]{"totalPages"},
                "bookname = ? and fontsize = ? ",
                new String[]{bookname, fontsize + ""},
                "", "", "");

        if (cur.moveToNext()) {
            totalPages = cur.getInt(cur.getColumnIndex("totalPages"));
        } else {
            totalPages = 1;
        }
        return totalPages;
    }

    // 保存书签
    public static int saveBookMarksPageNo(DataBaseHelper dbHelper, String bookname, float fontsize, int pageNo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bookname", bookname);
        values.put("fontsize", fontsize);
        values.put("pageNo", pageNo);

        long row = db.update("bookmarks", values, "bookname = ? and fontsize = ? ", new String[]{bookname, "" + fontsize});
        if (row == 0) {
            row = db.insert("bookmarks", "bookname=? and fontsize=?", values);
        }
        return (int) row;
    }

    // 保存当前书名，下次直接进入
    public static int saveBookMarksBookName(DataBaseHelper dbHelper, String bookname, String filename) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bookname", bookname);
        values.put("filename", filename);

        long row = db.update("bookname", values, "", new String[]{});
        if (row == 0) {
            row = db.insert("bookname", "", values);
        }
        return (int) row;
    }

    // 保存书签
    public static int saveBookMarkTotalPages(DataBaseHelper dbHelper, String bookname, float fontsize, int totalPages) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bookname", bookname);
        values.put("fontsize", fontsize);
        values.put("totalPages", totalPages);

        long row = db.update("bookmarks", values, "bookname = ? and fontsize = ? ", new String[]{bookname, "" + fontsize});
        if (row == 0) {
            row = db.insert("bookmarks", "bookname=? and fontsize=?", values);
        }
        return (int) row;
    }

    // 读取文件配置信息
    public static HashMap<Integer, Integer> readBookConfig(DataBaseHelper dbHelper, String bookname, float fontsize) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        HashMap<Integer, Integer> marks = new HashMap<Integer, Integer>();

        Cursor cur = db.query("bookconfig", new String[]{"pageNo", "startNo"}, "bookname=? and fontsize=?", new String[]{bookname, "" + fontsize}, "", "", "");
        while (cur.moveToNext()) {
            int pageNo = cur.getInt(cur.getColumnIndex("pageNo"));
            int startNo = cur.getInt(cur.getColumnIndex("startNo"));
            marks.put(pageNo, startNo);
        }
        return marks;
    }

    // 保存书籍信息
    public static int saveBookConfig(DataBaseHelper dbHelper, String bookname, float fontsize, HashMap<Integer, Integer> marks) {

        if (marks == null || marks.isEmpty()) {
            return -1;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("bookname", bookname);
        values.put("fontsize", fontsize);
        //循环处理
        Iterator<Integer> iter = marks.keySet().iterator();
        while (iter.hasNext()) {
            int key = iter.next();
            int value = marks.get(key);
            values.put("pageNo", key);
            values.put("startNo", value);
            db.insert("bookconfig", "bookname=? and fontsize=?", values);
        }
        return 0;
    }
}
