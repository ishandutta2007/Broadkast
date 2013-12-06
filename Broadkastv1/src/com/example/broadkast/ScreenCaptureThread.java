package com.example.broadkast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class ScreenCaptureThread implements Runnable{

	private ServerThread serverThread;

	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;
	private String TAG;
	public ScreenCaptureThread(ServerThread serverThread){
		this.serverThread = serverThread;
		TAG = getClass().getName();
	}

	@Override
	public void run() {
		long prev = System.currentTimeMillis();

//		File f = new File("/storage/sdcard0/Pictures/Screenshots/Screenshot_2013-10-31-12-13-28.png");
//		byte[] buffer = new byte[300000];
//		int bytes = 0;
		try {
			long totalBytes = 0;
            Process p = Runtime.getRuntime().exec("cat /dev/graphics/fb0");
            InputStream is = p.getInputStream();
            Log.i(TAG, "Starting sending framebuffer");
            byte[] buff = new byte[WIDTH*HEIGHT*3];
            while(true) {
                    
                    int nb = is.read(buff);
                    Log.i(TAG, "Num bytes read:"+ Integer.toString(nb) );
                    totalBytes += nb;
                    Log.i(TAG, "Total bytes read:"+Long.toString(totalBytes));
                    if(nb < 0)
                    {
                    	Log.e(TAG, "Error when reading from framebuffer?");
                    	is.close();
                    	is = p.getInputStream();
                    }else{
                    
                    	write(buff,0,nb);
                    }
                    
                    Thread.sleep(10);
                    
            }
//            is.close();
//            Log.w(TAG, "End of sending thread");
            
		} catch(Exception ex) {
			Log.e(TAG, "Exception in reading loop");
            ex.printStackTrace();
		}
		
		
//		try{
//			bytes = new FileInputStream(f).read(buffer);
//		}
//		catch(IOException e){
//			e.printStackTrace();
//		}
//		
		
//		while(true){
//			long curr = System.currentTimeMillis();
//			if(Math.abs(curr - prev) > 10000){
//				write(new String("CurrentTime = " + curr).getBytes());
//				prev = curr;
//			}
//		}
	}

	public void write(byte[] buff){
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if(oStreamArray == null){
			return;
		}
		
		for(OutputStream oStream : oStreamArray){
			try{
				oStream.write(buff);
			}
			catch(IOException e){
				Log.e(TAG, "Exception from writing to socket");
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buff, int offset, int numBytes){
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if(oStreamArray == null){
			return;
		}
		
		for(OutputStream oStream : oStreamArray){
			try{
				oStream.write(buff, offset, numBytes);
			}
			catch(IOException e){
				Log.e(TAG, "Exception from writing to socket");
				e.printStackTrace();
			}
		}
	}

}
