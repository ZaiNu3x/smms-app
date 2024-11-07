package group.intelliboys.smms.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import group.intelliboys.smms.activities.signin.SignInActivity;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.DeviceSpecs;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.data.user.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerAPIs {
    private final Activity activity;
    private final OkHttpClient okHttpClient;
    private final NetworkConfig networkConfig = NetworkConfig.getInstance();
    private String serverIpAddress;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public ServerAPIs(Activity activity) {
        this.okHttpClient = CustomOkHttpClient.getOkHttpClient(activity);
        this.activity = activity;
    }

    // ==================================== USER SIGN IN ====================================

    // THIS BLOCK OF CODE IS SPECIALLY CODED FOR SIGN IN ACTIVITY ONLY
    public void getUserProfileFromRemoteServer(String deviceId, String deviceName, String authToken) {
        if (activity instanceof SignInActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignInActivity signInActivity = (SignInActivity) activity;

            if (serverIpAddress != null) {
                final String USER_PROFILE_URL = serverIpAddress + "/user/profile";
                Request request = new Request.Builder()
                        .url(USER_PROFILE_URL)
                        .addHeader("Authorization", "Bearer " + authToken)
                        .addHeader("Device-ID", deviceId)
                        .addHeader("Device-Name", deviceName)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        activity.runOnUiThread(() -> {
                            signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();

                            if (!body.isEmpty()) {
                                activity.runOnUiThread(() -> {
                                    try {
                                        Map<?, ?> profileData = ObjectMapper.convertJsonToMapObject(body);
                                        User user = ObjectMapper.convertMapObjectToUser(profileData);

                                        // THIS CODE WILL BE EXECUTED AFTER USER PROFILE DATA SAVED INTO LOCAL DATABASE.
                                        Commons.toastMessage(activity, "Authentication Success!");
                                        signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }
                        }
                    }
                });
            } else activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "No Internet Connection!");
                signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
            });
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signIn activity instance!");
            });
        }
    }

    // THIS BLOCK OF CODE IS SPECIALLY CODED FOR SIGN IN ACTIVITY ONLY
    public void signIn(JSONObject loginCredential) {
        if (activity instanceof SignInActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignInActivity signInActivity = (SignInActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String LOGIN_URL = serverIpAddress + "/login";

                try {
                    RequestBody body = RequestBody.create(loginCredential.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(LOGIN_URL)
                            .post(body)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            activity.runOnUiThread(() -> {
                                Commons.toastMessage(activity, "Server Error!");
                                signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String responseBody = response.body().string();

                                if (!responseBody.isEmpty()) {
                                    Map<?, ?> jsonResponse = ObjectMapper.convertJsonToMapObject(responseBody);

                                    String message = (String) jsonResponse.get("message");

                                    if (message != null) {
                                        switch (message) {
                                            case "Email not found!":
                                                // CODE FOR EMAIL NOT FOUND!
                                                activity.runOnUiThread(() -> {
                                                    Commons.toastMessage(activity, "Email not found!");
                                                    signInActivity.getSignInEmailField().setError("Email not found!");
                                                    signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
                                                });
                                                break;
                                            case "Authentication Success!":
                                                // CODE FOR AUTHENTICATION SUCCESS!
                                                String authToken = (String) jsonResponse.get("token");
                                                getUserProfileFromRemoteServer(DeviceSpecs.DEVICE_ID, DeviceSpecs.DEVICE_NAME,
                                                        authToken);
                                                break;
                                            case "Wrong Password!":
                                                // CODE FOR WRONG PASSWORD!
                                                activity.runOnUiThread(() -> {
                                                    Commons.toastMessage(activity, "Wrong Password!");
                                                    signInActivity.getSignInPasswordField().setError("Wrong Password!");
                                                    signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
                                                });
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    activity.runOnUiThread(() -> {
                        Commons.toastMessage(activity, "Something went wrong!");
                        ((SignInActivity) activity).getSignInPasswordField().setVisibility(View.INVISIBLE);
                    });
                }
            } else activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "No Internet Connection!");
                signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
            });
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signIn activity instance!");
            });
        }
    }
    // ==================================== END OF USER SIGN IN ====================================
}
