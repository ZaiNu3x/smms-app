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
    public String getDeviceIpAddress() {
        Context context = Utils.getInstance().getApplicationContext();

        if (deviceIpAddress == null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                deviceIpAddress = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));
                return deviceIpAddress;
            } else return null;
        } else return deviceIpAddress;
    }

    public String getServerIpAddress() {
        Context context = Utils.getInstance().getApplicationContext();

        if (deviceIpAddress == null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                @SuppressLint("DefaultLocale")
                String[] ipSubParts = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff)).split("\\.");

                return "https://192.168." + ipSubParts[2] + ".254:443";
            } else return null;
        } else return deviceIpAddress;
    }
}
