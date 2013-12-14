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

/**
 * This class contains all of the code required to establish a WiFi Direct
 * Connection. It extends an activity and should be further extended by the
 * activities that wish to use it. Super constructors should be called to
 * properly setup WiFi Direct. This class also creates either a client thread or
 * a server and screen capture thread when connection information is available.
 * Future implementations of this class may want to more cleanly separate these
 * threads from the class or allow for other action to be taken when connection
 * info is received.
 */
public class WiFiDirect extends Activity implements ConnectionInfoListener {

	// Thread on which socket connection(s) is managed
	private Thread socketThread;

	// Thread on which screen is captured and transmitted
	private Thread screenCaptureThread;

	// Used to identify our WiFi Direct Service
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
	private WiFiDirectServicesList servicesList;

	private final WiFiDirect wifiDirect = this;

	/**
	 * Used to set the WiFiDirectServicesList that will be updated for user
	 * interaction
	 * 
	 * @param list
	 *            The list of services that should be updated when a new device
	 *            is discovered
	 */
	public void setList(WiFiDirectServicesList list) {
		servicesList = list;
	}

	/**
	 * Used to set up the WifiP2pManager, WifiDirect.channel, and
	 * WifiDirect.receiver required for talking and listening to the devices
	 * WifiDirect service.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

		// Choose actions that should be handled by the receiver
		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		manager.discoverPeers(channel, null);
	}

	/**
	 * Register receiver when inside of activity
	 */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, intentFilter);
	}

	/**
	 * Unregister receiver when activity isn't in foreground
	 */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	/**
	 * Call stop communication to end connection and clean up any of the manager's
	 * remaining requests.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopCommunication();
	}

	/**
	 * Called by 'Viewers' to connect to serviceDevice. This serviceDevice is
	 * currently just the most recently discovered device. Viewer should be able
	 * to select devices from list somehow.
	 */
	public void connectToDevice() {
		// Do nothing if a serviceDeivce has not yet been selected to connect to
		if (serviceDevice == null) {
			return;
		}

		Log.i("WIFI", "Connecting to device.");

		// Set up config with device and connection info
		WifiP2pConfig config = new WifiP2pConfig();
		config.groupOwnerIntent = 0;
		config.deviceAddress = serviceDevice.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		// Remove any existing service requests
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

		// Attempt connection and create listener
		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int errorCode) {
				wifiDirect.connectToDevice();
			}
		});
	}

	/**
	 * This function should be called by 'Broadcasters' so that other devices
	 * can discover and connect this device's service.
	 */
	public void startRegistration() {
		// Do nothing if a service has already been registered
		if (serviceInfo != null)
			return;

		Log.w(getClass().getName(), "Start Registration!");
		Map<String, String> record = new HashMap<String, String>();
		record.put("available", "visible");

		// Create new service
		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_NAME, "_presence._tcp", record);
		serviceInfo = service;
		
		// Register service through the manager
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

	/**
	 * Discovers services that have been registered by other devices. THis
	 * function should be called by devices who want to be 'Viewers'.
	 * servListener should add discover devices to a list so a 'Broadcaster' can
	 * be selected.
	 */
	public void discoverService() {

		/*
		 * Register listeners for DNS-SD services. These are callbacks invoked
		 * by the system when a service is actually discovered.
		 */

		DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice resourceType) {
				if (instanceName.equalsIgnoreCase(SERVICE_NAME)) {
					Log.w(getClass().getName(), "Discovered!");

					// Add service to the servicesList so the user can view them
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
			/*
			 * Callback includes: fullDomain: full domain name: e.g
			 * "printer._ipp._tcp.local." record: TXT record dta as a map of
			 * key/value pairs. device: The device running the advertised
			 * service.
			 */
			public void onDnsSdTxtRecordAvailable(String fullDomain,
					Map record, WifiP2pDevice device) {
			}
		};

		// Set the listeners created above for the manager
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
		
		// Initiate discover
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

	/**
	 * When connection info is available for the server or the client,
	 * set a server thread and screen capture thread or a client thread.
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		// Get p2pInfo
		this.p2pInfo = p2pInfo;

		Log.i("WIFI", "Connection Info Available");

		// Check if thread has already been started
		if (socketThread != null)
			return;

		// Create thread for socket communication
		boolean ready = p2pInfo != null && p2pInfo.groupFormed;

		// Create a new client thread if not the group owner
		if (ready && !p2pInfo.isGroupOwner) {
			ImageView imgView = new ImageView(this);
			setContentView(imgView);
			socketThread = new Thread(new ClientThread(
					p2pInfo.groupOwnerAddress, this, imgView));
			socketThread.start();
			Log.i("WIFI", "Started Client");
		} else {
			// Create a new server and screen capture thread if group owner
			if (ready && p2pInfo.isGroupOwner) {
				// Create ServerThread
				ServerThread serverThread = new ServerThread(this);
				socketThread = new Thread(serverThread);
				socketThread.start();
				Log.i("WIFI", "Started Server");

				// Create ScreenCaptureThread
				View v = this.getWindow().getDecorView().getRootView();
				v.setDrawingCacheEnabled(true);
				screenCaptureThread = new Thread(new ScreenCaptureThread(
						serverThread));
				screenCaptureThread.start();
				Log.i("SCREEN CAPTURE", "Started screen capture thread");
			}
		}

	}

	/**
	 * Set the serviceDevice that should be connected to. This method should
	 * be called from the onClick lisenter of a WiFiDirectServicesList item.
	 * @param serviceDevice The device to connect to.
	 */
	public void setServiceDevice(WifiP2pDevice serviceDevice) {
		this.serviceDevice = serviceDevice;
	}

	/**
	 * Stops and cleans up any remaining WifiDirect connections. Removes instances
	 * of p2pInfo and serviceDevice
	 */
	public void stopCommunication() {
		// Stop connecting to a device
		manager.cancelConnect(channel, new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {

			}
		});

		// Remove registered service if one exists
		if (serviceInfo != null)
			manager.removeLocalService(channel, serviceInfo,
					new ActionListener() {

						@Override
						public void onSuccess() {
						}

						@Override
						public void onFailure(int arg0) {

						}
					});

		// Remove service request if one exists
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest,
					new ActionListener() {

						@Override
						public void onSuccess() {
						}

						@Override
						public void onFailure(int arg0) {

						}
					});

		// Remove any groups that have benn created
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int arg0) {

			}
		});

		// Discard references to p2pInfo or serviceDevice
		this.p2pInfo = null;
		this.serviceDevice = null;
	}

}
