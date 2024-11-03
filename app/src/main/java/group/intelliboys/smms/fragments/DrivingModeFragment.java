package group.intelliboys.smms.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDateTime;
import java.util.UUID;

import group.intelliboys.smms.BuildConfig;
import group.intelliboys.smms.R;
import group.intelliboys.smms.models.data.StatusUpdate;
import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbStatusUpdateService;
import group.intelliboys.smms.services.local.LocalDbTravelHistoryService;

public class DrivingModeFragment extends Fragment implements SensorEventListener {
    private MapView mapView;
    private TextView speedTextView, statusTextView;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SPEED_LIMIT = 45;
    private ImageButton circleButton, circleButton1;
    private Marker myLocation;
    private LocalDbTravelHistoryService travelHistoryService;
    private LocalDbStatusUpdateService statusUpdateService;
    private String travelUUID;
    private TravelHistory travelHistory;
    private User loggedInUser;
    private Location lastLocation;
    private float corneringAngle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        View view = inflater.inflate(R.layout.fragment_driving_mode, container, false);

        mapView = view.findViewById(R.id.map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(5d);
        mapView.getController().setCenter(new GeoPoint(12.975775121410635d,
                121.79874219633962));
        mapView.getController().setZoom(6.5d);

        speedTextView = view.findViewById(R.id.speedTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        circleButton = view.findViewById(R.id.circle);
        circleButton1 = view.findViewById(R.id.circle1);
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alert_sound2);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        travelHistoryService = new LocalDbTravelHistoryService(requireActivity());
        statusUpdateService = new LocalDbStatusUpdateService(requireActivity());

        travelUUID = UUID.randomUUID().toString();
        loggedInUser = Utils.getInstance().getLoggedInUser().getUserModel().getValue();
        assert loggedInUser != null;
        Log.i("", loggedInUser.toString());

        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        requestLocationUpdates();
                    } else {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            requestLocationUpdates();
        }

        return view;
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        LocationRequest locationRequest = new LocationRequest.Builder(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                private boolean isAnimated = true;

                @SuppressLint("SetTextI18n")
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        int speedInKmh = (int) (location.getSpeed() * 3.6f);
                        lastLocation = location;

                        if (loggedInUser != null && travelHistory == null) {
                            String point = location.getLatitude() + "," + location.getLongitude()
                                    + "," + location.getAltitude();

                            travelHistory = TravelHistory.builder()
                                    .id(travelUUID)
                                    .userId(loggedInUser.getEmail())
                                    .startTime(LocalDateTime.now())
                                    .endTime(null)
                                    .startLatitude((float) location.getLatitude())
                                    .startLongitude((float) location.getLongitude())
                                    .startAltitude((float) location.getAltitude())
                                    .createdAt(LocalDateTime.now())
                                    .build();

                            travelHistoryService.addTravelHistory(travelHistory);
                        }

                        if (travelHistory != null && speedInKmh > 0) {
                            String id = UUID.randomUUID().toString();

                            StatusUpdate statusUpdate = StatusUpdate.builder()
                                    .id(id)
                                    .latitude(location.getLatitude())
                                    .longitude(location.getLongitude())
                                    .altitude(location.getAltitude())
                                    .corneringAngle(corneringAngle)
                                    .speed(speedInKmh)
                                    .direction("WEST")
                                    .createdAt(LocalDateTime.now())
                                    .isWearingHelmet(false)
                                    .travelHistoryId(travelHistory.getId())
                                    .build();

                            statusUpdateService.insertStatusUpdate(statusUpdate);
                        }

                        requireActivity().runOnUiThread(() -> {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                            if (myLocation == null) {
                                myLocation = new Marker(mapView);
                            } else {
                                myLocation.setPosition(geoPoint);
                                myLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                mapView.getOverlays().remove(myLocation);
                                mapView.getOverlays().add(myLocation);
                                mapView.invalidate();

                                if (isAnimated) {
                                    mapView.getController()
                                            .animateTo(myLocation.getPosition(), 18d, 3000L);
                                }
                                isAnimated = false;
                            }
                            speedTextView.setText("Speed: " + speedInKmh + " Km/h");

                            if (speedInKmh > SPEED_LIMIT) {
                                statusTextView.setText(R.string.status_over_speeding);
                                Toast.makeText(getActivity(), "You're over-speeding!", Toast.LENGTH_SHORT).show();
                                if (mediaPlayer != null) {
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.setLooping(true);
                                        mediaPlayer.start();
                                    }
                                }
                            } else {
                                statusTextView.setText(R.string.status_normal);
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.setLooping(false);
                                    mediaPlayer.pause();
                                }
                            }
                        });
                    }
                }
            };
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
        fusedLocationClient.removeLocationUpdates(locationCallback);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }


        travelHistory.setEndLatitude((float) lastLocation.getLatitude());
        travelHistory.setEndLongitude((float) lastLocation.getLongitude());
        travelHistory.setEndAltitude((float) lastLocation.getAltitude());
        travelHistory.setEndTime(LocalDateTime.now());
        travelHistoryService.updateTravelHistoryById(travelHistory);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            corneringAngle = x;

            if (x < -8) {
                circleButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                statusTextView.setText(R.string.agrresive_cornering);
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                }
            } else if (x > 8) {
                circleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                statusTextView.setText(R.string.agrresive_cornering);
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                }
            } else {
                circleButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                circleButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));

                statusTextView.setText(R.string.status_normal);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.setLooping(false);
                    mediaPlayer.pause();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
