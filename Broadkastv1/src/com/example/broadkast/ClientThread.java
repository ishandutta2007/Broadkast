package com.example.broadkast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;
import android.widget.Toast;

public class ClientThread implements Runnable{

	private InetAddress mAddress;

	public ClientThread(InetAddress groupOwnerAddress) {
		this.mAddress = groupOwnerAddress;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiDirect.SERVER_PORT), 5000);

			InputStream iStream = socket.getInputStream();

			String message = new String();
			byte[] buffer = new byte[1024];
			int bytes;
			
			Log.i("WIFI", "Beginning to read from input stream");
			
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
			
			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						break;
					}
					message = new String(buffer, 0, bytes);

					/*
					byteOs.write(buffer);
					
					File f = new File("/storage/sdcard0/Pictures","capture.png");
					
					f.delete();
					OutputStream os = new FileOutputStream(f);
					os.write(byteOs.toByteArray());
					os.close();
					
					
					Log.i("WIFI","ByteOs.size = "  + byteOs.size());
					*/
					
					final String printMessage = new String(message);
					
					ViewPage.activity.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							Toast.makeText(ViewPage.activity, printMessage, Toast.LENGTH_LONG).show();
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

