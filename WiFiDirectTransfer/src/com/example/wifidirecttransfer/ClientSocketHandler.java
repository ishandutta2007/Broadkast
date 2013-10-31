package com.example.wifidirecttransfer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread{
	private InetAddress serverAddress;
	private TransferManager transfer;
	private WiFiDirectActivity activity;
	
	public ClientSocketHandler(InetAddress address, WiFiDirectActivity activity) {
		serverAddress = address;
		this.activity = activity;
	}
	
	@Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress.getHostAddress(),
                    WiFiDirectActivity.SERVER_PORT), 5000);
            transfer = new TransferManager(socket, activity);
            new Thread(transfer).start();
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
