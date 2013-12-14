package com.example.broadkast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import android.util.Log;

/**
 * This thread is responsible for capturing the screen and writing it the output
 * streams of all currently connected sockets. Code for cleanly exiting this thread 
 * should be added in later implementations.
 *
 */
public class ScreenCaptureThread extends Thread {

	// Reference to serverThread
	private ServerThread serverThread;

	/**
	 * Creates a new ScreenCaptureThread.
	 * 
	 * @param serverThread Server thread from which output streams should be retreived
	 */
	public ScreenCaptureThread(ServerThread serverThread) {
		this.serverThread = serverThread;
	}

	/**
	 * Continuously takes screen shots and writes them to the currently 
	 * available output streams.
	 */
	@Override
	public void run() {
		// Buffer to read screen caputre into
		byte[] buffer = new byte[10000000];

		// Command used to capture the screen
		String cmd = new String(
				"/system/bin/screencap -p /storage/sdcard0/Pictures/screen.png");

		// Used to execute screen capture commmands
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		OutputStreamWriter osw = null;

		// Take screen shots and write them to output streams
		while (true) {
			try {
				// Run as super user so that screencap process can be called
				proc = runtime.exec("su");
				osw = new OutputStreamWriter(proc.getOutputStream());
				// Write screencap command to OutputStreamWriter
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

			// Check for screen capture file to exist
			File f = new File("/storage/sdcard0/Pictures/screen.png");
			while (!f.exists()) {

			}

			try {
				// Create input stream for screen capture
				FileInputStream fis = new FileInputStream(f);
				int nb = fis.read(buffer);
				fis.close();

				Log.i("Screen", "Num bytes = " + nb);

				// Read input stream into buffer and write it to output streams
				if (nb <= 10000000 && nb > 0) {
					write(buffer, 0, nb);
					f.delete();
				}

			} catch (IOException e) {

			}
		}
	}

	/**
	 * Writes size of the buffer and the entire contents of buffer 
	 * to socket output streams.
	 * @param buff The buffer to be written to socket output streams
	 */
	public void write(byte[] buff) {
		// Get output streams from serverThread
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if (oStreamArray == null) {
			return;
		}

		// Get 4 byte array containing the size of the buffer
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

	/**
	 * Writes numBytes and the specified portion of buffer 
	 * to socket output streams.
	 * @param buff The buffer to be written to socket output streams
	 * @param offset Index from buffer at which to start writing
	 * @param numBytes Number of bytes to write from the buffer
	 */
	public void write(byte[] buff, int offset, int numBytes) {
		// Get output streams from serverThread
		OutputStream[] oStreamArray = serverThread.getOSArray();
		if (oStreamArray == null) {
			return;
		}
		
		// Get 4 byte array containing the size of the buffer
		byte[] bytes = ByteBuffer.allocate(4).putInt(numBytes).array();

		// Write to each output stream
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
