package com.example.broadkast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{

	private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirectActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
            WiFiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
    	
    	if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            	// Wi-Fi P2P is enabled
            	activity.setIsEnabled(true);
            } else {
                // Wi-Fi P2P is not enabled
            }
        }
    	else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
    			// TODO
    	}
    	else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
    		
    		if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            
            if (networkInfo.isConnected()) {
            	activity.printMessage("Connected to a device");
                // we are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, (ConnectionInfoListener) activity);
            } else {
                // It's a disconnect
            }
    	}
    }
}
