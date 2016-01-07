package com.jksoft.ebooks;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


// 目录类
// 加载指定目录下的所有电子书文件名
// 要用ListView
public class CatalogActivity extends ListActivity {

    ListView catalogLv;

    List<HashMap<String, String>> ebooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        System.out.println("Hello, I'm in!");
        catalogLv = new ListView(this);

        // 书列表
        HashMap<String, String> booknames = new HashMap<String, String>();
        booknames.put("长生剑", "changshengjian.txt");
        booknames.put("碧玉刀", "biyudao.txt");
        booknames.put("霸王枪", "bawangqiang.txt");
        booknames.put("离别钩", "libiegou.txt");
        booknames.put("孔雀翎", "kongqueling.txt");
        booknames.put("多情环", "duoqinghuan.txt");
        booknames.put("拳头", "quantou.txt");


        ebooks = new ArrayList<HashMap<String, String>>();

        Iterator<String> booknamesIter = booknames.keySet().iterator();
        while (booknamesIter.hasNext()) {
            String key = booknamesIter.next();
            String value = booknames.get(key);

            HashMap<String, String> row = new HashMap<String, String>();
            row.put("filename", key);
            row.put("bookname", value);

            ebooks.add(row);
        }

        // 第一个参数，是当前context
        // 第二个参数，list
        // 第三个参数，每一个listitem的布局文件
        // 第四个参数，
        SimpleAdapter listAdapter = new SimpleAdapter(this, ebooks,
                R.layout.ebook_catolog,
                new String[]{"filename", "bookname"},
                new int[]{R.id.filename, R.id.bookname});

        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        System.out.println("position : " + position);
        // 获取书名
        if (position >= ebooks.size()) {
            return;
        }
        HashMap<String, String> row = ebooks.get(position);
        String filename = row.get("filename");
        String bookname = row.get("bookname");

        System.out.println("position : " + position + "filename : " + filename + ", bookname : " + bookname);


        Intent intent = new Intent();
        intent.setClass(CatalogActivity.this, RBookActivity.class);

        intent.putExtra("filename", filename);
        intent.putExtra("bookname", bookname);

        CatalogActivity.this.startActivity(intent);
        // 下面三个步骤，在新打开的Activity中做
        // 读取书签
        // 读取文件内容
        // 打开E-Book Activity，展示相应的文件内容
        super.onListItemClick(l, v, position, id);
    }


}

