package com.example.broadkast;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WiFiDirectActivity extends Activity implements ConnectionInfoListener{

	private Thread socketThread;
	
	private final String SERVICE_NAME = "test";
	static final int SERVER_PORT = 7878;

	private WifiP2pManager manager;
	private Channel channel;
	private BroadcastReceiver receiver;
	private IntentFilter intentFilter;
	private WifiP2pDnsSdServiceRequest serviceRequest;

	private boolean isWifiP2pEnabled = false;

	// Service Info
	private WifiP2pDevice serviceDevice;

	private WifiP2pInfo p2pInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.w(getClass().getName(), "Got to onCreate in wifi()!");
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		//intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	protected void setIsEnabled(boolean enabled){
		isWifiP2pEnabled = enabled;
		Toast.makeText(WiFiDirectActivity.this, "isEnabled = true",
				Toast.LENGTH_SHORT).show();
	}


	public void sendMessage(View view){	
		EditText editText = (EditText) findViewById(R.id.edit_message);

		boolean ready = p2pInfo != null && p2pInfo.groupFormed;
		
		if (ready)
			printMessage(p2pInfo.isGroupOwner + " " + p2pInfo.groupOwnerAddress.getHostAddress() + p2pInfo.groupFormed);
		
		if (ready && !p2pInfo.isGroupOwner){
			socketThread = new Thread(new ClientThread(p2pInfo.groupOwnerAddress, this));
			socketThread.start();
		}
		else{
			editText.setText("Could Not send message");
			if (ready && p2pInfo.isGroupOwner){
				socketThread = new Thread(new ServerThread(this));
				socketThread.start();
			}
		}
	}

	public void connectToDevice(View view){

		if(serviceDevice == null){
			printMessage("No devices have been found.");
			return;
		}
		
		TextView textView = (TextView) findViewById(R.id.text_message);

		textView.setText("Connecting to " + serviceDevice.deviceName);

		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = serviceDevice.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest,
					new ActionListener() {

				@Override
				public void onSuccess() {
					//
				}

				@Override
				public void onFailure(int arg0) {
					//
				}
			});

		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				printMessage("Connected to device successfully");
			}

			@Override
			public void onFailure(int errorCode) {
				printMessage("Failed to connect to device");
			}
		});
	}

	public void startRegistration(View view){
		Map<String, String> record = new HashMap<String, String>();
		record.put("available", "visible");

		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_NAME, "_presence._tcp", record);
		manager.addLocalService(channel, service, new ActionListener() {

			@Override
			public void onSuccess() {
				EditText editText = (EditText) findViewById(R.id.edit_message);
				editText.setText("Registered");
			}

			@Override
			public void onFailure(int error) {
				EditText editText = (EditText) findViewById(R.id.edit_message);
				editText.setText("Didn't register service");
			}
		});
		
		respondToDiscoverService(view);
	}

	public void respondToDiscoverService(View view){

		/*
		 * Register listeners for DNS-SD services. These are callbacks invoked
		 * by the system when a service is actually discovered.
		 */

		DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName, String registrationType,
					WifiP2pDevice resourceType) {
				if(instanceName.equalsIgnoreCase(SERVICE_NAME)){
					// Add service
					EditText editText = (EditText) findViewById(R.id.edit_message);
					editText.setText(resourceType.deviceName + ":" + instanceName);
					setServiceDevice(resourceType);
				}
			}
		};

		DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
			@Override
			/* Callback includes:
			 * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
			 * record: TXT record dta as a map of key/value pairs.
			 * device: The device running the advertised service.
			 */

			public void onDnsSdTxtRecordAvailable(
					String fullDomain, Map record, WifiP2pDevice device) {
				//Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
				//buddies.put(device.deviceAddress, record.get("buddyname"));
			}
		};

		manager.setDnsSdResponseListeners(channel, servListener, txtListener);


		// After attaching listeners, create a service request and initiate
		// discovery.
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, serviceRequest,
				new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {
			}
		});
		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				// Notify successful
			}

			@Override
			public void onFailure(int arg0) {
				// Notify failed
			}
		});
	}

	public void setServiceDevice(WifiP2pDevice device){
		serviceDevice = device;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		printMessage("group formed = " + p2pInfo.groupFormed + ", owner address = "
				+  p2pInfo.groupOwnerAddress.getHostAddress() + ", Is group owner = " +  p2pInfo.isGroupOwner);

		this.p2pInfo = p2pInfo;
	}


	public void printMessage(String message){
		TextView textView = (TextView) findViewById(R.id.text_message);

		textView.setText(message);
	}

}
