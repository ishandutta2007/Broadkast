package com.example.broadkast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import android.util.Log;

public class ScreenCaptureThread extends Thread {

	private ServerThread serverThread;

	public ScreenCaptureThread(ServerThread serverThread) {
		this.serverThread = serverThread;
	}

	@Override
	public void run() {

		byte[] buffer = new byte[10000000];

		String cmd = new String(
				"/system/bin/screencap -p /storage/sdcard0/Pictures/screen.png");

		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		OutputStreamWriter osw = null;

		while (true) {
			try { // Run Script
				proc = runtime.exec("su");
				osw = new OutputStreamWriter(proc.getOutputStream());
				osw.write(cmd);
				osw.flush();
				osw.close();
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Log.i("Screen", "Captured new screenShot.");

			File f = new File("/storage/sdcard0/Pictures/screen.png");
			while (!f.exists()) {

			}

			try {
				FileInputStream fis = new FileInputStream(f);
				int nb = fis.read(buffer);
				fis.close();

				Log.i("Screen", "Num bytes = " + nb);

				if (nb <= 10000000 && nb > 0) {
					write(buffer, 0, nb);
					f.delete();
				}

			} catch (IOException e) {

			}
		}
	}

	public void write(byte[] buff) {
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if (oStreamArray == null) {
			return;
		}

		for (OutputStream oStream : oStreamArray) {
			try {
				// Write size
				byte[] bytes = ByteBuffer.allocate(4).putInt(buff.length)
						.array();
				oStream.write(bytes);

				// Write data
				oStream.write(buff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buff, int offset, int numBytes) {
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if (oStreamArray == null) {
			return;
		}

		byte[] bytes = ByteBuffer.allocate(4).putInt(numBytes).array();

		for (OutputStream oStream : oStreamArray) {
			try {
				// Write size
				oStream.write(bytes);

				int fileSize = ByteBuffer.wrap(bytes, 0, 4).getInt();
				Log.i("WIFI", "FileSize = " + fileSize);

				// Write data
				oStream.write(buff, offset, numBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
