package com.example.broadkast;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ClientUiThread extends Thread{

	WiFiDirect wifiDirect;

	Bitmap[] bitmap = new Bitmap[5];
	int oldest = 0;
	int size = 0;

	final ImageView screen;

	public ClientUiThread(WiFiDirect wifiDirect, ImageView v){
		this.wifiDirect = wifiDirect;
		this.screen = v;
	}

	@Override
	public void run() {
		while(true){
			
			final Bitmap bm = getBitmap();

			if(bm != null){
				wifiDirect.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						screen.setImageBitmap(bm);
					}
				});
			}


		}
	}

	public synchronized void updateBitmaps(Bitmap bm){
		if(size == 5)
			bitmap[oldest] = bm;
		else{
			bitmap[(oldest + size) % 5] = bm;
			size++;
		}
	}

	public synchronized Bitmap getBitmap(){
		if(size == 0)
			return null;

		Bitmap bm =  bitmap[oldest];
		oldest = (oldest + 1) % 5;
		size--;
		return bm;
	}


}
