package com.example.wifidirecttransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.widget.Toast;

public class TransferManager implements Runnable {


    private Socket socket = null;
    private WiFiDirectActivity activity;
    
    public TransferManager(Socket socket, WiFiDirectActivity activity) {
        this.socket = socket;
        this.activity = activity;
    }

    private InputStream iStream;
    private OutputStream oStream;

    @Override
    public void run() {
        try {

            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            String message = new String();
            
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    message = message + new String(buffer, 0, bytes);

                } catch (IOException e) {
                	
                }
            }
            
            Toast.makeText(activity, message, Toast.LENGTH_LONG);
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {

        }
    }
	
}
