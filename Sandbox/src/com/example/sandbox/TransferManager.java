package com.example.sandbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TransferManager implements Runnable {


	private Socket socket = null;

	public TransferManager(Socket socket) {
		this.socket = socket;
	}

	private InputStream iStream;
	private OutputStream oStream;

	@Override
	public void run() {
		try {
			/*
			activity.runOnUiThread(new Runnable(){	            	
				@Override
				public void run(){
					activity.printMessage("In transfer manager!");
				}
			});
			*/
	
			iStream = socket.getInputStream();
			oStream = socket.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytes;
			oStream.write(new String("This is a test message from server").getBytes());

			String message = new String();

			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						break;
					}
					message = new String(buffer, 0, bytes);

					final String printMessage = new String(message);

					/*
					activity.runOnUiThread(new Runnable(){	            	
						@Override
						public void run(){
							if(printMessage != null){
								activity.printMessage(printMessage);
							}
						}
					});
					*/                    
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buffer) {
		try {
			oStream.write(buffer);
		} catch (IOException e) {

		}
	}

}
