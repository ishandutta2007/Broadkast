package com.example.broadkast;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class KastPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kast);
	}

	public void onBackPressed() {

		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void startBroadcast(View v) {
		Intent i = new Intent(this, BroadcastService.class);
		startService(i);
	}

	public void stopBroadcast(View v) {
		stopService(new Intent(this, BroadcastService.class));
	}
}