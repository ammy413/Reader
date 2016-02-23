package com.example.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.GridLayout.LayoutParams; 

public class MainActivity extends Activity
{
	
	private GridLayout mGridLayout;
	String mBookname[]=new String[]
			{
					"书名1 \n 阅读至xxx","书名2 阅读至yyy","书名3 \n 阅读至zzz","4\n4","5\n5","6\n6","4\n4","5\n5","6\n6"
			};
	



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		

		
		//新建TableLayout01的实例     
        TableLayout tableLayout = (TableLayout)findViewById(R.id.TableLayout01);
        tableLayout.setStretchAllColumns(true);   
        for(int i=0;i<mBookname.length;i++)
        {
        	TableRow tableRow=new TableRow(this);
        	Button mbutton = new Button(this);
        	mbutton.setText(mBookname[i]);
        	tableRow.addView(mbutton);
        	tableLayout.addView(tableRow);    
        }
        
        
        
       
        
       
		
		
		
		
		
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
