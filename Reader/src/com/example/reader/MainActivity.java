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
					"1","2"
			};
	
	



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//新建TableLayout01的实例     
        TableLayout tableLayout = (TableLayout)findViewById(R.id.TableLayout01);
        
        
        TableRow tableRow=new TableRow(this);
        
        
        Button button1 = new Button(this);
        Button button2 = new Button(this);
        button1.setText(mBookname[0]);
        button2.setText(mBookname[1]);
        
        tableRow.addView(button1);
        tableRow.addView(button2);
        
        tableLayout.addView(tableRow); 
        
       
		
		
		
		
		
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
