package com.example.sandbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class Broadcaster extends Thread {

	private LinkedList<TransferManager> managers = new LinkedList<TransferManager>();
	
	public Broadcaster() {
	}
	
	@Override
	public void run(){
		// wait for write or add
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTransferManager(TransferManager manager){
		managers.add(manager);
	}
	
	public void write(InputStream iStream){
		writeInputStream(iStream);
		// wait after finished
	}
	
	public void write(byte[] buffer){
		writeByteBuffer(buffer);
		// wait after finished
	}
	
	private void writeInputStream(InputStream iStream){
		int bytes;
		byte[] buffer = new byte[1024];
		while(true){
			try{
			bytes = iStream.read(buffer);
			}
			catch(IOException e){
				e.printStackTrace();
				break;
			}
			
			if(bytes == -1)
				break;
			
			writeByteBuffer(buffer);
		}
	}
	
	private void writeByteBuffer(byte[] buffer){
		Iterator<TransferManager> iter = managers.iterator();
		
		while(iter.hasNext()){
			iter.next().write(buffer);
		}
	}
}
