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
	
	public ScreenCaptureThread(ServerThread serverThread){
		this.serverThread = serverThread;
	}

	@Override
	public void run() {
		long prev = System.currentTimeMillis();

//		File f = new File("/storage/sdcard0/Pictures/Screenshots/Screenshot_2013-10-31-12-13-28.png");
//		byte[] buffer = new byte[300000];
//		int bytes = 0;
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
				e.printStackTrace();
			}
		}
	}

}
