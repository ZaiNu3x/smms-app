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
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.common.util.concurrent.ListenableFuture;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import group.intelliboys.smms.MainActivity;
import group.intelliboys.smms.R;
import group.intelliboys.smms.components.ui.CustomMapView;
import group.intelliboys.smms.models.data.view_models.HomeFragmentViewModel;
import group.intelliboys.smms.orm.data.AccidentHistory;
import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.orm.repository.AccidentHistoryRepository;
import group.intelliboys.smms.orm.repository.TravelHistoryRepository;
import group.intelliboys.smms.orm.repository.TravelStatusUpdateRepository;
import group.intelliboys.smms.security.SecurityContextHolder;
import group.intelliboys.smms.services.LocationService;
import group.intelliboys.smms.services.OsmService;
import group.intelliboys.smms.services.OsrmService;
import group.intelliboys.smms.services.TravelHistoryService;
import group.intelliboys.smms.services.TravelStatusUpdateService;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.Executor;
import group.intelliboys.smms.utils.ServerAPIs;
import group.intelliboys.smms.utils.converters.ImageConverter;
import lombok.Getter;
import lombok.Setter;

public class DrivingModeFragment extends Fragment implements SensorEventListener {
    private CustomMapView mapView;
    private ImageButton leftWarningIcon;
    private ImageButton rightWarningIcon;
    private TextView speedTxtView;
    private TextView statusTxtView;
    @Getter
    private TextView speedLimitTxtView;
    @Getter
    private TextView roadTypeTxtView;
    @Getter
    private TextView remDistanceTxtView;
    private Button backToDrivingMode;
    private OsrmService osrmService;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    @Setter
    private int speedLimit = 60;
    private HomeFragmentViewModel viewModel;
    private Marker myLocation;
    private boolean isFocusOnMyLocationMarker;
    private boolean isCountdownRunning;
    private Marker markerA;
    private Marker markerB;
    private Polyline routeLine;
    private float ridingAngle;
    private static final int LOCATION_REQUEST_CODE = 1;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback;
    private OsmService osmService;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private User user;
    private TravelHistory travelEntry;
    private TravelHistoryRepository travelHistoryRepository;
    private TravelStatusUpdate statusUpdate;
    private TravelStatusUpdateRepository travelStatusUpdateRepository;
    private AccidentHistoryRepository accidentHistoryRepository;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private ImageCapture imageCapture;
    private AccidentHistory accident;
    private byte[] frontCameraSnapImage;
    private byte[] backCameraSnapImage;

    private ServerAPIs serverAPIs;

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
        backToDrivingMode = view.findViewById(R.id.backToDrivingBtn);

        osrmService = new OsrmService(this);
        serverAPIs = new ServerAPIs(requireActivity());

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
        requestPermissionForCameraAccess();

        user = SecurityContextHolder.getInstance().getAuthenticatedUser();
        travelHistoryRepository = new TravelHistoryRepository();
        travelStatusUpdateRepository = new TravelStatusUpdateRepository();
        accidentHistoryRepository = new AccidentHistoryRepository();

