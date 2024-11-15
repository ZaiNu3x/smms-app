package group.intelliboys.smms.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.utils.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsrmService {
    private Activity activity;
    private OkHttpClient okHttpClient;

    public OsrmService(Activity activity) {
        this.activity = activity;
        okHttpClient = new OkHttpClient();
    }

    public void getFullAddressOnCoordinates(GeoPoint coordinates, MapView mapView, EditText field) {
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
                field.setText(lat + ", " + lon);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String body = response.body().string();
                    Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                    String displayName = (String) data.get("display_name");
                    field.setText(displayName);
                }
            }
        });
    }

    public void getFullAddressOnKeywords(String value, MapView mapView, EditText field) {
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
                    float lat = (float) data.get(0).get("lat");
                    float lon = (float) data.get(0).get("lon");
                    field.setText(displayName);

                    GeoPoint geoPoint = new GeoPoint(lat, lon);
                }
            }
        });
    }
}
