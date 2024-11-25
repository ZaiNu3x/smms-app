package group.intelliboys.smms.services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.orm.data.TravelHistory;
import group.intelliboys.smms.orm.repository.TravelHistoryRepository;
import group.intelliboys.smms.utils.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravelHistoryService {
    private static TravelHistoryService instance;
    private final TravelHistoryRepository travelHistoryRepository;
    private final OkHttpClient httpClient;

    private TravelHistoryService() {
        travelHistoryRepository = new TravelHistoryRepository();
        httpClient = new OkHttpClient();
    }

    public static TravelHistoryService getInstance() {
        if (instance == null) {
            instance = new TravelHistoryService();
        }

        return instance;
    }

    public void updateNullStartLocation() {
        List<TravelHistory> travelHistories = travelHistoryRepository.getTravelHistoriesWithNullStartLocation();

        travelHistories.forEach(t -> {
            String API = "https://nominatim.openstreetmap.org/reverse?lat=" + t.getStartLatitude() + "&lon=" +
                    t.getStartLongitude() + "&zoom=18&addressdetails=1&format=json";

            Request request = new Request.Builder()
                    .url(API)
                    .addHeader("Referer", "https://map.project-osrm.org/")
                    .get()
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("", Objects.requireNonNull(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.body() != null) {
                        String body = response.body().string();
                        Map<?, ?> map = ObjectMapper.convertJsonToMapObject(body);
                        String displayName = (String) map.get("display_name");
                        t.setStartLocationName(displayName);
                        travelHistoryRepository.updateTravelHistory(t);
                    }
                }
            });
        });
    }

    public void updateNullEndLocation() {
        List<TravelHistory> travelHistories = travelHistoryRepository.getTravelHistoriesWithNullEndLocation();

        travelHistories.forEach(t -> {
            String API = "https://nominatim.openstreetmap.org/reverse?lat=" + t.getEndLatitude() + "&lon=" +
                    t.getEndLongitude() + "&zoom=18&addressdetails=1&format=json";

            Request request = new Request.Builder()
                    .url(API)
                    .addHeader("Referer", "https://map.project-osrm.org/")
                    .get()
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("", Objects.requireNonNull(e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.body() != null) {
                        String body = response.body().string();
                        Map<?, ?> map = ObjectMapper.convertJsonToMapObject(body);
                        String displayName = (String) map.get("display_name");
                        t.setEndLocationName(displayName);
                        travelHistoryRepository.updateTravelHistory(t);
                    }
                }
            });
        });
    }
}
