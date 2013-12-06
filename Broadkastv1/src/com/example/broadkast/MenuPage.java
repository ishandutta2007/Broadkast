package com.example.broadkast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
		
		setContentView(new myView(this));

	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 private class myView extends View{
		 int bytearray[];
		 
		 int index = 0;
		 public myView(Context context) {
		  super(context);
		  // TODO Auto-generated constructor stub
		  bytearray = new int [736*1280];
		  for (int i = 0 ; i < 736*1280; i ++){
				 bytearray[i] = 0xF000; 
		  }
		 }

		 @Override
		 protected void onDraw(Canvas canvas) {
		  // TODO Auto-generated method stub
			 //getResources().getIntArray(R.drawable.fb0);
			 Log.i("DRAW", "onDraw called");
			 //index = (index+1)%(736*1280);
			 
			 for (int i = 0 ; i < 736*1280; i ++){
				 bytearray[i] = bytearray[i]+0xf; 
		  }
			 
		 // Bitmap myBitmap = BitmapFactory.decodeByteArray(bytearray, 0, 736*1280*1);
		          canvas.drawBitmap(bytearray, 0, 736, 0, 0, 736, 750, false, null);
		          invalidate();
		 }
	}

}