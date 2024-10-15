package group.intelliboys.smms.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import group.intelliboys.smms.R;

public class MonitoringFragment extends Fragment implements SensorEventListener {

    private TextView speedTextView, statusTextView;
    private LocationManager locationManager;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SPEED_LIMIT = 3;
    private static final int LOCATION_UPDATE_INTERVAL = 500;
    private static final int LOCATION_UPDATE_DISTANCE = 0;
    private ImageButton circleButton, circleButton1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monitoring, container, false);

        speedTextView = rootView.findViewById(R.id.speedTextView);
        statusTextView = rootView.findViewById(R.id.statusTextView);
        circleButton = rootView.findViewById(R.id.circle);
        circleButton1 = rootView.findViewById(R.id.circle1);

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alert_sound2);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "Please enable GPS", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        return rootView;
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        private float lastSpeed = 0;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                float speed = location.getSpeed() * 3.6f; // Convert m/s to km/h
                Log.d("MonitoringFragment", "Location received: Lat=" + location.getLatitude() + ", Lon=" + location.getLongitude());
                Log.d("MonitoringFragment", "Speed: " + speed + " km/h");

                lastSpeed = speed;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        speedTextView.setText(getString(R.string.speed_text, speed));
                        Log.d("MonitoringFragment", "Speed Text Updated: " + speed + " km/h");

                        if (speed > SPEED_LIMIT) {
                            statusTextView.setText(R.string.status_over_speeding);
                            Toast.makeText(getActivity(), "You're over-speeding!", Toast.LENGTH_SHORT).show();

                            if (mediaPlayer != null) {
                                if (!mediaPlayer.isPlaying()) {
                                    mediaPlayer.setLooping(true); // Set to loop
                                    mediaPlayer.start(); // Start the alert sound
                                }
                            }
                        } else {
                            statusTextView.setText(R.string.status_normal);

                            // Stop the sound only when speed drops below the limit
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.setLooping(false); // Stop looping
                                mediaPlayer.pause(); // Pause the sound
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(getActivity(), "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
