package com.example.broadkast;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class BroadcastService extends Service {

	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;
	private boolean iscasting = false;
	View screen;
	
	WiFiDirect wifid;
	
	
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
	
	private void sendFrameBuffer() {
        try {
                Process p = Runtime.getRuntime().exec("/system/bin/cat /dev/graphics/fb0");
                InputStream is = p.getInputStream();
                Log.w(getClass().getName(), "Starting sending framebuffer");
                //OutputStream os = s.getOutputStream();
                byte[] buff = new byte[WIDTH*HEIGHT*3];
                while(true) {
                        //FileInputStream fos = new FileInputStream("/dev/graphics/fb0");
                        int nb = is.read(buff);
                        if(nb < -1)
                                break;
                        //fos.close();
                        System.out.println("val "+nb);
                        //wifiD goes here
                        //os.write(buff,0,nb);
                        Thread.sleep(10);
                }
                is.close();
                Log.w(getClass().getName(), "End of sending thread");
                
        } catch(Exception ex) {
                ex.printStackTrace();
        }
}
}
