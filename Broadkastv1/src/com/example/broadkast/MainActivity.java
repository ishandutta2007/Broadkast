package com.example.broadkast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

/**
 * 
 * The main menu activity of the app
 * The user clicks Kast if they want to broadcast their screen
 * The user clicks View to see other's broadcasts
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	/**
	 * Goes the the kast page to let a user broadcast
	 * @param view
	 */
	public void KastButton(View view) {

		Intent myIntent = new Intent(MainActivity.this, KastPage.class);
		MainActivity.this.startActivity(myIntent);

	}

	/**
	 * Goes to the menu page for debugging and other info
	 * @param view
	 */
	public void MenuButton(View view) {

		Intent myIntent = new Intent(MainActivity.this, MenuPage.class);
		MainActivity.this.startActivity(myIntent);
	}

	/**
	 * Goes to the view page to start viewing broadcasts
	 * @param view
	 */
	public void ViewButton(View view) {

		Intent myIntent = new Intent(MainActivity.this, ViewPage.class);
		MainActivity.this.startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
