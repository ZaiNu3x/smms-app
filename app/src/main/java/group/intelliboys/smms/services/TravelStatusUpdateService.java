package group.intelliboys.smms.services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.orm.data.TravelStatusUpdate;
import group.intelliboys.smms.orm.repository.TravelStatusUpdateRepository;
import group.intelliboys.smms.utils.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelStatusUpdateService {
    private static TravelStatusUpdateService instance;
    private final TravelStatusUpdateRepository travelStatusUpdateRepository;
    private final OkHttpClient httpClient;

    private TravelStatusUpdateService() {
        travelStatusUpdateRepository = new TravelStatusUpdateRepository();
        httpClient = new OkHttpClient();
    }

    public static TravelStatusUpdateService getInstance() {
        if (instance == null) {
            instance = new TravelStatusUpdateService();
        }

        return instance;
    }

    public void updateAddress(String id) {
        TravelStatusUpdate s = travelStatusUpdateRepository.getTravelStatusUpdatesWithNullAddress(id);

        final String API = "https://nominatim.openstreetmap.org/reverse?lat=" + s.getLatitude() + "&lon=" +
                s.getLongitude() + "&zoom=18&addressdetails=1&format=json";

        Request request = new Request.Builder()
                .url(API)
                .addHeader("Referer", "https://map.project-osrm.org/")
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String body = response.body().string();
                    Map<?, ?> map = ObjectMapper.convertJsonToMapObject(body);
                    String displayName = (String) map.get("display_name");
                    s.setAddress(displayName);
                    travelStatusUpdateRepository.updateTravelStatusUpdate(s);
                }
            }
        });
    }
}

