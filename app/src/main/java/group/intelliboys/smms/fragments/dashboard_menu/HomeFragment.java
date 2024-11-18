package group.intelliboys.smms.fragments.dashboard_menu;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.maps.android.PolyUtil;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group.intelliboys.smms.BuildConfig;
import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.dashboard.HomeActivity;
import group.intelliboys.smms.fragments.driving_mode.DrivingModeFragment;
import group.intelliboys.smms.models.data.view_models.HomeFragmentViewModel;
import group.intelliboys.smms.services.OsrmService;
import lombok.Getter;

@Getter
public class HomeFragment extends Fragment {
    private MapView mapView;
    private AutoCompleteTextView pointA;
    private AutoCompleteTextView pointB;
    private CircleImageView showMyLocationBtn;
    private CircleImageView navContainerBtn;
    private ConstraintLayout navContainer;
    private Button startDrivingBtn;
    private CircleImageView drivingBtn;
    private Marker markerA;
    private Marker markerB;
    private Marker myLocation;
    private Polyline routeLine;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_REQUEST_CODE = 1;
    private HomeFragmentViewModel viewModel;
    private OsrmService osrmService;
    private ConstraintLayout routeInfoContainer;
    private TextView distanceInKmLbl;
    private TextView durationTxtLbl;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView = view.findViewById(R.id.home_map);
        pointA = view.findViewById(R.id.startPoint);
        pointB = view.findViewById(R.id.endPoint);
        showMyLocationBtn = view.findViewById(R.id.show_location_btn);
        navContainerBtn = view.findViewById(R.id.navigateBtn);
        navContainer = view.findViewById(R.id.navigateContainer);
        startDrivingBtn = view.findViewById(R.id.startDrivingBtn);
        drivingBtn = view.findViewById(R.id.drivingBtn);
        navContainer.setVisibility(View.INVISIBLE);
        navContainer.setAlpha(0f);
        routeInfoContainer = view.findViewById(R.id.routeInfoContainer);
        distanceInKmLbl = view.findViewById(R.id.distanceInKmLbl);
        durationTxtLbl = view.findViewById(R.id.durationLbl);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(5d);

        routeLine = new Polyline(mapView);

        // ============================= MAP CONFIG INITIALIZATION =============================
        viewModel = HomeFragmentViewModel.getInstance();

        if (viewModel.getZoomLevel() == 0) {
            mapView.getController().setZoom(6.5f);
        } else mapView.getController().setZoom(viewModel.getZoomLevel());

        if (viewModel.getMapCenter() == null) {
            mapView.getController().setCenter(new GeoPoint(12.8797f, 121.7740f));
        } else mapView.getController().setCenter(viewModel.getMapCenter());

        if (viewModel.getPointAValue() != null && !viewModel.getPointAValue().isEmpty()) {
            pointA.setText(viewModel.getPointAValue());
        }

        if (viewModel.getPointBValue() != null && !viewModel.getPointBValue().isEmpty()) {
            pointB.setText(viewModel.getPointBValue());
        }

        if (viewModel.getMarkerACoordinates() != null) {
            setMarkerA(viewModel.getMarkerACoordinates());
        }

        if (viewModel.getMarkerBCoordinates() != null) {
            setMarkerB(viewModel.getMarkerBCoordinates());
        }

        if (viewModel.isNavContainerVisible()) {
            navContainer.setVisibility(View.VISIBLE);
            navContainer.setAlpha(1f);
        }

        if (viewModel.getRoutePoints() != null) {
            drawRouteOnMap(viewModel.getRoutePoints());
        }

        if (viewModel.isRouteInfoContainerVisible()) {
            setDistanceAndDuration(viewModel.getDistance(), viewModel.getDuration());
            routeInfoContainer.setVisibility(View.VISIBLE);
        }

        // ============================= END OF MAP CONFIG INITIALIZATION =============================

