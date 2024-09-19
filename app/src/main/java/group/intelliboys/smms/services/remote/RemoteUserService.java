package group.intelliboys.smms.services.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Objects;

import group.intelliboys.smms.activities.HomeActivity;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.models.forms.UserCredential;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbUserService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteUserService {
    private Activity activityRef;
    private final OkHttpClient okHttpClient;
    private final Context context;

    public RemoteUserService(Activity activity) {
        this.activityRef = activity;
        this.context = activity.getApplicationContext();
        okHttpClient = CustomOkHttpClient.getOkHttpClient(context);
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

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 200) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());

                    User user = mapper.readValue(responseBody, User.class);
                    if (user != null) {
                        user.setAuthToken(credential.getToken());
                        LocalDbUserService userService = new LocalDbUserService(activityRef);

                        try {
                            userService.deleteUser(user.getEmail());
                            userService.addUser(user);

                            activityRef.runOnUiThread(() -> {
                                Toast.makeText(activityRef, "AUTHENTICATION SUCCESS!", Toast.LENGTH_LONG).show();
                            });

                            Utils.getInstance().setLoggedInUser(user);
                            Intent intent = new Intent(activityRef, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activityRef.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activityRef, "SOMETHING WENT WRONG!", Toast.LENGTH_LONG).show();
                            Log.i("", Objects.requireNonNull(e.getMessage()));
                        }

                    } else {
                        activityRef.runOnUiThread(() -> {
                            Toast.makeText(activityRef, "SOMETHING WENT WRONG!", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        });
    }
}
