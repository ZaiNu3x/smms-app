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

    public static NetworkConfig getInstance() {
        if (networkConfigInstance == null) {
            networkConfigInstance = new NetworkConfig();
        }
        return networkConfigInstance;
    }

    @SuppressLint("DefaultLocale")
    public String getDeviceIpAddress(Context context) {
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    public String getNetworkIpAddress(Context context) {
        String deviceIpAddress = getDeviceIpAddress(context);

        if (deviceIpAddress != null && !deviceIpAddress.isEmpty()) {
            String[] addresses = deviceIpAddress.split("\\.");
            StringBuilder builder = new StringBuilder();

            builder.append(addresses[0]).append(".")
                    .append(addresses[1]).append(".")
                    .append(addresses[2]).append(".");

            return new String(builder);
        } else return null;
    }

    public String getServerIpAddress(Context context) {
        String networkIpAddress = getNetworkIpAddress(context);

        if (networkIpAddress != null && !networkIpAddress.isEmpty()) {
            String[] addresses = networkIpAddress.split("\\.");
            StringBuilder builder = new StringBuilder();

            builder.append("https://").append(addresses[0]).append(".")
                    .append(addresses[1]).append(".")
                    .append(addresses[2]).append(".254:443");

            return new String(builder);
        } else return null;
    }
}
