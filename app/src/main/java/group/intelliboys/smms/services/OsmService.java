package group.intelliboys.smms.services;

import android.util.Log;

import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.fragments.driving_mode.DrivingModeFragment;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsmService {
    private OkHttpClient okHttpClient;
    private Fragment fragment;

    public OsmService(Fragment fragment) {
        this.fragment = fragment;
        okHttpClient = new OkHttpClient();
    }

    public void getRoadTypeAndSpeedLimit(GeoPoint geoPoint) {
        if (fragment instanceof DrivingModeFragment) {
            DrivingModeFragment drivingModeFragment = (DrivingModeFragment) fragment;

            final String API = "https://overpass-api.de/api/interpreter?data=[out:json];way(around:5, "
                    + geoPoint.getLatitude() + ", " + geoPoint.getLongitude() + ")[maxspeed][name];out qt;";

            Request request = new Request.Builder()
                    .url(API)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.body() != null) {
                    String body = response.body().string();
                    Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                    ArrayList<Map<String, Object>> elements = (ArrayList<Map<String, Object>>) data.get("elements");

                    if (elements != null && !elements.isEmpty()) {
                        Map<String, Object> firstTag = (Map<String, Object>) elements.get(0).get("tags");

                        if (firstTag != null) {
                            String roadName = (String) firstTag.get("name");
                            String roadType = (String) firstTag.get("highway");
                            int maxSpeed = Integer.parseInt((String) firstTag.get("maxspeed"));

                            drivingModeFragment.requireActivity().runOnUiThread(() -> {
                                drivingModeFragment.getRoadTypeTxtView().setText(roadType);
                                drivingModeFragment.getSpeedLimitTxtView().setText(maxSpeed + " Km/Hr");
                                drivingModeFragment.setSpeedLimit(maxSpeed);
                                Commons.toastMessage(drivingModeFragment.requireActivity(), roadName);
                            });
                        }
                    }
                }

                response.close();
            } catch (Exception e) {
                Log.i("e", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}
