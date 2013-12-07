package com.example.broadkast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class ScreenCaptureThread implements Runnable{

	private ServerThread serverThread;
	private View v;

	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;


	public ScreenCaptureThread(ServerThread serverThread, View v){
		this.serverThread = serverThread;
		this.v = v;
	}

	@Override
	public void run() {
		//long prev = System.currentTimeMillis();

		byte[] buffer = new byte[4000000];
		int bytes = 0;
		/*
		try {
			Process p = Runtime.getRuntime().exec("/system/bin/cat /dev/graphics/fb0");
			InputStream is = p.getInputStream();
			Log.i(getClass().getName(), "Starting sending framebuffer");
			byte[] buff = new byte[WIDTH*HEIGHT*3];
			while(true) {

				int nb = is.read(buff);
				if(nb < -1)
					break;

				write(buff,0,nb);

			}
			is.close();
			Log.w(getClass().getName(), "End of sending thread");

		} catch(Exception ex) {
			ex.printStackTrace();
		}
		 */

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		while(true){
			while(System.currentTimeMillis() % 1000 != 0){

			}

			v.setDrawingCacheEnabled(true);
			Bitmap bm = v.getDrawingCache();
			bm.compress(CompressFormat.WEBP, 1, os);
			write(os.toByteArray(), 0, os.size());
			os.reset();
		}

	}

	public void write(byte[] buff){
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if(oStreamArray == null){
			return;
		}

		for(OutputStream oStream : oStreamArray){
			try{
				// Write size
				byte[] bytes = ByteBuffer.allocate(4).putInt(buff.length).array();
				oStream.write(bytes);


				// Write data
				oStream.write(buff);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buff, int offset, int numBytes){		
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if(oStreamArray == null){
			return;
		}

		byte[] bytes = ByteBuffer.allocate(4).putInt(numBytes).array();

		for(OutputStream oStream : oStreamArray){
			try{
				// Write size
				oStream.write(bytes);

				int fileSize = ByteBuffer.wrap(bytes, 0, 4).getInt();
				Log.i("WIFI","FileSize = " + fileSize);

				// Write data
				oStream.write(buff, offset, numBytes);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}

}
