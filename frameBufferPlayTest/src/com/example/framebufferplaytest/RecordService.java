package com.example.framebufferplaytest;

import com.example.broadkast.KastPage;
import com.example.broadkast.R;
import com.example.broadkast.WiFiDirect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class RecordService extends Service {


	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;
	private boolean iscasting = false;
	View screen;
	
	WiFiDirect wifid;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		wifid = KastPage.activity;
		wifid.startRegistration();
		wifid.discoverService();
		
		broadcast();
		return (START_NOT_STICKY);
	}
	
	@Override
	public void onDestroy() {
		stop();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void broadcast(){
		if(!iscasting){
			Log.i(getClass().getName(), "Got to broadcast()!");
			iscasting = true;
			
			Notification note = new Notification(R.drawable.ic_launcher,
					"Starting Screen Broadcast", System.currentTimeMillis());
			Intent i = new Intent(this, KastPage.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			note.setLatestEventInfo(this, "Broadkast",
					"Now Broadcasting screen", pi);
			note.flags |= Notification.FLAG_NO_CLEAR;

			
			
			startForeground(1337, note);
			//captureScreen();

		}
	}

	
	private void stop() {
		wifid.stopBroadcasting();
		if(iscasting){
			Log.w(getClass().getName(), "Got to stop()!");
			iscasting = false;
			stopForeground(true);
			Toast.makeText(this, "Broadcast ended", Toast.LENGTH_LONG).show();
		}
	}
	

}
