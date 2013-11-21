package com.example.broadkast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientThread implements Runnable{

	private WiFiDirectActivity activity;
	private InetAddress mAddress;

	public ClientThread(InetAddress groupOwnerAddress, WiFiDirectActivity activity) {
		this.mAddress = groupOwnerAddress;
		this.activity = activity;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiDirectActivity.SERVER_PORT), 5000);

			InputStream iStream = socket.getInputStream();
			OutputStream oStream = socket.getOutputStream();

			// Write 
			oStream.write(new String("This is a test message from client:" + System.currentTimeMillis()).getBytes());

			String message = new String();
			byte[] buffer = new byte[1024];
			int bytes;
			
			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						break;
					}
					message = new String(buffer, 0, bytes);

					final String printMessage = new String(message);

					activity.runOnUiThread(new Runnable(){	            	
						@Override
						public void run(){
							if(printMessage != null){
								activity.printMessage(printMessage);
							}
						}
					});                    
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}


}

