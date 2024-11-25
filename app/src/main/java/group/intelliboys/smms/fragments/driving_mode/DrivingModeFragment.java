package group.intelliboys.smms.fragments.driving_mode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import group.intelliboys.smms.R;
import group.intelliboys.smms.components.ui.CustomMapView;
import group.intelliboys.smms.models.data.view_models.HomeFragmentViewModel;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.orm.repository.TravelHistoryRepository;
import group.intelliboys.smms.orm.repository.TravelStatusUpdateRepository;
import group.intelliboys.smms.security.SecurityContextHolder;
import group.intelliboys.smms.services.LocationService;
import group.intelliboys.smms.utils.Commons;

public class DrivingModeFragment extends Fragment implements SensorEventListener {
    private CustomMapView mapView;
    private ImageButton leftWarningIcon;
    private ImageButton rightWarningIcon;
    private TextView speedTxtView;
    private TextView statusTxtView;
    private TextView speedLimitTxtView;
    private TextView roadTypeTxtView;
    private TextView remDistanceTxtView;

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final int SPEED_LIMIT = 45;

    private HomeFragmentViewModel viewModel;
    private Marker markerA;
    private Marker markerB;
    private Polyline routeLine;

    private float ridingAngle;

    private static final int LOCATION_REQUEST_CODE = 1;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;

    private User user;
    private TravelHistory travelEntry;
    private TravelHistoryRepository travelHistoryRepository;
    private TravelStatusUpdateRepository travelStatusUpdateRepository;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driving_mode, container, false);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapView = view.findViewById(R.id.drivingModeMapView);
        leftWarningIcon = view.findViewById(R.id.leftWarningIcon);
        rightWarningIcon = view.findViewById(R.id.rightWarningIcon);
        speedTxtView = view.findViewById(R.id.speedTextView);
        statusTxtView = view.findViewById(R.id.statusTextView);
        remDistanceTxtView = view.findViewById(R.id.remainingDistance);
        roadTypeTxtView = view.findViewById(R.id.roadType);
        speedLimitTxtView = view.findViewById(R.id.speedLimit);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(5d);

        viewModel = HomeFragmentViewModel.getInstance();
        routeLine = new Polyline(mapView);

        if (viewModel.getZoomLevel() == 0) {
            mapView.getController().setZoom(6.5f);
        } else mapView.getController().setZoom(viewModel.getZoomLevel());

        if (viewModel.getMapCenter() == null) {
            mapView.getController().setCenter(new GeoPoint(12.8797f, 121.7740f));
        } else mapView.getController().setCenter(viewModel.getMapCenter());

        if (viewModel.getMarkerACoordinates() != null) {
            setMarkerA(viewModel.getMarkerACoordinates());
        }

        if (viewModel.getMarkerBCoordinates() != null) {
            setMarkerB(viewModel.getMarkerBCoordinates());
        }

        if (viewModel.getRoutePoints() != null) {
            drawRouteOnMap(viewModel.getRoutePoints());
        }

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alert_sound2);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        user = SecurityContextHolder.getInstance().getAuthenticatedUser();
        travelHistoryRepository = new TravelHistoryRepository();
        travelStatusUpdateRepository = new TravelStatusUpdateRepository();

        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        requestLocationUpdates();
                    } else {
                        Commons.toastMessage(getContext(), "Location permission denied");
                    }
                });

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        requestLocationUpdates();
        return view;
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        locationProviderClient = LocationService.getInstance().getLocationProviderClient();

        if (locationRequest == null) {
            locationRequest = new LocationRequest.Builder(3000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build();
        }

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        if (travelEntry == null) {
                            travelEntry = new TravelHistory();
                            travelEntry.setTravelHistoryId(UUID.randomUUID().toString());
                            travelEntry.setStartTime(LocalDateTime.now());
                            travelEntry.setStartLatitude((float) location.getLatitude());
                            travelEntry.setStartLongitude((float) location.getLongitude());
                            travelEntry.setStartAltitude((float) location.getAltitude());
                            travelEntry.setCreatedAt(LocalDateTime.now());
                            travelEntry.setUserId(user.getEmail());
                            travelHistoryRepository.insertTravelHistory(travelEntry);
                            Log.i("", "Travel Entry Inserted!");
                        }

                        TravelStatusUpdate statusUpdate = TravelStatusUpdate.builder()
                                .travelStatusUpdateId(UUID.randomUUID().toString())
                                .travelHistoryId(travelEntry.getTravelHistoryId())
                                .latitude((float) location.getLatitude())
                                .longitude((float) location.getLongitude())
                                .altitude((float) location.getAltitude())
                                .ridingAngle(ridingAngle)
                                .speedInKmh((int) location.getSpeed())
                                .createdAt(LocalDateTime.now())
                                .build();

                        travelStatusUpdateRepository.insertTravelStatusUpdate(statusUpdate);
                        Log.i("", "Travel Status Inserted!");
                        lastLocation = location;
                    }
                }
            };
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    public void playWarningSound() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
        }
    }

    public void setMarkerA(GeoPoint geoPoint) {
        if (markerA == null) {
            markerA = new Marker(mapView);
        }

        mapView.getOverlays().remove(markerA);
        mapView.getOverlays().remove(routeLine);
        markerA.setPosition(geoPoint);
        markerA.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
        Drawable icon = getResources().getDrawable(R.drawable.ic_start_pin);
        markerA.setIcon(icon);
        mapView.getOverlays().add(markerA);
        mapView.invalidate();
    }

    public void setMarkerB(GeoPoint geoPoint) {
        if (markerB == null) {
            markerB = new Marker(mapView);
        }

        mapView.getOverlays().remove(markerB);
        mapView.getOverlays().remove(routeLine);
        markerB.setPosition(geoPoint);
        markerB.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
        Drawable icon = getResources().getDrawable(R.drawable.ic_end_pin);
        markerB.setIcon(icon);
        mapView.getOverlays().add(markerB);
        mapView.invalidate();
    }

    public void drawRouteOnMap(List<GeoPoint> geoPoints) {
        mapView.removeRoute(routeLine);
        routeLine.setPoints(geoPoints);
        routeLine.setColor(Color.rgb(62, 108, 237));
        routeLine.setWidth(10);
        mapView.drawRoute(routeLine);
        mapView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach();

        if (travelEntry != null) {
            travelEntry.setEndTime(LocalDateTime.now());
            travelEntry.setEndLatitude((float) lastLocation.getLatitude());
            travelEntry.setEndLongitude((float) lastLocation.getLongitude());
            travelEntry.setEndAltitude((float) lastLocation.getAltitude());
            travelHistoryRepository.updateTravelHistory(travelEntry);
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // CODES

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ridingAngle = sensorEvent.values[0];
            Log.i("", "X: " + ridingAngle);

            // LEFT LEANING
            if (ridingAngle >= 8) {
                playWarningSound();
                leftWarningIcon.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                statusTxtView.setText(R.string.agrresive_cornering);
            }

            // RIGHT LEANING
            else if (ridingAngle <= -8) {
                playWarningSound();
                rightWarningIcon.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                statusTxtView.setText(R.string.agrresive_cornering);
            }

            // NEUTRAL
            else {
                leftWarningIcon.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                rightWarningIcon.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                statusTxtView.setText(R.string.status_normal);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // CODES
    }
}
