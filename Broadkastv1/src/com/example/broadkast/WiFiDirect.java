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
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.broadkast.WiFiDirectServicesList.WiFiDevicesAdapter;

public class WiFiDirect extends Activity implements ConnectionInfoListener {

	// Thread on which socket connection(s) is managed
	private Thread socketThread;
	private WiFiDirectServicesList servicesList;

	// Thread on which screen is captured and transmitted
	private Thread screenCaptureThread;

	private final String SERVICE_NAME = "Broadkast";
	static final int SERVER_PORT = 7878;

	// Used for managing WiFi Direct connections
	private WifiP2pManager manager;
	private Channel channel;
	private BroadcastReceiver receiver;
	private IntentFilter intentFilter;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	private WifiP2pServiceInfo serviceInfo;


	// Info about current connection/group
	private WifiP2pDevice serviceDevice;
	private WifiP2pInfo p2pInfo;
	
	private final WiFiDirect wifiDirect = this;

	public void setList (WiFiDirectServicesList list){
		servicesList = list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		//intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		manager.discoverPeers(channel, null);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopCommunication();
	}

	/* Called by 'Viewers' to connect to serviceDevice. This serviceDevice is currently
	 * just the most recently discovered device. Viewer should be able to select
	 * devices from list somehow.
	 */
	public void connectToDevice(){
		if(serviceDevice == null){
			//printMessage("No devices have been found.");
			return;
		}

		Log.i("WIFI","Connecting to device.");

		WifiP2pConfig config = new WifiP2pConfig();
		config.groupOwnerIntent = 0;
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
				//printMessage("Connected to device successfully");
			}

			@Override
			public void onFailure(int errorCode) {
				wifiDirect.connectToDevice();
			}
		});
	}

	/*
	 * This function should be called by 'Broadcasters' so that other devices
	 * can discover and connect to them.
	 */
	public void startRegistration(){
		if(serviceInfo != null)
			return;

		Log.w(getClass().getName(), "Start Registration!");
		Map<String, String> record = new HashMap<String, String>();
		record.put("available", "visible");

		/*
		manager.createGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.w(getClass().getName(), "Created P2P Group");
			}

			@Override
			public void onFailure(int error) {
				Log.w(getClass().getName(), "Failed to create Group!");
			}
		});*/

		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_NAME, "_presence._tcp", record);
		serviceInfo = service;		
		manager.addLocalService(channel, service, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.w(getClass().getName(), "Successful registration!");
			}

			@Override
			public void onFailure(int error) {
				Log.w(getClass().getName(), "Failed registration!");
			}
		});
	}

	/*
	 * Discovers services that have been registered by other devices. THis
	 * function should be called by devices who want to be 'Viewers'. servListener
	 * should add discover devices to a list so a 'Broadcaster' can be selected.
	 */
	public void discoverService(){

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
					//EditText editText = (EditText) findViewById(R.id.edit_message);
					//editText.setText(resourceType.deviceName + ":" + instanceName);
					Log.w(getClass().getName(), "Discovered!");
					//serviceDevice = resourceType;

					if (servicesList != null) {
						WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) servicesList
								.getListAdapter());
						WiFiP2pService service = new WiFiP2pService();
						service.device = resourceType;
						service.instanceName = instanceName;
						service.serviceRegistrationType = registrationType;
						adapter.add(service);
						adapter.notifyDataSetChanged();
					}	
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
				//Log.i("WIFI", "DnsSdTxtRecord available -" + record.toString());
			}
		};

		manager.setDnsSdResponseListeners(channel, servListener, txtListener);


		// After attaching listeners, create a service request and initiate
		// discovery.
		manager.clearServiceRequests(channel, null);
		
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, serviceRequest,
				new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {
				wifiDirect.discoverService();
			}
		});
		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				// Notify successful
			}

			@Override
			public void onFailure(int arg0) {
				wifiDirect.discoverService();
			}
		});
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		// Get p2pInfo
		this.p2pInfo = p2pInfo;

		Log.i("WIFI", "Connection Info Available");

		// Check if thread has already been started
		if(socketThread != null)
			return;



		// Create thread for socket communication
		boolean ready = p2pInfo != null && p2pInfo.groupFormed;

		if (ready && !p2pInfo.isGroupOwner){
			ImageView imgView = new ImageView(this);
			setContentView(imgView);
			socketThread = new Thread(new ClientThread(p2pInfo.groupOwnerAddress,this,imgView));
			socketThread.start();
			Log.i("WIFI", "Started Client");
		}
		else{
			if (ready && p2pInfo.isGroupOwner){
				// Create ServerThread
				ServerThread serverThread = new ServerThread(this);
				socketThread = new Thread(serverThread);
				socketThread.start();
				Log.i("WIFI", "Started Server");

				// Create ScreenCaptureThread
				// activity= getCurrentActivity();
				View v = this.getWindow().getDecorView().getRootView();
				screenCaptureThread = new Thread(new ScreenCaptureThread(serverThread,v));
				screenCaptureThread.start();
				Log.i("SCREEN CAPTURE", "Started screen capture thread");
			}
		}

	}


	public void setServiceDevice(WifiP2pDevice serviceDevice){
		this.serviceDevice = serviceDevice;
	}

	public void stopCommunication(){
		manager.cancelConnect(channel, new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {

			}
		});

		if(serviceInfo != null)
			manager.removeLocalService(channel, serviceInfo, new ActionListener() {

				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int arg0) {

				}
			});

		if(serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {

				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int arg0) {

				}
			});

		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {

			}
		});
		

		this.p2pInfo = null;
		this.serviceDevice = null;
	}

	
}
