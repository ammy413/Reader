package com.example.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.GridLayout.LayoutParams; 

public class MainActivity extends Activity
{
	
	private GridLayout mGridLayout;
	String name[]=new String[]
			{
					"1","2"
			};
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		mGridLayout = (GridLayout) findViewById(R.id.root);
		Button button = new Button(this);
		Spec rowSpec = GridLayout.spec(2);
		Spec columnSpec = GridLayout.spec(0);
		LayoutParams layoutParams = new LayoutParams(rowSpec, columnSpec); 
		
		
		button.setText(name[0]);
		mGridLayout.addView(button,layoutParams);
       
		Button button2 = new Button(this);
		Spec rowSpec2 = GridLayout.spec(2);
		Spec columnSpec2 = GridLayout.spec(1);
		LayoutParams layoutParams2 = new LayoutParams(rowSpec2, columnSpec2); 
		
		
		button2.setText(name[1]);
		mGridLayout.addView(button2,layoutParams2);
		
		
		
		
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
