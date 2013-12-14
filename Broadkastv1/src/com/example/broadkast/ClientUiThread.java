package com.example.broadkast;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * This class if responsible for updating the UiThread with the Bitmaps
 * it has most recently received. It takes an ImageView and an instance
 * of WifiDirect and uses these to update the ImageView on the UiThread.
 * It also allows for synchronous access to a Bitmap queue that allows older
 * bitmaps to be overwritten if they are not written to the ui soon enough.
 */
public class ClientUiThread extends Thread {

	// Instance of WifiDirect
	WiFiDirect wifiDirect;
	
	// Bitmap queue to add and remove images from
	Bitmap[] bitmap = new Bitmap[5];
	// Indicates the index of the oldest bitmap in the queue
	int oldest = 0;
	// Indicates the current number of bitmaps in the queue
	int size = 0;

	// Image view to be updated
	final ImageView screen;

	/**
	 * Used to create a new ClientUiThread.
	 * @param wifiDirect Instance of WifiDirect
	 * @param v ImageView to be updated
	 */
	public ClientUiThread(WiFiDirect wifiDirect, ImageView v) {
		this.wifiDirect = wifiDirect;
		this.screen = v;
	}

	/**
	 * Checks for bitmaps to be in the queue and update the UiThread with
	 * them as soon as possible.
	 */
	@Override
	public void run() {
		while (true) {
			// Get new bitmap from array
			final Bitmap bm = getBitmap();

			// Update UiThread if a bitmap was returned
			if (bm != null) {
				wifiDirect.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						screen.setImageBitmap(bm);
					}
				});
			}

		}
	}

	/**
	 * Synchronously modifies the bitmap queue with a new bitmap
	 * from the ClientThread.
	 * @param bm Bitmap to be added to the queue
	 */
	public synchronized void updateBitmaps(Bitmap bm) {
		// Overwrite the oldest bitmap if the queue is full
		if (size == 5){
			bitmap[oldest] = bm;
			oldest = (oldest + 1) % 5;
		}
		// Update queue
		else {
			bitmap[(oldest + size) % 5] = bm;
			size++;
		}
	}

	/**
	 * Synchronously remove a bitmap from the queue
	 * @return The oldest bitmap or null if queue if empty
	 */
	public synchronized Bitmap getBitmap() {
		if (size == 0)
			return null;

		Bitmap bm = bitmap[oldest];
		oldest = (oldest + 1) % 5;
		size--;
		return bm;
	}

}
