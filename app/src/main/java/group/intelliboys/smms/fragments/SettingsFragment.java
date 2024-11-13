package group.intelliboys.smms.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import group.intelliboys.smms.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;

public class SettingsFragment extends Fragment implements SensorEventListener {

    private TextView tvLocationType;
    private TextView tvSpeedLimit;
    private TextView tvCurrentSpeed;
    private TextView tvWarningMessage;
    private LocationManager locationManager;
    private MapView mapView;
    private ImageButton circleButton;
    private ImageButton circleButton1;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final String OSRM_API_URL = "http://router.project-osrm.org/route/v1/driving/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tvLocationType = view.findViewById(R.id.tv_location_type);
        tvSpeedLimit = view.findViewById(R.id.tv_speed_limit);
        tvCurrentSpeed = view.findViewById(R.id.speedTextView);
        tvWarningMessage = view.findViewById(R.id.tv_warning_message);

        mapView = view.findViewById(R.id.home_map);
        GeoPoint startPoint = new GeoPoint(14.5995, 120.9842);
        IMapController mapController = mapView.getController();
        mapController.setCenter(startPoint);
        mapController.setZoom(12);


        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        circleButton = view.findViewById(R.id.circle);
        circleButton1 = view.findViewById(R.id.circle1);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Set initial location type and speed limit
        String locationType = "City and municipal roads, with light traffic";
        int speedLimit = getSpeedLimit(locationType);

        tvLocationType.setText("Location Type: " + locationType);
        tvSpeedLimit.setText("Speed Limit: " + speedLimit + " km/h");

        startLocationUpdates();

        return view;
    }

    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Please grant location permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateSpeed(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(@NonNull String provider) { }

        @Override
        public void onProviderDisabled(@NonNull String provider) { }
    };

    private void updateSpeed(Location location) {
        if (location != null) {
            float speed = location.getSpeed() * 3.6f;
            tvCurrentSpeed.setText("Current Speed: " + speed + " km/h");


            String locationType = tvLocationType.getText().toString();
            int speedLimit = getSpeedLimit(locationType);

            if (speed > speedLimit) {
                tvWarningMessage.setVisibility(View.VISIBLE);
                fetchRouteDetails(location);
            } else {
                tvWarningMessage.setVisibility(View.GONE);
            }
        }
    }

    private void fetchRouteDetails(Location location) {
        double currentLat = location.getLatitude();
        double currentLon = location.getLongitude();

        String url = OSRM_API_URL + currentLon + "," + currentLat + ";" + currentLon + "," + currentLat + "?overview=full"; // Simplified request for testing

        // Create a new thread for making the network request
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    double routeDuration = jsonObject.getJSONArray("routes")
                            .getJSONObject(0)
                            .getJSONArray("legs")
                            .getJSONObject(0)
                            .getDouble("duration");

                    getActivity().runOnUiThread(() -> {
                        // Update UI with the route details
                        Toast.makeText(getContext(), "Route Duration: " + routeDuration + " seconds", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching route data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private int getSpeedLimit(String locationType) {
        switch (locationType) {
            case "Expressways":
                return 100;
            case "National and provincial roads":
                return 80;
            case "Through streets or boulevards, clear of traffic":
                return 40;
            case "City and municipal roads, with light traffic":
                return 30;
            case "Crowded streets, school zones, roads where drivers must pass stationary vehicles":
                return 20;
            default:
                return 30; // Default to a city road limit if unknown
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        // Unregister the sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];


            if (x < -8) {
                circleButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else if (x > 8) {
                circleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                circleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                circleButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onResume() {
        super.onResume();
        // Register the sensor listener when the fragment is resumed
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the sensor listener when the fragment is paused
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
