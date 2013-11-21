package com.example.broadkast;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        Button KastButton = (Button) findViewById(R.id.button2);

		KastButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            	Intent myIntent = new Intent(MainActivity.this, KastPage.class);
            	startActivityForResult(myIntent, 0);
            	
            	
            }
        }); 
	

	}



	public void MenuButton(View view){

		setContentView(R.layout.menu);
	}


	public void ViewButton(View view){

		setContentView(R.layout.view);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
