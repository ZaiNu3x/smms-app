package group.intelliboys.smms.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.data.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserService {
    private OkHttpClient okHttpClient;

    public UserService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public User fetchUserData(String authToken) {
        final String FETCH_USER_URL = NetworkConfig.HOST + NetworkConfig.PORT + "/user/profile";
        final MediaType JSON = MediaType.get("application/json");

        Request request = new Request.Builder()
                .url(FETCH_USER_URL)
                .addHeader("Authorization", authToken)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

        return null;
    }
}
