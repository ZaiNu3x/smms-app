package group.intelliboys.smms.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import group.intelliboys.smms.R;

public class MonitoringFragment extends Fragment {

    private TextView speedTextView, statusTextView;
    private LocationManager locationManager;
    private MediaPlayer mediaPlayer;
    private static final int SPEED_LIMIT = 3; // Speed limit in km/h

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_monitoring, container, false);


        speedTextView = rootView.findViewById(R.id.speedTextView);
        statusTextView = rootView.findViewById(R.id.statusTextView);


        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alert_sound1);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return rootView;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

        return rootView;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                float speed = location.getSpeed() * 3.6f; // Convert m/s to km/h
                speedTextView.setText("Speed: " + String.format("%.2f", speed) + " km/h");

                // Check for over-speeding
                if (speed > SPEED_LIMIT) {
                    statusTextView.setText("Status: Over Speeding!");
                    Toast.makeText(getActivity(), "You're over-speeding!", Toast.LENGTH_SHORT).show();

                    // Play the alert sound if not already playing
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                } else {
                    statusTextView.setText("Status: Normal");
                    // Stop the sound if the speed is under the limit
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
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
    }
}
