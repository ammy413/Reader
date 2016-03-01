package com.reader;

import java.io.File;
import java.util.ArrayList;

import com.example.reader.R;
import com.reader.ScanSD.ScanSD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.GridLayout.LayoutParams; 

public class MainMenu extends Activity
{
	
	private ListView booklist;
	
	public final static String EXTRA_MESSAGE = "com.reader.MESSAGE";
	
	String mBookname[]=new String[]
			{
					"书名1 \n 阅读至xxx","书名2 阅读至yyy","书名3 \n 阅读至zzz","4\n4","5\n5","6\n6","4\n4","5\n5","6\n6"
					,"书名2 阅读至yyy","书名3 \n 阅读至zzz","4\n4","5\n5","6\n6","4\n4","5\n5","6\n6"
			};
	



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainmenu);
		
		ArrayList<String> a = new ArrayList<String>();
		ScanSD SDcard = new ScanSD();
        //mBookname = SDcard.getFileList();
		a=SDcard.sgetFileList();
		
		booklist = (ListView)findViewById(R.id.booklist);
		//booklist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mBookname));
		booklist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,a));
		booklist.setOnItemClickListener(
				new AdapterView.OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
					{
						System.out.println(arg2);
						setTitle("你点击了第"+arg2+"行");
						jump(arg2);
					}
				}
				);
		
        

		
		
	}

	
	private void jump(int arg2)
	{
		
		Intent mintent = new Intent(this,Reader.class);
		mintent.putExtra(EXTRA_MESSAGE, arg2);
		startActivity(mintent);
	}
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
