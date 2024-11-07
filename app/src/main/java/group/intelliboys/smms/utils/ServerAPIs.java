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
                            postSignIn(signInActivity);
                            Commons.toastMessage(signInActivity, "Profile fetching failed!");
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
                                        Log.i("", user.toString());

                                        // THIS CODE WILL BE EXECUTED AFTER USER PROFILE DATA SAVED INTO LOCAL DATABASE.
                                        postSignIn(signInActivity);
                                        Commons.toastMessage(activity, "Authentication Success!");
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }
                        }
                    }
                });
            } else activity.runOnUiThread(() -> {
                postSignIn(signInActivity);
                Commons.toastMessage(activity, "No Internet Connection!");
            });
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signIn activity instance!");
            });

            throw new RuntimeException();
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
                                postSignIn(signInActivity);
                                Commons.toastMessage(activity, "Server Error!");
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
                                                    postSignIn(signInActivity);
                                                    signInActivity.getSignInEmailField().setError("Email not found!");
                                                    Commons.toastMessage(activity, "Email not found!");
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
                                                    postSignIn(signInActivity);
                                                    signInActivity.getSignInPasswordField().setError("Wrong Password!");
                                                    Commons.toastMessage(activity, "Wrong Password!");
                                                });
                                                break;
                                            case "Please Verify!":
                                                // CODE FOR 2 FACTOR AUTHENTICATION
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    activity.runOnUiThread(() -> {
                        postSignIn(signInActivity);
                        Commons.toastMessage(activity, "Something went wrong!");
                    });
                }
            } else activity.runOnUiThread(() -> {
                postSignIn(signInActivity);
                Commons.toastMessage(activity, "No Internet Connection!");
            });
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signIn activity instance!");
                throw new RuntimeException();
            });
        }
    }

    public void preSignIn(SignInActivity signInActivity) {
        signInActivity.disableButtons();
        signInActivity.getSignInProgress().setVisibility(View.VISIBLE);
    }

    public void postSignIn(SignInActivity signInActivity) {
        signInActivity.enableButtons();
        signInActivity.getSignInProgress().setVisibility(View.INVISIBLE);
    }
    // ==================================== END OF USER SIGN IN ====================================
}
