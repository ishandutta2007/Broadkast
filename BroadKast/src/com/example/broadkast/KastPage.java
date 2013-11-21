package com.example.broadkast;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class KastPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kast);
	}
	
	public void onBackPressed(Bundle savedInstanceState) {
		super.onBackPressed();
		setContentView(R.layout.activity_main);
		finish();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}