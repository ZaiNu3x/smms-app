package group.intelliboys.smms.configs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkConfig {
    private static NetworkConfig networkConfigInstance;
    private String deviceIpAddress;

    public static String HOST = "192.168.1.254:";
    public static String PORT = "433";

    private NetworkConfig() {
    }

    public static NetworkConfig getInstance() {
        if (networkConfigInstance == null) {
            networkConfigInstance = new NetworkConfig();
            return networkConfigInstance;
        } else {
            return networkConfigInstance;
        }
    }

    @SuppressLint("DefaultLocale")
    public String getDeviceIpAddress(Context context) {
        if (deviceIpAddress == null) {
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
            } else return null;
        } else return deviceIpAddress;
    }
}
