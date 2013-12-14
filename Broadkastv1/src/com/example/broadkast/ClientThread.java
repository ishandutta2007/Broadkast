package com.example.broadkast;

import java.io.ByteArrayOutputStream;
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

/**
 * Creates a socket and attempts to connect to server. Also creates a ClientUiThread
 * for updating the ImageView. Reads from sockets input stream and writes received Bitmaps
 * to ClientUi queue. Code for cleanly exiting this thread should be added in later 
 * implementations.
 */
public class ClientThread implements Runnable {

	// Address of server that should be connected to
	private InetAddress mAddress;
	private final ClientUiThread uiThread;

	/**
	 * Sets initial parameters and starts ClientUiThread
	 * 
	 * @param groupOwnerAddress Server address
	 * @param wd Instance of WiFiDirect
	 * @param v ImageView to be updated by ClientUiThread
	 */
	public ClientThread(InetAddress groupOwnerAddress, WiFiDirect wd,
			ImageView v) {
		this.mAddress = groupOwnerAddress;
		this.uiThread = new ClientUiThread(wd, v);
		this.uiThread.start();

	}

	/**
	 * Connects to server socket. Then reads from input stream and writes
	 * received Bitmaps to ClientUiThread.
	 */
	@Override
	public void run() {
		// Create a new socket for this thread
		Socket socket = new Socket();
		try {
			// Connect to server socket
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiDirect.SERVER_PORT), 5000);

			// Get input stream from socket
			InputStream iStream = socket.getInputStream();

			// Buffer to read into
			byte[] buffer = new byte[4000000];
			int bytes;

			Log.i("WIFI", "Beginning to read from input stream");

			// Store data in byteOs until entire image/file has been received
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();

			// Indicates whether a file is currently being read in or not
			boolean readingFile = false;
			// Indicates the size of the file currently being read in
			int fileSize = 0;

			Log.i("WIFI", "In client thread");

			// Read from input stream and update clientUiThread continuously
			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						Log.e("Client thread",
								"Couldn't read from socket input stream.");
						break;
					}
					// Write data read from input stream to byteOs
					byteOs.write(buffer, 0, bytes);
					
					// Check if a file is being read
					if (!readingFile) {
						// If a file is not being read, check first for bytes to
						// find out the size of the next file to be sent across.
						if (byteOs.size() >= 4) {
							int osSize = byteOs.size();
							byte[] osBuff = byteOs.toByteArray();
							// Get fileSize from first four bytes
							fileSize = ByteBuffer.wrap(osBuff.clone(), 0, 4)
									.getInt();
							
							// Reset byteOs buff
							byteOs.reset();

							// Write any extra bytes received back into osBuff
							if (fileSize >= 0) {
								byteOs.write(osBuff, 4, osSize - 4);
								readingFile = true;
								Log.i("WIFI", "Filesize = " + fileSize);
							}
						}
					}

					// If the entire file has been received, create a bitmap
					if (readingFile && fileSize <= byteOs.size()) {
						// Get byte array from byteOs
						int osSize = byteOs.size();
						byte[] osBuff = byteOs.toByteArray();
						byteOs.reset();

						// Write any extra bytes received back into osBuff
						byteOs.write(osBuff, fileSize, osSize - fileSize);
						readingFile = false;

						// Create Bitmap from received byteArray
						final Bitmap bm = BitmapFactory.decodeByteArray(osBuff,
								0, fileSize);
						// Send new bitmap to the ClientUiThread
						uiThread.updateBitmaps(bm);

						Log.i("WIFI", "Saved file");
					}

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
