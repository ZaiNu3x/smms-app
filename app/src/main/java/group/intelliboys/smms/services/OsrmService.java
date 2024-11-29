package group.intelliboys.smms.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.fragments.dashboard_menu.HomeFragment;
import group.intelliboys.smms.orm.data.SearchedLocation;
import group.intelliboys.smms.orm.repository.SearchedLocationRepository;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsrmService {
    private Fragment fragment;
    private OkHttpClient okHttpClient;

    public OsrmService(Fragment fragment) {
        this.fragment = fragment;
        okHttpClient = new OkHttpClient();

        final String URL = "https://nominatim.openstreetmap.org/search?q=Taguig%20City%20University&limit=5&format=json&addressdetails=1";

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Referer", "https://map.project-osrm.org/")
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // CODE
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // CODE
            }
        });
    }

    public void getFullAddressOnCoordinates(GeoPoint coordinates, EditText field) {
        try {
            if (fragment instanceof HomeFragment) {
                HomeFragment homeFragment = (HomeFragment) fragment;
                Activity activity = homeFragment.requireActivity();

                final String API = "https://nominatim.openstreetmap.org/reverse?lat=" + coordinates.getLatitude() + "&lon=" +
                        coordinates.getLongitude() + "&zoom=18&addressdetails=1&format=json";

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Referer", "https://map.project-osrm.org/")
                        .get()
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        float lat = (float) coordinates.getLatitude();
                        float lon = (float) coordinates.getLongitude();

                        activity.runOnUiThread(() -> {
                            field.setText(lat + ", " + lon);
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                            String displayName = (String) data.get("display_name");

                            activity.runOnUiThread(() -> {
                                field.setText(displayName);
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.i("", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void getFullAddressOnKeywordsInPointA(String value) {
        try {
            if (fragment instanceof HomeFragment) {
                HomeFragment homeFragment = (HomeFragment) fragment;
                Activity activity = homeFragment.requireActivity();

                String keywords = value.replaceAll("\\s", "%20");
                final String API = "https://nominatim.openstreetmap.org/search?q=" + keywords + "&limit=5&format=json&addressdetails=1";

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Referer", "https://map.project-osrm.org/")
                        .get()
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    private SearchedLocationRepository repository = new SearchedLocationRepository();

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("", Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            if (response.body() != null) {
                                String body = response.body().string();
                                List<Map<String, Object>> data = ObjectMapper.convertJsonToListOfMap(body);
                                Log.i("", "Data Length: " + data.size());

                                if (data.size() == 1) {
                                    SearchedLocation location = SearchedLocation.builder()
                                            .displayName((String) data.get(0).get("display_name"))
                                            .latitude(Float.parseFloat((String) data.get(0).get("lat")))
                                            .longitude(Float.parseFloat((String) data.get(0).get("lon")))
                                            .build();

                                    repository.insertSearchedLocation(location);
                                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                                    activity.runOnUiThread(() -> {
                                        homeFragment.getPointA().setText(location.getDisplayName());
                                        homeFragment.setMarkerA(geoPoint);
                                        homeFragment.getViewModel().setMarkerACoordinates(geoPoint);

                                        if (homeFragment.getMarkerA() != null && homeFragment.getMarkerB() != null) {
                                            GeoPoint pointA = homeFragment.getMarkerA().getPosition();
                                            GeoPoint pointB = homeFragment.getMarkerB().getPosition();
                                            getRouteFromPointAToPointB(pointA, pointB);
                                        }
                                    });
                                } else if (data.size() > 1) {
                                    List<SearchedLocation> locations = new ArrayList<>();

                                    data.forEach(o -> {
                                        SearchedLocation location = SearchedLocation.builder()
                                                .displayName((String) o.get("display_name"))
                                                .latitude(Float.parseFloat((String) o.get("lat")))
                                                .longitude(Float.parseFloat((String) o.get("lon")))
                                                .build();

                                        locations.add(location);
                                        repository.insertSearchedLocation(location);
                                    });

                                    Log.i("", locations.toString());

                                    activity.runOnUiThread(() -> {
                                        ArrayAdapter<SearchedLocation> listOfResult = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, locations);
                                        homeFragment.getPointA().setAdapter(listOfResult);
                                        homeFragment.getPointA().showDropDown();
                                    });

                                } else {
                                    activity.runOnUiThread(() -> {
                                        Commons.toastMessage(activity, "NOT FOUND!");
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.i("", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.i("", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void getFullAddressOnKeywordsInPointB(String value) {
        try {
            if (fragment instanceof HomeFragment) {
                HomeFragment homeFragment = (HomeFragment) fragment;
                Activity activity = homeFragment.requireActivity();

                String keywords = value.replaceAll("\\s", "%20");
                final String API = "https://nominatim.openstreetmap.org/search?q=" + keywords + "&limit=5&format=json&addressdetails=1";

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Referer", "https://map.project-osrm.org/")
                        .get()
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    private SearchedLocationRepository repository = new SearchedLocationRepository();

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("", Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            if (response.body() != null) {
                                String body = response.body().string();
                                List<Map<String, Object>> data = ObjectMapper.convertJsonToListOfMap(body);

                                if (data.size() == 1) {
                                    String displayName = (String) data.get(0).get("display_name");
                                    float lat = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lat")));
                                    float lon = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lon")));
                                    GeoPoint geoPoint = new GeoPoint(lat, lon);

                                    activity.runOnUiThread(() -> {
                                        homeFragment.getPointB().setText(displayName);
                                        homeFragment.setMarkerB(geoPoint);
                                        homeFragment.getViewModel().setMarkerBCoordinates(geoPoint);

                                        if (homeFragment.getMarkerA() != null && homeFragment.getMarkerB() != null) {
                                            GeoPoint pointA = homeFragment.getMarkerA().getPosition();
                                            GeoPoint pointB = homeFragment.getMarkerB().getPosition();
                                            getRouteFromPointAToPointB(pointA, pointB);
                                        }
                                    });
                                } else if (data.size() > 1) {
                                    List<SearchedLocation> locations = new ArrayList<>();

                                    data.forEach(o -> {
                                        SearchedLocation location = SearchedLocation.builder()
                                                .displayName((String) o.get("display_name"))
                                                .latitude(Float.parseFloat((String) o.get("lat")))
                                                .longitude(Float.parseFloat((String) o.get("lon")))
                                                .build();

                                        locations.add(location);
                                        repository.insertSearchedLocation(location);
                                    });

                                    Log.i("", locations.toString());

                                    activity.runOnUiThread(() -> {
                                        ArrayAdapter<SearchedLocation> listOfResult = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, locations);
                                        homeFragment.getPointB().setAdapter(listOfResult);
                                        homeFragment.getPointB().showDropDown();
                                    });
                                } else {
                                    activity.runOnUiThread(() -> {
                                        Commons.toastMessage(activity, "NOT FOUND!");
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.i("", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.i("", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void getRouteFromPointAToPointB(GeoPoint pointA, GeoPoint pointB) {
        try {
            if (fragment instanceof HomeFragment) {
                HomeFragment homeFragment = (HomeFragment) fragment;
                Activity activity = homeFragment.requireActivity();

                final String API = "https://routing.openstreetmap.de/routed-car/route/v1/driving/" + pointA.getLongitude() +
                        "," + pointA.getLatitude() + ";" + pointB.getLongitude() + "," + pointB.getLatitude() +
                        "?overview=false&alternatives=true&steps=true&annotations=speed";

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Referer", "https://map.project-osrm.org/")
                        .get()
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("", Objects.requireNonNull(e.getMessage()));
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                            try {
                                JSONObject routeData = new JSONObject(data);
                                JSONArray routes = routeData.getJSONArray("routes");
                                JSONObject route = routes.getJSONObject(0);

                                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                double distanceInKm = Double.parseDouble(decimalFormat.format(route
                                        .getDouble("distance") / 1000));
                                double duration = Double.parseDouble(decimalFormat.format(route
                                        .getDouble("duration") / 3600));

                                homeFragment.setDistanceAndDuration(distanceInKm, duration);

                                JSONArray legs = route.getJSONArray("legs");
                                List<GeoPoint> geoPoints = new ArrayList<>();

                                for (int i = 0; i < legs.length(); i++) {
                                    JSONObject leg = legs.getJSONObject(i);
                                    JSONArray steps = leg.getJSONArray("steps");

                                    for (int j = 0; j < steps.length(); j++) {
                                        JSONObject step = steps.getJSONObject(j);
                                        String encodedGeometry = step.getString("geometry");

                                        List<GeoPoint> points = homeFragment.decodePolyline(encodedGeometry);
                                        geoPoints.addAll(points);
                                    }
                                }

                                activity.runOnUiThread(() -> {
                                    homeFragment.drawRouteOnMap(geoPoints);
                                    homeFragment.getMapView().centerRouteOnMap(geoPoints);
                                });

                                homeFragment.getViewModel().setDistance(distanceInKm);
                                homeFragment.getViewModel().setDuration(duration);
                            } catch (Exception e) {
                                Log.i("", Objects.requireNonNull(e.getMessage()));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.i("", Objects.requireNonNull(e.getMessage()));
        }
    }
}
