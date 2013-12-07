package com.example.broadkast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ClientThread implements Runnable{

	public static final int WIDTH = 736;
	public static final int HEIGHT = 1280;
	
	private InetAddress mAddress;
	private final ImageView screen;
	private final WiFiDirect wd;
	
	public ClientThread(InetAddress groupOwnerAddress, WiFiDirect wd, ImageView v) {
		this.mAddress = groupOwnerAddress;
		this.wd = wd;
		this.screen = v;
		
		
		
		/*
		screen = new StreamView(wd);
		ImageView v =  new ImageView(wd);
		Drawable draw = Drawable.createFromPath("/storage/sdcard0/Pictures/Screenshot_2013-10-04-15-07-01.png");
		v.setImageDrawable(draw);
		wd.setContentView(v);
		*/
		//wd.setContentView(screen);
		//Log.i("Client thread", "Set content view to StreamView.");
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiDirect.SERVER_PORT), 5000);

			InputStream iStream = socket.getInputStream();

			//String message = new String();
			//byte[] buffer = new byte[WIDTH*HEIGHT];
			byte[] buffer = new byte[4000000];
			int bytes;
			
			Log.i("WIFI", "Beginning to read from input stream");
			
			// Store data in byteOs until entire image/file has been received
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
			
			boolean readingFile = false;
			int fileSize = 0;
			int fileCount = 0;
			
			Log.i("WIFI","In client thread");
			
			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						Log.e("Client thread", "Couldn't read from socket input stream.");
						break;
					}
					
					
					byteOs.write(buffer,0,bytes);
					if (!readingFile) { 
						if(byteOs.size() >= 4){
							int osSize = byteOs.size();
							byte[] osBuff = byteOs.toByteArray();
							fileSize = ByteBuffer.wrap(osBuff.clone(), 0, 4).getInt();
							
							byteOs.reset();
							
							byteOs.write(osBuff, 4, osSize - 4);
							readingFile = true;
							Log.i("WIFI","Filesize = "  + fileSize);
						}
					}
					
					if (readingFile && fileSize <= byteOs.size()){
						int osSize = byteOs.size();
						byte[] osBuff = byteOs.toByteArray();
						byteOs.reset();
						
						byteOs.write(osBuff, fileSize, osSize - fileSize);
						readingFile = false;
						
						final Bitmap bm = BitmapFactory.decodeByteArray(osBuff, 0, fileSize);
						
						
						wd.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								screen.setImageBitmap(bm);
							}
						});
						
						fileCount++;
						Log.i("WIFI", "Saved file");
					}
					
					Log.i("WIFI","ByteOs.size = "  + byteOs.size());
					
					/*
					//message = new String(buffer, 0, bytes);
					byteOs.write(buffer);
					if(byteOs.size() >= WIDTH*HEIGHT)
					{
						//
					}
					File f = new File("/storage/sdcard0/Pictures","capture.png");
					
					f.delete();
					OutputStream os = new FileOutputStream(f);
					os.write(byteOs.toByteArray());
					os.close();
					*/
					
					/*
					final String printMessage = new String(message);
					
					ViewPage.activity.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							Toast.makeText(ViewPage.activity, printMessage, Toast.LENGTH_LONG).show();
						}
					});
					*/
					
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

