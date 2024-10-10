package group.intelliboys.smms.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import group.intelliboys.smms.R;

public class TravelHistoryFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    private TextView dateTimeTextView;
    private List<String> locationRecords = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long REFRESH_INTERVAL = 3000;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (getActivity() != null) {
                    boolean allPermissionsGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        getLocation();
                    } else {
                        Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_history, container, false);
        locationTextView = view.findViewById(R.id.locationTextView);
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            getLocation();
        }

        startAutoRefresh();

        return view;

    }

    private void getLocation() {
        if (requireActivity() != null && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationRecord = getString(R.string.location_format, latitude, longitude) + " - " + getCurrentDateTime();
                            locationRecords.add(locationRecord);
                            updateLocationRecords();
                        } else {
                            Toast.makeText(requireActivity(), "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                getLocation();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }, REFRESH_INTERVAL);
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        dateTimeTextView.setText(currentDateAndTime);
    }

    private void updateLocationRecords() {
        StringBuilder records = new StringBuilder();
        for (String record : locationRecords) {
            records.append(record).append("\n");
        }
        locationTextView.setText(records.toString());
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}

