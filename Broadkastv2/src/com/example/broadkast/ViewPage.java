package com.example.broadkast;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;


import com.example.broadkast.WiFiDirectServicesList;

public class ViewPage extends WiFiDirect {

	public static Activity activity;
	
	public IntentFilter intentFilter;
	
	private WiFiDirectServicesList servicesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		servicesList = new WiFiDirectServicesList();
        getFragmentManager().beginTransaction()
                .add(R.id.container_root, servicesList, "services").commit();
		setContentView(R.layout.view);
		this.setList(servicesList);
		servicesList.setWiFiDirect(this);
		discoverService();
		ViewPage.activity = this;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.stopCommunication();		
	}
	
	public WiFiDirect getWiFiDirect(){
		return this;
	}

}