package group.intelliboys.smms.services.remote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import group.intelliboys.smms.fragments.HomeFragment;
import group.intelliboys.smms.models.data.SearchedPlace;
import group.intelliboys.smms.models.view_models.HomeFragmentViewModel;
import group.intelliboys.smms.services.local.LocalDbSearchedPlaceService;
import lombok.Data;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OSRMService {
    private final OkHttpClient httpClient;
    private final Fragment fragment;
    public static Polyline routePolyline = new Polyline();

    public OSRMService(Fragment fragment) {
        httpClient = new OkHttpClient();
        this.fragment = fragment;
    }

    // GET ROUTE FROM POINT A TO POINT B
    public void getRoute(GeoPoint sp, GeoPoint ep) {
        String URL = "https://routing.openstreetmap.de/routed-car/route/v1/driving/" + sp.getLongitude() +
                "," + sp.getLatitude() + ";" + ep.getLongitude() + "," + ep.getLatitude() +
                "?overview=false&alternatives=true&steps=true";

        Log.i("", URL);

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();

                try {
                    JSONObject routeData = new JSONObject(body);
                    JSONArray routes = routeData.getJSONArray("routes");
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    List<GeoPoint> geoPoints = new ArrayList<>();

                    // Loop through each leg
                    for (int i = 0; i < legs.length(); i++) {
                        JSONObject leg = legs.getJSONObject(i);
                        JSONArray steps = leg.getJSONArray("steps");

                        // Loop through each step in the leg
                        for (int j = 0; j < steps.length(); j++) {
                            JSONObject step = steps.getJSONObject(j);
                            String encodedGeometry = step.getString("geometry");

                            // Decode the polyline geometry
                            List<GeoPoint> points = decodePolyline(encodedGeometry);
                            geoPoints.addAll(points);
                        }
                    }

                    HomeFragment homeFragment = (HomeFragment) fragment;
                    homeFragment.getMapView().getOverlays().remove(routePolyline);
                    routePolyline.setPoints(geoPoints);
                    routePolyline.setWidth(10);
                    routePolyline.setColor(Color.rgb(12, 147, 237));
                    homeFragment.getMapView().getOverlays().add(routePolyline);
                    homeFragment.getMapView().invalidate();

                    focusOnRoute(homeFragment.getMapView(), geoPoints);
                } catch (Exception e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }

    private void focusOnRoute(MapView mapView, List<GeoPoint> geoPoints) {
        if (geoPoints.isEmpty()) return;

        // Get the bounds of the route (min and max latitudes/longitudes)
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (GeoPoint point : geoPoints) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }

        // Create a bounding box from the min/max latitudes/longitudes
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);

        // Adjust the map to zoom and center on the bounding box
        mapView.zoomToBoundingBox(boundingBox, true);
    }

    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> polyline = new ArrayList<>();
        List<com.google.android.gms.maps.model.LatLng> decoded = PolyUtil.decode(encoded);

        for (com.google.android.gms.maps.model.LatLng latLng : decoded) {
            polyline.add(new GeoPoint(latLng.latitude, latLng.longitude));
        }

        return polyline;
    }

    // GET FULL ADDRESS NAME FROM ADDRESS KEYWORDS
    public void getAddressOnPointA(String address) {
        String addressKeywords = address.replaceAll(" ", "%20");
        String URL = "https://nominatim.openstreetmap.org/" +
                "search?q=" + addressKeywords + "&limit=5&format=json&addressdetails=1";

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .addHeader("Referer", "https://map.project-osrm.org/")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            private final HomeFragmentViewModel viewModel = HomeFragmentViewModel.getInstance();

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseBody = response.body().string();

                    HomeFragment homeFragment = (HomeFragment) fragment;
                    Activity activity = homeFragment.requireActivity();

                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, Object>[] data = mapper.readValue(responseBody, HashMap[].class);

                    if (data != null && data.length > 0) {
                        String value = (String) data[0].get("display_name");
                        String name = (String) data[0].get("name");
                        String displayName = (String) data[0].get("display_name");
                        double latitude = Double.parseDouble((String) Objects.requireNonNull(data[0].get("lat")));
                        double longitude = Double.parseDouble((String) Objects.requireNonNull(data[0].get("lon")));
                        String boundingBox = Objects.requireNonNull(data[0].get("boundingbox")).toString();

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.submit(() -> {
                            LocalDbSearchedPlaceService searchedPlaceService = new LocalDbSearchedPlaceService(homeFragment
                                    .requireActivity());

                            SearchedPlace place = SearchedPlace.builder()
                                    .name(name)
                                    .displayName(displayName)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .boundingBox(boundingBox)
                                    .build();

                            searchedPlaceService.insertSearchedPlace(place);
                        });
                        executorService.shutdown();

                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

                        activity.runOnUiThread(() -> {
                            homeFragment.getPointA().setText(value);
                            viewModel.setPointAValue(value);
                            homeFragment.setMarkerA(geoPoint);
                            homeFragment.showStartDrivingButton();

                            if (homeFragment.getMarkerB() != null) {
                                GeoPoint pointA = homeFragment.getMarkerA().getPosition();
                                GeoPoint pointB = homeFragment.getMarkerB().getPosition();
                                getRoute(pointA, pointB);
                            }
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "NOT FOUND!", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        });
    }

    // GET FULL ADDRESS NAME FROM ADDRESS KEYWORDS
    public void getAddressOnPointB(String address) {
        String addressKeywords = address.replaceAll(" ", "%20");
        String URL = "https://nominatim.openstreetmap.org/" +
                "search?q=" + addressKeywords + "&limit=5&format=json&addressdetails=1";

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .addHeader("Referer", "https://map.project-osrm.org/")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            private final HomeFragmentViewModel viewModel = HomeFragmentViewModel.getInstance();

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseBody = response.body().string();

                    HomeFragment homeFragment = (HomeFragment) fragment;
                    Activity activity = homeFragment.requireActivity();

                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, Object>[] data = mapper.readValue(responseBody, HashMap[].class);

                    if (data != null && data.length > 0) {
                        String value = (String) data[0].get("display_name");
                        String name = (String) data[0].get("name");
                        String displayName = (String) data[0].get("display_name");
                        double latitude = Double.parseDouble((String) Objects.requireNonNull(data[0].get("lat")));
                        double longitude = Double.parseDouble((String) Objects.requireNonNull(data[0].get("lon")));
                        String boundingBox = Objects.requireNonNull(data[0].get("boundingbox")).toString();

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.submit(() -> {
                            LocalDbSearchedPlaceService searchedPlaceService = new LocalDbSearchedPlaceService(homeFragment
                                    .requireActivity());

                            SearchedPlace place = SearchedPlace.builder()
                                    .name(name)
                                    .displayName(displayName)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .boundingBox(boundingBox)
                                    .build();

                            searchedPlaceService.insertSearchedPlace(place);
                        });
                        executorService.shutdown();

                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

                        activity.runOnUiThread(() -> {
                            homeFragment.getPointB().setText(value);
                            viewModel.setPointBValue(value);
                            homeFragment.setMarkerB(geoPoint);
                            homeFragment.showStartDrivingButton();

                            if (homeFragment.getMarkerA() != null) {
                                GeoPoint pointA = homeFragment.getMarkerA().getPosition();
                                GeoPoint pointB = homeFragment.getMarkerB().getPosition();
                                getRoute(pointA, pointB);
                            }
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "NOT FOUND!", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        });
    }

    // GET FULL ADDRESS NAME OF THE COORDINATES
    public void getAddress(double latitude, double longitude) {
        String URL = "https://nominatim.openstreetmap.org/reverse?" +
                "lat=" + latitude + "&lon=" + longitude + "&zoom=18&addressdetails=1&format=json";

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .addHeader("Referer", "https://map.project-osrm.org/")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            private final HomeFragmentViewModel viewModel = HomeFragmentViewModel.getInstance();

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Activity activity = fragment.requireActivity();

                activity.runOnUiThread(() -> {
                    HomeFragment homeFragment = (HomeFragment) fragment;

                    if (homeFragment.getPointA().hasFocus()) {
                        activity.runOnUiThread(() -> {
                            homeFragment.getPointA().setText(latitude + ", " + longitude);
                            homeFragment.getPointB().requestFocus();
                        });
                    } else if (homeFragment.getPointB().hasFocus()) {
                        activity.runOnUiThread(() -> {
                            homeFragment.getPointB().setText(latitude + ", " + longitude);
                        });
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseBody = response.body().string();

                    HomeFragment homeFragment = (HomeFragment) fragment;
                    Activity activity = homeFragment.requireActivity();

                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, Object> data = mapper.readValue(responseBody, HashMap.class);

                    String value = (String) data.get("display_name");

                    if (homeFragment.getPointA().hasFocus()) {
                        activity.runOnUiThread(() -> {
                            homeFragment.getPointA().setText(value);
                            viewModel.setPointAValue(value);
                            homeFragment.getPointB().requestFocus();
                        });
                    } else if (homeFragment.getPointB().hasFocus()) {
                        activity.runOnUiThread(() -> {
                            homeFragment.getPointB().setText(value);
                            viewModel.setPointBValue(value);
                        });
                    }
                }
            }
        });
    }
}
