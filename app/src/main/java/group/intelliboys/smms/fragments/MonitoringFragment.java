package group.intelliboys.smms.fragments;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import group.intelliboys.smms.R;

public class MonitoringFragment extends Fragment implements LocationListener {

    private TextView locationText, speedText;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monitoring, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        locationText = view.findViewById(R.id.locationText);
        speedText = view.findViewById(R.id.speedText);

        // Initialize LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);

        // Request location updates
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update location and speed data
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float speed = location.getSpeed(); // Speed in meters/second

        // Convert speed to km/h
        float speedKmh = speed * 3.6f;

        // Display the location and speed
        locationText.setText("LOCATION: " + latitude + ", " + longitude);
        speedText.setText("SPEED: " + speedKmh + " km/h");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop location updates to prevent memory leaks
        locationManager.removeUpdates(this);
    }
}
