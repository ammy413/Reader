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


// Ŀ¼��
// ����ָ��Ŀ¼�µ����е������ļ���
// Ҫ��ListView
public class CatalogActivity extends ListActivity {

    ListView catalogLv;

    List<HashMap<String, String>> ebooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        System.out.println("Hello, I'm in!");
        catalogLv = new ListView(this);

        // ���б�
        HashMap<String, String> booknames = new HashMap<String, String>();
        booknames.put("������", "changshengjian.txt");
        booknames.put("����", "biyudao.txt");
        booknames.put("����ǹ", "bawangqiang.txt");
        booknames.put("���", "libiegou.txt");
        booknames.put("��ȸ��", "kongqueling.txt");
        booknames.put("���黷", "duoqinghuan.txt");
        booknames.put("ȭͷ", "quantou.txt");


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

        // ��һ���������ǵ�ǰcontext
        // �ڶ���������list
        // ������������ÿһ��listitem�Ĳ����ļ�
        // ���ĸ�������
        SimpleAdapter listAdapter = new SimpleAdapter(this, ebooks,
                R.layout.ebook_catolog,
                new String[]{"filename", "bookname"},
                new int[]{R.id.filename, R.id.bookname});

        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        System.out.println("position : " + position);
        // ��ȡ����
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
        // �����������裬���´򿪵�Activity����
        // ��ȡ��ǩ
        // ��ȡ�ļ�����
        // ��E-Book Activity��չʾ��Ӧ���ļ�����
        super.onListItemClick(l, v, position, id);
    }


}

