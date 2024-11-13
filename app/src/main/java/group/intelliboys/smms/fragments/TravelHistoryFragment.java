package group.intelliboys.smms.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private LinearLayout recordsContainer;
    private List<String> locationRecords = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long REFRESH_INTERVAL = 3000;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (isAdded() && getActivity() != null) {
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
        recordsContainer = view.findViewById(R.id.recordsContainer);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check location permissions
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            getLocation();
        }

        startAutoRefresh();

        return view;
    }

    private void getLocation() {
        if (isAdded() && requireActivity() != null &&
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null && isAdded()) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationRecord = getString(R.string.location_format, latitude, longitude);
                            String dateTime = getCurrentDateTime();


                            addLocationRecord(locationRecord, dateTime);
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
                if (isAdded()) {
                    getLocation();
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        }, REFRESH_INTERVAL);
    }

    private void addLocationRecord(String location, String dateTime) {
        View card = LayoutInflater.from(requireContext()).inflate(R.layout.card_layout, recordsContainer, false);

        TextView dateTextView = card.findViewById(R.id.dateTextView);
        TextView locationTextView = card.findViewById(R.id.locationTextView);
        TextView timeTextView = card.findViewById(R.id.timeTextView);

        dateTextView.setText("Date: " + dateTime.split(" ")[0]);
        timeTextView.setText("Time: " + dateTime.split(" ")[1]);
        locationTextView.setText("Location: " + location);

        recordsContainer.addView(card);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
