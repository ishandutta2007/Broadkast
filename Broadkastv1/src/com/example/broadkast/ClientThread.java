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

public class ClientThread implements Runnable {

	private InetAddress mAddress;
	private final ClientUiThread uiThread;

	public ClientThread(InetAddress groupOwnerAddress, WiFiDirect wd,
			ImageView v) {
		this.mAddress = groupOwnerAddress;
		this.uiThread = new ClientUiThread(wd, v);
		this.uiThread.start();

	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					WiFiDirect.SERVER_PORT), 5000);

			InputStream iStream = socket.getInputStream();

			// String message = new String();
			byte[] buffer = new byte[4000000];
			int bytes;

			Log.i("WIFI", "Beginning to read from input stream");

			// Store data in byteOs until entire image/file has been received
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();

			boolean readingFile = false;
			int fileSize = 0;

			Log.i("WIFI", "In client thread");

			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						Log.e("Client thread",
								"Couldn't read from socket input stream.");
						break;
					}

					byteOs.write(buffer, 0, bytes);
					if (!readingFile) {
						if (byteOs.size() >= 4) {
							int osSize = byteOs.size();
							byte[] osBuff = byteOs.toByteArray();
							fileSize = ByteBuffer.wrap(osBuff.clone(), 0, 4)
									.getInt();

							byteOs.reset();

							if (fileSize >= 0) {
								byteOs.write(osBuff, 4, osSize - 4);
								readingFile = true;
								Log.i("WIFI", "Filesize = " + fileSize);
							}
						}
					}

					if (readingFile && fileSize <= byteOs.size()) {
						int osSize = byteOs.size();
						byte[] osBuff = byteOs.toByteArray();
						byteOs.reset();

						byteOs.write(osBuff, fileSize, osSize - fileSize);
						readingFile = false;

						final Bitmap bm = BitmapFactory.decodeByteArray(osBuff,
								0, fileSize);
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
