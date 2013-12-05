package com.example.broadkast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServerThread implements Runnable{

	private ServerSocket socket;
	private Socket s;
	private WiFiDirect wifid;
	private final int THREAD_COUNT = 10;

	public ServerThread(WiFiDirect wifid) {
		this.wifid = wifid;
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
            	s = socket.accept();
            	
            	addSocket(s);
            	
            	/*
            	wifid.runOnUiThread(new Runnable(){
            		@Override
            		public void run(){
            			wifid.addSocket(s);  
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
    
    ArrayList<Socket> sockets = new ArrayList<Socket>();
    
    public synchronized void addSocket(Socket s){
    	sockets.add(s);
    }
    
    public synchronized OutputStream[] getOSArray(){
    	if(sockets.size() == 0)
    		return null;
    	
    	OutputStream[] oStreamArray = new OutputStream[sockets.size()];
    	
    	try{
    		for(int i = 0; i < sockets.size(); i++)
    			oStreamArray[i] = sockets.get(i).getOutputStream();
    	}
    	catch(IOException e){
    		return null;
    	}
    	
    	return oStreamArray;
    }

}

