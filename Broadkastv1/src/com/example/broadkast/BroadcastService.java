package com.example.broadkast;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BroadcastService extends Service {

	private boolean iscasting = false;

	WiFiDirect wifid;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

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

			startForeground(1337, note);
		}
	}

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
