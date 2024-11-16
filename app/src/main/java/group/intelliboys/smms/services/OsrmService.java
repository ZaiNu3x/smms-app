package group.intelliboys.smms.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.fragments.dashboard_menu.HomeFragment;
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
    }

    public void getFullAddressOnKeywordsInPointA(String value) {
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
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.body() != null) {
                        String body = response.body().string();
                        List<Map<String, Object>> data = ObjectMapper.convertJsonToListOfMap(body);
                        String displayName = (String) data.get(0).get("display_name");
                        float lat = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lat")));
                        float lon = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lon")));
                        GeoPoint geoPoint = new GeoPoint(lat, lon);

                        activity.runOnUiThread(() -> {
                            homeFragment.getPointA().setText(displayName);
                            homeFragment.setMarkerA(geoPoint);
                            homeFragment.getViewModel().setMarkerACoordinates(geoPoint);
                        });
                    }
                }
            });
        }
    }

    public void getFullAddressOnKeywordsInPointB(String value) {
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
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.body() != null) {
                        String body = response.body().string();
                        List<Map<String, Object>> data = ObjectMapper.convertJsonToListOfMap(body);
                        String displayName = (String) data.get(0).get("display_name");
                        float lat = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lat")));
                        float lon = Float.parseFloat((String) Objects.requireNonNull(data.get(0).get("lon")));
                        GeoPoint geoPoint = new GeoPoint(lat, lon);

                        activity.runOnUiThread(() -> {
                            homeFragment.getPointB().setText(displayName);
                            homeFragment.setMarkerB(geoPoint);
                            homeFragment.getViewModel().setMarkerBCoordinates(geoPoint);
                        });
                    }
                }
            });
        }
    }

    public void getRouteFromPointAToPointB(GeoPoint pointA, GeoPoint pointB) {
        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) fragment;
            Activity activity = homeFragment.requireActivity();

            final String API = "https://routing.openstreetmap.de/routed-car/route/v1/driving/" +
                    "121.04677955852198,14.5064614;121.05535712896216,14.4912287?" +
                    "overview=false&alternatives=true&steps=true";

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

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.body() != null) {
                        String body = response.body().string();
                        Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                        Log.i("", data.toString());
                    }
                }
            });
        }
    }
}
