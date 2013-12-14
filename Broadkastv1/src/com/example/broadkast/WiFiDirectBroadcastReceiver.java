package com.example.broadkast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

/**
 * This class is responsible for listening for changes in the WIFI_P2P state.
 * It should handle these changes by itself or through WiFiDirect as appropriate.
 * Further error handling should still be implemented in this class and in WiFiDirect.
 *
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	// Instances of objects created in WiFiDirect
	private WifiP2pManager manager;
	private Channel channel;
	private WiFiDirect direct;

	/**
	 * Pass in instances of the objects created in WiFiDirect to the constructor.
	 * 
	 * @param manager
	 * @param channel
	 * @param direct
	 */
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
			WiFiDirect direct) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.direct = direct;
	}

	/**
	 * Should handle intents as they are received. Most importantly, requesting
	 * connection info when P2P connection has changed.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("WIFI", "In onReceive of BroadcastReceiver");

		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wi-Fi P2P is enabled
			} else {
				// Wi-Fi P2P is not enabled
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// TODO
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {

			Log.i("WIFI", "P2P connection changed");

			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {
				// we are connected with the other device, request connection
				// info to find group owner IP
				Log.i("WIFI", "Requesting connection info");
				manager.requestConnectionInfo(channel,
						(ConnectionInfoListener) direct);
			} else {
				// It's a disconnect
			}
		}
	}
}
