package com.example.broadkast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MenuPage extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		Calendar c = Calendar.getInstance();
		System.out.println("Current time => "+c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		// formattedDate have current date/time
		Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

		//Phone Manufacturer 
		TextView Manufacturer = (TextView)  this.findViewById(R.id.Manufacturer);
		Manufacturer.setText("Manufacturer : "+ Build.MANUFACTURER);

		//Current Date and Time
		TextView timedate = (TextView)  this.findViewById(R.id.timedate);
		timedate.setText("Current Date and Time : "+formattedDate);

		//Android Build Version
		TextView buildVersionText = (TextView)  this.findViewById(R.id.buildVersion);
		buildVersionText.setText("Current Build : "+ Build.VERSION.RELEASE);
		


		//Device Type
		TextView device = (TextView)  this.findViewById(R.id.device);
		device.setText("Device Type : " + Build.DEVICE);


		//Screen Type
		TextView screentype = (TextView)  this.findViewById(R.id.screentype);
		screentype.setText("Screen Resolution : " + Configuration.SCREENLAYOUT_SIZE_MASK);

	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}