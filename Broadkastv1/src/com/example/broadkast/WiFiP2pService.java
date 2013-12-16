/**
 * This class was taken from the WiFiDirectServiceDiscovery sample
 * application provided through the Android SDK. It is used to 
 * hold information about broadcasting devices in our application.
 */

package com.example.broadkast;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    WifiP2pDevice device;
    String instanceName = null;
    String serviceRegistrationType = null;
}
