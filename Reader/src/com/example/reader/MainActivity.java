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
	String name[]=new String[]
			{
					"1","2"
			};
	
	
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;    
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT;  


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		//�½�TableLayout01��ʵ��     
        TableLayout tableLayout = (TableLayout)findViewById(R.id.TableLayout01);    
        //ȫ�����Զ����հ״�     
        tableLayout.setStretchAllColumns(true);    
        //����10�У�8�еı��     
        for(int row=0;row<10;row++)    
        {    
            TableRow tableRow=new TableRow(this);    
            for(int col=0;col<8;col++)    
            {    
                //tv������ʾ     
                TextView tv=new TextView(this);    
                tv.setText("("+col+","+row+")");    
                tableRow.addView(tv);    
            }    
            //�½���TableRow��ӵ�TableLayout     
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));    
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
