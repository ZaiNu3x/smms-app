package group.intelliboys.smms.configs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import group.intelliboys.smms.services.Utils;

public class NetworkConfig {
    private static NetworkConfig networkConfigInstance;
    private String deviceIpAddress;

    public static synchronized NetworkConfig getInstance() {
        if (networkConfigInstance == null) {
            networkConfigInstance = new NetworkConfig();
        }
        return networkConfigInstance;
    }

    @SuppressLint("DefaultLocale")
    private String fetchDeviceIpAddress(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }
        return null;
    }

    public String getDeviceIpAddress() {
        if (deviceIpAddress == null) {
            Context context = Utils.getInstance().getApplicationContext();
            deviceIpAddress = fetchDeviceIpAddress(context);
        }
        return deviceIpAddress;
    }

    public boolean isNetworkActive() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        return network != null;
    }

    public String getServerIpAddress() {
        if (deviceIpAddress == null) {
            Context context = Utils.getInstance().getApplicationContext();
            deviceIpAddress = fetchDeviceIpAddress(context);
            if (deviceIpAddress == null) {
                return null;
            }
        }

        String[] ipSubParts = deviceIpAddress.split("\\.");
        return "https://192.168." + ipSubParts[2] + ".254:443";
    }
}
