package com.example.broadkast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.View;

public class ScreenCaptureThread extends Thread{

	private ServerThread serverThread;
	private View v;
	private WiFiDirect wd;

	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;


	public ScreenCaptureThread(ServerThread serverThread, View v, WiFiDirect wd){
		this.serverThread = serverThread;
		this.v = v;
		this.wd = wd;
	}

	@Override
	public void run() {
		//long prev = System.currentTimeMillis();

		byte[] buffer = new byte[10000000];
		int bytes = 0;

		String cmd = new String("/system/bin/screencap -p /storage/sdcard0/Pictures/screen.png");

		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		OutputStreamWriter osw = null;

		while(true){
			try { // Run Script
				proc = runtime.exec("su");
				osw = new OutputStreamWriter(proc.getOutputStream());
				osw.write(cmd);
				osw.flush();
				osw.close();
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						e.printStackTrace();                    
					}
				}
			}
			Log.i("Screen","Captured new screenShot.");

			File f = new File("/storage/sdcard0/Pictures/screen.png");
			while(!f.exists()){

			}
			/*
			int i = 0;
			while(i > 100000000)
				i++;
				*/
			
			
			try {
				FileInputStream fis = new FileInputStream(f);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				int nb = fis.read(buffer);
				fis.close();
				
				Log.i("Screen","Num bytes = " + nb);
				
				if(nb <= 10000000 && nb > 0){
					write(buffer,0,nb);
					f.delete();
				}

			} catch (IOException e) {

			}
		}
		
		/*
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		while(true){
			while(System.currentTimeMillis() % 10 != 0){

			}
			
			wd.runOnUiThread(new Runnable(){
				@Override
				public void run(){
				setBitmap(v.getDrawingCache());	
				}
			});
			Bitmap bm = getBitmap();
			if(bm != null){
				bm.compress(CompressFormat.WEBP, 1, os);
				write(os.toByteArray(), 0, os.size());
				os.reset();
			}
		}
		*/

	}
	
	private Bitmap bm;
	public synchronized void setBitmap(Bitmap bm){
		this.bm = bm;
	}
	
	public synchronized Bitmap getBitmap(){
		return this.bm;
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
