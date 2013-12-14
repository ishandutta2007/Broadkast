package com.example.broadkast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Creates a server socket and listens for connections on it. When a connection
 * is made, it should be added to a list of socket connections that have currently 
 * been made. Code for cleanly exiting this thread should be added in later implementations.
 */
public class ServerThread implements Runnable {

	// Server socket used for this thread
	private ServerSocket socket;
	// Holds reference to most recently made socket connection
	private Socket s;

	/**
	 * Used to initialize ServerSocket with the port specified in WiFiDirect.
	 * 
	 * @param wifid Instance of WifiDirect in which this thread was created
	 */
	public ServerThread(WiFiDirect wifid) {
		try {
			socket = new ServerSocket(WiFiDirect.SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// Listen for socket connection, block until received
				s = socket.accept();
				// Add new socket to list
				addSocket(s);
			} catch (IOException e) {
				try {
					if (socket != null && !socket.isClosed())
						socket.close();
				} catch (IOException ioe) {

				}
				e.printStackTrace();
				break;
			}
		}
	}

	/*List of sockets that have connected to the server socket.
	 * This list will also be accessed by ScreenCaptureThread so
	 * synchronous methods are required for accessing it.
	 */
	ArrayList<Socket> sockets = new ArrayList<Socket>();

	/**
	 * Add a socket to the current list of connections.
	 * @param s Socket to be added.
	 */
	public synchronized void addSocket(Socket s) {
		sockets.add(s);
	}

	/**
	 * Returns output streams that should be written to by ScreenCaptureThread.
	 * @return Array of output streams corresponding to connected sockets.
	 */
	public synchronized OutputStream[] getOSArray() {
		if (sockets.size() == 0)
			return null;

		OutputStream[] oStreamArray = new OutputStream[sockets.size()];

		try {
			for (int i = 0; i < sockets.size(); i++)
				oStreamArray[i] = sockets.get(i).getOutputStream();
		} catch (IOException e) {
			return null;
		}

		return oStreamArray;
	}
}
