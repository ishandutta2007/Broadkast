package com.example.broadkast;

import java.io.IOException;
import java.io.OutputStream;

public class ScreenCaptureThread implements Runnable{

	private ServerThread serverThread;

	public ScreenCaptureThread(ServerThread serverThread){
		this.serverThread = serverThread;
	}

	@Override
	public void run() {
		long prev = System.currentTimeMillis();

		while(true){
			long curr = System.currentTimeMillis();
			if(Math.abs(curr - prev) > 10000){
				write(new String("CurrentTime = " + curr).getBytes());
				prev = curr;
			}
		}
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
