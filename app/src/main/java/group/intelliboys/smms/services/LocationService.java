package group.intelliboys.smms.services;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import group.intelliboys.smms.utils.ContextHolder;

public class LocationService {
    private static LocationService instance;
    private final FusedLocationProviderClient locationProviderClient;

    private LocationService() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(ContextHolder.getInstance()
                .getContext());
    }

    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }

        return instance;
    }

    public FusedLocationProviderClient getLocationProviderClient() {
        return locationProviderClient;
    }
}
