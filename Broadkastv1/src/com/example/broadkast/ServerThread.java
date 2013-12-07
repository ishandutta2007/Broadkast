package com.example.broadkast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable{

	private ServerSocket socket;
	private Socket s;

	public ServerThread(WiFiDirect wifid) {
		try {
            socket = new ServerSocket(WiFiDirect.SERVER_PORT);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

    @Override
	public void run() {
		while (true) {
            try {                
            	s = socket.accept();
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

