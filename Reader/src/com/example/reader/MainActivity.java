package com.example.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.GridLayout.LayoutParams; 

public class MainActivity extends Activity
{
	
	private GridLayout mGridLayout;
	private ListView booklist;
	
	
	
	String mBookname[]=new String[]
			{
					"书名1 \n 阅读至xxx","书名2 阅读至yyy","书名3 \n 阅读至zzz","4\n4","5\n5","6\n6","4\n4","5\n5","6\n6"
					,"书名2 阅读至yyy","书名3 \n 阅读至zzz","4\n4","5\n5","6\n6","4\n4","5\n5","6\n6"
			};
	



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		booklist = (ListView)findViewById(R.id.booklist);
		booklist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mBookname));

		
		
        

		
		
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
