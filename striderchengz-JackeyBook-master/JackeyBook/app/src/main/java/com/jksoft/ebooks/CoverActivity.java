package com.jksoft.ebooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

//∑‚√Ê
public class CoverActivity extends Activity {

    Button btn_o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        btn_o = (Button) findViewById(R.id.btn_o);

        btn_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(CoverActivity.this, CatalogActivity.class);

                CoverActivity.this.startActivity(i);
            }
        });
    }

}
