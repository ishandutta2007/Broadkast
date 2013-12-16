package com.example.broadkast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
/**
 * The Activity for the viewing side of the app
 * The page is populated with broadcasters it detects on wifi direct
 * The user can then click a broadcaster to view their screen
 *
 */
public class ViewPage extends WiFiDirect {

	public static Activity activity;
	private WiFiDirectServicesList servicesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Setup the wifi direct and the list that will show the broadcasters
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

	public WiFiDirect getWiFiDirect() {
		return this;
	}

}