package com.example.broadkast;

import java.util.Iterator;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.Toast;

public class TestPeerListListener implements PeerListListener {

	WiFiDirectActivity activity;
	
	public TestPeerListListener(WiFiDirectActivity activity){
		this.activity = activity;
	}
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		String devicelist = "Devices: ";

		Iterator<WifiP2pDevice> iter = peers.getDeviceList().iterator();
		while(iter.hasNext()){
			devicelist = devicelist + iter.next().deviceName + ",";
		}
		Toast.makeText(activity, devicelist,
				Toast.LENGTH_SHORT).show();
		
	}	
}