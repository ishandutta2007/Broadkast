package com.example.broadkast;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import com.example.broadkast.WiFiDirectServicesList;

public class ViewPage extends Activity {

	private WiFiDirectServicesList servicesList;
	private WiFiDirect wifiD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wifiD = new WiFiDirect(this);
		wifiD.discoverService();
		servicesList = new WiFiDirectServicesList();
        getFragmentManager().beginTransaction()
                .add(R.id.container_root, servicesList, "services").commit();
		setContentView(R.layout.view);
		wifiD.setList(servicesList);
		servicesList.setWiFiDirect(wifiD);
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public WiFiDirect getWiFiDirect(){
		return wifiD;
	}

}