package com.example.broadkast;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class KastPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kast);
	}
	

	public void startBroadcast (View v){
		Toast.makeText(this, "Setting up WiFi Direct", Toast.LENGTH_LONG).show();
		WiFiDirect wifid = new WiFiDirect(this);
		wifid.startRegistration();
		wifid.discoverService();
		Intent i = new Intent(this, BroadcastService.class);
		startService(i);
	}
	
	public void stopBroadcast (View v){
		stopService(new Intent(this, BroadcastService.class));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}