package com.example.broadkast;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServerThread implements Runnable{

	private ServerSocket socket;
	private final int THREAD_COUNT = 10;

	public ServerThread() {
		try {
            socket = new ServerSocket(WiFiDirect.SERVER_PORT);
        } catch (IOException e) {
        	e.printStackTrace();
        	pool.shutdown();
        }
	}
	
	/**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
	public void run() {
		while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
            	
                pool.execute(new TransferManager(socket.accept()));
                /*
                activity.runOnUiThread(new Runnable(){	            	
                	@Override
                	public void run(){
                		activity.printMessage("Started a server socket. Pool executing:" + pool.getActiveCount());
                	}
                });
                */

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
	}


}

