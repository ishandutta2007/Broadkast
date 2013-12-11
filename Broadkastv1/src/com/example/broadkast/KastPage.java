package com.example.broadkast;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class KastPage extends WiFiDirect {

	public static KastPage activity;
	public static View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kast);
		activity = this;
	}

	public void startBroadcast(View v) {
		Toast.makeText(this, "Setting up WiFi Direct", Toast.LENGTH_LONG)
				.show();
		KastPage.view = v;

		Intent i = new Intent(this, BroadcastService.class);
		startService(i);
	}

	public void stopBroadcast(View v) {
		stopService(new Intent(this, BroadcastService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}