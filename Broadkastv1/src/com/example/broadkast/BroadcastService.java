package com.example.broadkast;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Broadcast Service is used to start this application as a background service
 * The wifiDirect is initialized and starts running in the background
 *
 */
public class BroadcastService extends Service {

	private boolean iscasting = false;

	WiFiDirect wifid;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//Initialize the wifi direct
		wifid = KastPage.activity;
		wifid.startRegistration();

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

	/**
	 * Sets up the background process notifies the user with notifications and toasts
	 */
	private void broadcast() {
		if (!iscasting) {
			Log.w(getClass().getName(), "Got to broadcast()!");
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

			//Starts the background service in foreground so it cannot be killed
			startForeground(1337, note);
		}
	}

	/**
	 * Ends the service and notifies the user it has been ended
	 */
	private void stop() {
		wifid.stopCommunication();
		if (iscasting) {
			Log.w(getClass().getName(), "Got to stop()!");
			iscasting = false;
			stopForeground(true);
			Toast.makeText(this, "Broadcast ended", Toast.LENGTH_LONG).show();
		}
	}
}
