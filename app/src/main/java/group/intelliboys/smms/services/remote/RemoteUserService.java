package group.intelliboys.smms.services.remote;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import group.intelliboys.smms.activities.HomeActivity;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.models.forms.UserCredential;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbTravelHistoryService;
import group.intelliboys.smms.services.local.LocalDbUserService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteUserService {
    private final Activity activityRef;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final LocalDbUserService localDbUserService;
    private final LocalDbTravelHistoryService travelHistoryService;

    public RemoteUserService(Activity activity) {
        this.activityRef = activity;
        this.okHttpClient = CustomOkHttpClient.getOkHttpClient(activity.getApplicationContext());
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.localDbUserService = new LocalDbUserService(activityRef);
        this.travelHistoryService = new LocalDbTravelHistoryService(activityRef);
    }

    public void fetchUserData(UserCredential credential) {
        String ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        final String FETCH_USER_URL = ipAddress + "/user/profile";

        Request request = new Request.Builder()
                .url(FETCH_USER_URL)
                .addHeader("Authorization", "Bearer " + credential.getToken())
                .addHeader("Device-ID", credential.getDeviceId())
                .addHeader("Device-Name", credential.getDeviceName())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RemoteUserService", "Fetch user data failed: " + e.getMessage());
                showToast("Network error. Please try again.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    if (!body.isEmpty()) {
                        try {
                            User fetchedUser = objectMapper.readValue(body, User.class);
                            handleUserFetchSuccess(fetchedUser, credential);
                        } catch (Exception e) {
                            Log.e("RemoteUserService", "Error parsing user data: " + e.getMessage());
                            showToast("SOMETHING WENT WRONG!");
                        }
                    }
                } else {
                    showToast("SOMETHING WENT WRONG!");
                }
            }
        });
    }

    private void handleUserFetchSuccess(User fetchedUser, UserCredential credential) {
        fetchedUser.setAuthToken(credential.getToken());
        localDbUserService.deleteUser(fetchedUser.getEmail());
        localDbUserService.addUser(fetchedUser);
        updateTravelHistories(fetchedUser.getTravelHistories(), fetchedUser.getEmail());

        activityRef.runOnUiThread(() -> {
            showToast("AUTHENTICATION SUCCESS!");
            Utils.getInstance().setLoggedInUser(fetchedUser);
            Intent intent = new Intent(activityRef, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activityRef.startActivity(intent);
        });
    }

    private void updateTravelHistories(List<TravelHistory> travelHistories, String userId) {
        travelHistories.forEach(travelHistory -> {
            travelHistory.setUserId(userId);
            travelHistoryService.addTravelHistory(travelHistory);
        });
    }

    public void synchronizedData(UserCredential credential, JSONObject user) {
        String ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        final String FETCH_USER_URL = ipAddress + "/user/sync";

        final MediaType JSON = MediaType.get("application/json");
        RequestBody requestBody = RequestBody.create(user.toString(), JSON);

        Request request = new Request.Builder()
                .url(FETCH_USER_URL)
                .addHeader("Authorization", "Bearer " + credential.getToken())
                .addHeader("Device-ID", credential.getDeviceId())
                .addHeader("Device-Name", credential.getDeviceName())
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RemoteUserService", "Data synchronization failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    if (!body.isEmpty()) {
                        try {
                            User fetchedUser = objectMapper.readValue(body, User.class);
                            updateTravelHistories(fetchedUser.getTravelHistories(), fetchedUser.getEmail());
                        } catch (Exception e) {
                            Log.e("RemoteUserService", "Error parsing sync data: " + e.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void showToast(String message) {
        activityRef.runOnUiThread(() -> Toast.makeText(activityRef, message, Toast.LENGTH_LONG).show());
    }
}