        osrmService = new OsrmService(this);

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                viewModel.setMapCenter(mapView.getMapCenter());
                viewModel.setZoomLevel((float) mapView.getZoomLevelDouble());
                return false;
            }
        });

        GestureDetector gestureDetector = new GestureDetector(requireActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                GeoPoint geoPoint = (GeoPoint) mapView.getProjection()
                        .fromPixels((int) e.getX(), (int) e.getY());

                // =========================== POINT A ============================
                if (pointA.hasFocus()) {
                    setMarkerA(geoPoint);
                    osrmService.getFullAddressOnCoordinates(geoPoint, pointA);
                    viewModel.setMarkerACoordinates(geoPoint);

                    if (markerB != null) {
                        GeoPoint pointA = markerA.getPosition();
                        GeoPoint pointB = markerB.getPosition();

                        osrmService.getRouteFromPointAToPointB(pointA, pointB);
                    }
                }
                // =================================================================

                // =========================== POINT B ===========================
                if (pointB.hasFocus()) {
                    setMarkerB(geoPoint);
                    osrmService.getFullAddressOnCoordinates(geoPoint, pointB);
                    viewModel.setMarkerBCoordinates(geoPoint);

                    if (markerA != null) {
                        GeoPoint pointA = markerA.getPosition();
                        GeoPoint pointB = markerB.getPosition();

                        osrmService.getRouteFromPointAToPointB(pointA, pointB);
                    }
                }
                // =================================================================
            }
        });

        mapView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        showMyLocationBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

            LocationRequest locationRequest = new LocationRequest.Builder(3000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build();

            if (locationCallback == null) {
                locationCallback = new LocationCallback() {
                    private final HomeActivity homeActivity = (HomeActivity) HomeFragment.this.requireActivity();
                    private boolean isAnimated = true;

                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();

                        assert location != null;
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        if (pointA.hasFocus()) {
                            homeActivity.runOnUiThread(() -> {
                                mapView.getOverlays().remove(myLocation);
                                setMarkerA(geoPoint);
                                osrmService.getFullAddressOnCoordinates(geoPoint, pointA);

                                if (isAnimated) {
                                    mapView.getController()
                                            .animateTo(markerA.getPosition(), 17d, 3000L);
                                }

                                viewModel.setMarkerACoordinates(geoPoint);
                            });
                        } else if (pointB.hasFocus()) {
                            homeActivity.runOnUiThread(() -> {
                                mapView.getOverlays().remove(myLocation);
                                setMarkerB(geoPoint);
                                osrmService.getFullAddressOnCoordinates(geoPoint, pointB);

                                if (isAnimated) {
                                    mapView.getController()
                                            .animateTo(markerB.getPosition(), 17d, 3000L);
                                }

                                viewModel.setMarkerBCoordinates(geoPoint);
                            });
                        } else {
                            if (myLocation == null) {
                                myLocation = new Marker(mapView);
                            }

                            myLocation.setPosition(geoPoint);
                            myLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            mapView.getOverlays().remove(myLocation);
                            mapView.getOverlays().add(myLocation);

                            if (isAnimated) {
                                mapView.getController()
                                        .animateTo(myLocation.getPosition(), 17d, 3000L);
                            }
                            isAnimated = false;
                            mapView.invalidate();
                        }

                        Log.i("", geoPoint.toString());
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                };
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            if (myLocation != null) {
                mapView.getController()
                        .setCenter(myLocation.getPosition());
                mapView.getController().setZoom(18d);
            }
        });

        navContainerBtn.setOnClickListener(v -> {
            if (navContainer.getVisibility() == View.VISIBLE) {
                navContainer.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        navContainer.setVisibility(View.INVISIBLE);
                        viewModel.setNavContainerVisible(false);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
            } else {
                navContainer.animate().alpha(1f).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                        navContainer.setVisibility(View.VISIBLE);
                        viewModel.setNavContainerVisible(true);
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
            }
        });

        pointA.setOnLongClickListener(v -> {
            pointA.selectAll();
            return true;
        });

        pointA.setOnKeyListener((v, i, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                String keywords = pointA.getText().toString();
                osrmService.getFullAddressOnKeywordsInPointA(keywords);
                return true;
            }

            return false;
        });

        pointA.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 150;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable value) {
                handler.removeCallbacks(runnable);

                runnable = () -> {

                    if (value.length() == 0) {
                        viewModel.setPointAValue(null);
                        deleteMarkerA();
                    } else {
                        viewModel.setPointAValue(value.toString());
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        pointA.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                pointA.showDropDown();
            }
        });

        pointA.setOnItemClickListener((adapterView, v, i, l) -> {

        });

        pointB.setOnLongClickListener(v -> {
            pointB.selectAll();
            return true;
        });

        pointB.setOnKeyListener((v, i, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                String keywords = pointB.getText().toString();
                osrmService.getFullAddressOnKeywordsInPointB(keywords);
                return true;
            }

            return false;
        });

        pointB.setOnItemClickListener((adapterView, v, i, l) -> {

        });

        pointB.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 150;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable value) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    if (value.length() == 0) {
                        viewModel.setPointBValue(null);
                        deleteMarkerB();
                    } else {
                        viewModel.setPointBValue(value.toString());
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        drivingBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle("ENTER DRIVING MODE?")
                    .setMessage("Are you sure to enter driving mode?")
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, DrivingModeFragment.class, null).commit();
                    })
                    .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                        // CODE
                    }))
                    .show();
        });

        startDrivingBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle("ENTER DRIVING MODE?")
                    .setMessage("Are you sure to enter driving mode?")
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, DrivingModeFragment.class, null).commit();
                    })
                    .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                        // CODE
                    }))
                    .show();
        });

        return view;
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

    public void deleteMarkerA() {
        mapView.getOverlays().remove(markerA);
        mapView.getOverlays().remove(routeLine);
        markerA = null;
        mapView.invalidate();

        viewModel.setMarkerACoordinates(null);
        viewModel.setRoutePoints(null);
        showRouteInfoContainer();
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

    public void deleteMarkerB() {
        mapView.getOverlays().remove(markerB);
        mapView.getOverlays().remove(routeLine);
        markerB = null;
        mapView.invalidate();

        viewModel.setMarkerBCoordinates(null);
        viewModel.setRoutePoints(null);
        showRouteInfoContainer();
    }

    public void drawRouteOnMap(List<GeoPoint> geoPoints) {
        mapView.getOverlays().remove(routeLine);
        routeLine.setPoints(geoPoints);
        routeLine.setColor(Color.rgb(62, 108, 237));
        routeLine.setWidth(10);
        mapView.getOverlays().add(routeLine);
        viewModel.setRoutePoints(List.copyOf(geoPoints));
        showRouteInfoContainer();
        mapView.invalidate();
    }

    public void showRouteInfoContainer() {
        if (routeInfoContainer.getVisibility() == View.VISIBLE && (markerA == null || markerB == null)) {
            routeInfoContainer.animate().alpha(0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    routeInfoContainer.setVisibility(View.INVISIBLE);
                    viewModel.setDistance(0);
                    viewModel.setDuration(0);
                    viewModel.setRouteInfoContainerVisible(false);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
        } else {
            routeInfoContainer.animate().alpha(1f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {
                    routeInfoContainer.setVisibility(View.VISIBLE);
                    viewModel.setRouteInfoContainerVisible(true);
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void setDistanceAndDuration(double distance, double duration) {
        distanceInKmLbl.setText(distance + " Km");

        int hours = (int) duration;
        int minutes = (int) ((duration - hours) * 60);

        if (duration < 1) {
            durationTxtLbl.setText(minutes + " mins");
        } else if (duration > 2) {
            durationTxtLbl.setText(hours + "hrs");
        }

    }

    public List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> polyline = new ArrayList<>();
        List<com.google.android.gms.maps.model.LatLng> decoded = PolyUtil.decode(encoded);

        for (com.google.android.gms.maps.model.LatLng latLng : decoded) {
            polyline.add(new GeoPoint(latLng.latitude, latLng.longitude));
        }

        return polyline;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