        osmService = new OsmService(this);

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

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                isFocusOnMyLocationMarker = false;
                backToDrivingMode.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                isFocusOnMyLocationMarker = false;
                backToDrivingMode.setVisibility(View.VISIBLE);
                return false;
            }
        });

        setUpCamera();

        backToDrivingMode.setOnClickListener(v -> {
            isFocusOnMyLocationMarker = true;
            backToDrivingMode.setVisibility(View.INVISIBLE);
            Executors.newSingleThreadExecutor().submit(this::updateRoadInfo);
        });

        return view;
    }

    private void requestPermissionForCameraAccess() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Commons.toastMessage(requireActivity(), "Camera permission granted");
            setUpCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

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
                private final GeoPoint point = new GeoPoint(0f, 0f);

                @SuppressLint("SetTextI18n")
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

                            Executor.run(() -> {
                                travelHistoryRepository.insertTravelHistory(travelEntry);
                            });
                        }

                        point.setLatitude(location.getLatitude());
                        point.setLongitude(location.getLongitude());

                        if (myLocation == null) {
                            myLocation = new Marker(mapView);
                            mapView.addMarker(myLocation);
                            myLocation.setPosition(point);
                            mapView.getController()
                                    .animateTo(myLocation.getPosition(), 17d, 3000L);
                        }

                        myLocation.setPosition(point);
                        mapView.invalidate();

                        int speed = (int) (location.getSpeed() * 3.6f);

                        statusUpdate = TravelStatusUpdate.builder()
                                .travelStatusUpdateId(UUID.randomUUID().toString())
                                .travelHistoryId(travelEntry.getTravelHistoryId())
                                .latitude((float) location.getLatitude())
                                .longitude((float) location.getLongitude())
                                .altitude((float) location.getAltitude())
                                .ridingAngle(ridingAngle)
                                .speedInKmh(speed)
                                .createdAt(LocalDateTime.now())
                                .build();

                        travelStatusUpdateRepository.insertTravelStatusUpdate(statusUpdate);

                        requireActivity().runOnUiThread(() -> {
                            speedTxtView.setText("Speed: " + speed + " Km/hr");
                        });

                        lastLocation = location;
                    }
                }
            };
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateRoadInfo() {
        if (lastLocation != null) {
            GeoPoint geoPoint = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
            osmService.getRoadTypeAndSpeedLimit(geoPoint);

            if (markerB != null) {
                GeoPoint myPoint = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
                osrmService.getRemainingDistance(myPoint, markerB.getPosition());
            }

            Log.i("", "Road Info Updated!");
        }
    }

    public void onAccidentDetected() {
        isCountdownRunning = true;
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!(ridingAngle >= 8 || ridingAngle <= -8)) {
                    this.cancel();
                    isCountdownRunning = false;
                }
            }

            @Override
            public void onFinish() {
                if (ridingAngle >= 8 || ridingAngle <= -8) {
                    AccidentHistory accidentEntry = AccidentHistory.builder()
                            .accidentHistoryId(UUID.randomUUID().toString())
                            .travelStatusUpdateId(statusUpdate.getTravelStatusUpdateId())
                            .message("Accident Detected!")
                            .createdAt(LocalDateTime.now())
                            .build();

                    Executors.newSingleThreadExecutor().submit(() -> {
                        accidentHistoryRepository.insertAccidentHistory(accidentEntry);
                        accident = SerializationUtils.clone(accidentEntry);
                        TravelStatusUpdateService.getInstance()
                                .updateAddress(statusUpdate.getTravelStatusUpdateId());
                        takePhoto();
                    });
                }

                isCountdownRunning = false;
            }
        };
        countDownTimer.start();
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

    private int lens = CameraSelector.LENS_FACING_FRONT;

    public void setUpCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider
                .getInstance(requireActivity());

        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                startCamera(cameraProvider); // Call startCamera method here
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Camera initialization failed", e);
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void startCamera(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lens)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll();
            // Bind use cases to camera
            Camera camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageCapture);

            if (lens == CameraSelector.LENS_FACING_BACK) {
                takePhoto();
            }
        } catch (Exception e) {
            Log.e("CameraX", "Use case binding failed", e);
        }
    }

    private void takePhoto() {
        File photoFile = new File(requireActivity().getExternalFilesDir(null), LocalDateTime.now().toString() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputFileOptions, ContextCompat.getMainExecutor(requireActivity()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri() != null ?
                                outputFileResults.getSavedUri() : Uri.fromFile(photoFile);

                        String msg = "Photo capture succeeded: " + savedUri;
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
                        Log.d("CameraX", msg);

                        if (lens == CameraSelector.LENS_FACING_FRONT) {
                            frontCameraSnapImage = ImageConverter.compressImage(outputFileResultsToByteArray(outputFileResults), 50);
                            Log.i("", String.valueOf(frontCameraSnapImage.length));
                            lens = CameraSelector.LENS_FACING_BACK;
                        } else {
                            backCameraSnapImage = ImageConverter.compressImage(outputFileResultsToByteArray(outputFileResults), 50);
                            Log.i("", String.valueOf(backCameraSnapImage.length));
                            lens = CameraSelector.LENS_FACING_FRONT;
                        }

                        if (accident != null && frontCameraSnapImage != null && backCameraSnapImage != null) {
                            // UPDATE ACCIDENT ENTRY
                            Executor.run(() -> {
                                accident.setFrontCameraSnap(frontCameraSnapImage);
                                accident.setBackCameraSnap(backCameraSnapImage);

                                accidentHistoryRepository.updateAccidentHistory(accident);
                                Log.d("", "Accident Entry Updated!");

                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("accidentHistoryId", accident.getAccidentHistoryId());
                                    jsonObject.put("latitude", lastLocation.getLatitude());
                                    jsonObject.put("longitude", lastLocation.getLongitude());
                                    jsonObject.put("frontCameraSnap", Base64.getEncoder().encodeToString(accident.getFrontCameraSnap()));
                                    jsonObject.put("backCameraSnap", Base64.getEncoder().encodeToString(accident.getBackCameraSnap()));
                                    jsonObject.put("email", MainActivity.accidentEmailRecipient);
                                    jsonObject.put("createdAt", accident.getCreatedAt());
                                    Log.i("", String.valueOf(Log.d("", "Detected: " + jsonObject.toString())));

                                    serverAPIs.sendAccidentEntry(jsonObject);
                                } catch (Exception e) {
                                    Log.i("", Objects.requireNonNull(e.getMessage()));
                                }

                                // EXECUTE AFTER UPDATING ACCIDENT ENTRY
                                accident = null;
                                frontCameraSnapImage = null;
                                backCameraSnapImage = null;
                            });
                        }

                        setUpCamera();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        String msg = "Photo capture failed: " + exception.getMessage();
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
                        Log.e("CameraX", msg, exception);
                    }
                }
        );
    }

    public byte[] outputFileResultsToByteArray(ImageCapture.OutputFileResults outputFileResults) {
        Uri savedUri = outputFileResults.getSavedUri();
        File file = new File(savedUri.getPath());
        byte[] byteArray = null;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            byteArray = new byte[(int) file.length()];
            inputStream.read(byteArray);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (travelEntry != null) {
            Executor.run(() -> {
                travelEntry.setEndTime(LocalDateTime.now());
                travelEntry.setEndLatitude((float) lastLocation.getLatitude());
                travelEntry.setEndLongitude((float) lastLocation.getLongitude());
                travelEntry.setEndAltitude((float) lastLocation.getAltitude());
                travelHistoryRepository.updateTravelHistory(travelEntry);
                TravelHistoryService.getInstance().updateNullStartLocation();
                TravelHistoryService.getInstance().updateNullEndLocation();
            });
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        locationProviderClient.removeLocationUpdates(locationCallback);
        mapView.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        locationProviderClient.removeLocationUpdates(locationCallback);
        mapView.onPause();
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

                if (!isCountdownRunning) {
                    onAccidentDetected();
                }

            }

            // RIGHT LEANING
            else if (ridingAngle <= -8) {
                playWarningSound();
                rightWarningIcon.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                statusTxtView.setText(R.string.agrresive_cornering);

                if (!isCountdownRunning) {
                    onAccidentDetected();
                }
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
