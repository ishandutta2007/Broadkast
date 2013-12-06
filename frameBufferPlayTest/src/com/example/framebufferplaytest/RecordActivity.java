package com.example.framebufferplaytest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class RecordActivity extends Activity{

	
	private Thread thread = null;
	private RecordThread recorder = null;
	@Override
	public void onStart()
	{
	}
	
	@Override
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		
		setContentView(R.layout.activity_record);
	}
	
	public void onClickStart()
	{
		recorder = new RecordThread();
		thread= new Thread(recorder);
		thread.start();
	}
	
	public void onClickStop()
	{
		if(thread != null)
		{
			recorder.stop();
			thread.join();
			Log.i(getClass().getName(), "Recorder stopped");
		}
		
	}
	public class RecordThread implements Runnable{

		private volatile boolean running = true;
		private final int WIDTH = 736;
		private final int HEIGHT = 1280;
		
		public void stop()
		{
			running = false;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
		        Process p = Runtime.getRuntime().exec("/system/bin/cat /dev/graphics/fb0");
		        InputStream is = p.getInputStream();
		        FileOutputStream os = new FileOutputStream(new File("./fb0"));
		        Log.i(getClass().getName(), "Starting sending framebuffer");
		        byte[] buff = new byte[WIDTH*HEIGHT*3];
		        while(running) {
		                
		                int nb = is.read(buff);
		                if(nb < -1)
		                {
		                	Log.e(getClass().getName(), "couldn't read from fb0 input stream");
		                	break;
		                }
		                
		                os.write(buff,0,nb);
		                
		        }
		        is.close();
		        Log.i(getClass().getName(), "End of sending thread");
		        
			} catch(Exception ex) {
				Log.e(getClass().getName(), "Error in record thread");
		        ex.printStackTrace();
			}
		}
		
	}
	
}
