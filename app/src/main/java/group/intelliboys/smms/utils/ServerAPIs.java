package group.intelliboys.smms.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.dashboard.HomeActivity;
import group.intelliboys.smms.activities.forgot_password.ForgotPasswordActivity;
import group.intelliboys.smms.activities.signin.SignInActivity;
import group.intelliboys.smms.activities.signin.SignInVerificationActivity;
import group.intelliboys.smms.activities.signup.SignUpActivity;
import group.intelliboys.smms.activities.signup.SignUpProfileActivity;
import group.intelliboys.smms.activities.signup.SignUpVerificationActivity;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.DeviceSpecs;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.fragments.forgot_password.ForgotPasswordFragment;
import group.intelliboys.smms.fragments.forgot_password.ForgotPasswordVerificationFragment;
import group.intelliboys.smms.fragments.forgot_password.SearchAccountFragment;
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
                                        Intent intent = new Intent(activity, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(intent);

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
                toastNoInternetConnection();
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
                                                activity.runOnUiThread(() -> {
                                                    String formId = (String) jsonResponse.get("formId");

                                                    Intent intent = new Intent(activity, SignInVerificationActivity.class);
                                                    intent.putExtra("formId", formId);
                                                    activity.startActivity(intent);
                                                    postSignIn(signInActivity);
                                                    Commons.toastMessage(activity, "Please Verify!");
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
                        postSignIn(signInActivity);
                        toastErrorMessage();
                    });
                }
            } else activity.runOnUiThread(() -> {
                postSignIn(signInActivity);
                toastNoInternetConnection();
            });
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signIn activity instance!");
            });
            throw new RuntimeException();
        }
    }

    public void verifySignIn(JSONObject form) {
        Log.i("", "Executed!");
        if (activity instanceof SignInVerificationActivity) {
            SignInVerificationActivity signInVerificationActivity = (SignInVerificationActivity) activity;
            serverIpAddress = networkConfig.getServerIpAddress(activity);

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String LOGIN_2FA_URL = serverIpAddress + "/login/2fa/verify";

                try {
                    RequestBody body = RequestBody.create(form.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(LOGIN_2FA_URL)
                            .post(body)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            signInVerificationActivity.getSigninVerSubmitBtn().setEnabled(true);
                            Log.i("", Objects.requireNonNull(e.getMessage()));
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                Log.i("", data.toString());

                                String formId = (String) data.get("formId");
                                String token = (String) data.get("token");
                                boolean isEmailOtpMatches = (boolean) data.get("emailOtpMatches");
                                boolean isSmsOtpMatches = (boolean) data.get("smsOtpMatches");

                                if (!isEmailOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        signInVerificationActivity.getSigninVerEmailOtpField().setError("Invalid OTP!");
                                    });
                                }

                                if (!isSmsOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        signInVerificationActivity.getSigninVerSmsOtpField().setError("Invalid OTP!");
                                    });
                                }

                                if (isEmailOtpMatches && isSmsOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        Intent intent = new Intent(activity, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(intent);
                                        Commons.toastMessage(activity, "Authentication Success!");
                                    });
                                }
                            }

                            activity.runOnUiThread(() -> {
                                signInVerificationActivity.getSigninVerSubmitBtn().setEnabled(true);
                            });
                        }
                    });
                } catch (Exception e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                    activity.runOnUiThread(this::toastErrorMessage);
                }
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid SignIn verification activity");
            });
            throw new RuntimeException();
        }
    }

    public void resendLoginEmailOtp() {
        if (activity instanceof SignInVerificationActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignInVerificationActivity signUpVerificationActivity = (SignInVerificationActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                String formId = signUpVerificationActivity.getIntent().getStringExtra("formId");
                assert formId != null;
                Log.i("", formId);
                final String RESEND_EMAIL_OTP = serverIpAddress + "/login/2fa/resend/email-otp/" + formId;

                try {
                    Request request = new Request.Builder()
                            .url(RESEND_EMAIL_OTP)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            toastErrorMessage();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                String status = (String) data.get("status");

                                if (status != null && !status.isEmpty()) {
                                    switch (status) {
                                        case "NEW_EMAIL_OTP_RESENT_SUCCESSFULLY":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "New Email OTP sent!");
                                            });
                                            break;
                                        case "2FA_VERIFICATION_FORM_ALREADY_VERIFIED":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "This form already verified!");
                                                Intent intent = new Intent(activity, SignInActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                activity.startActivity(intent);
                                            });
                                            break;
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                }
            }
        }
    }

    public void resendLoginSmsOtp() {
        if (activity instanceof SignInVerificationActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignInVerificationActivity signUpVerificationActivity = (SignInVerificationActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                String formId = signUpVerificationActivity.getIntent().getStringExtra("formId");
                final String RESEND_EMAIL_OTP = serverIpAddress + "/login/2fa/resend/sms-otp/" + formId;

                try {
                    Request request = new Request.Builder()
                            .url(RESEND_EMAIL_OTP)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            toastErrorMessage();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);

                                String status = (String) data.get("status");

                                if (status != null && !status.isEmpty()) {
                                    switch (status) {
                                        case "NEW_SMS_OTP_RESENT_SUCCESSFULLY":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "New Sms OTP sent!");
                                            });
                                            break;
                                        case "2FA_VERIFICATION_FORM_ALREADY_VERIFIED":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "This form already verified!");
                                                Intent intent = new Intent(activity, SignInActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                activity.startActivity(intent);
                                            });
                                            break;
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                }
            }
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

    // ==================================== USER REGISTRATION ====================================

    public void verifyEmailAndPhoneNumberIfNotExists(JSONObject signupForm) throws JSONException {
        if (activity instanceof SignUpActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignUpActivity signUpActivity = (SignUpActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String LOGIN_URL = serverIpAddress + "/register/is-account-exists";

                String email = signupForm.getString("email");
                String phoneNumber = signupForm.getString("phoneNumber");
                String password = signupForm.getString("password");
                String confirmPassword = signupForm.getString("confirmPassword");

                try {
                    RequestBody body = RequestBody.create(signupForm.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(LOGIN_URL)
                            .post(body)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            activity.runOnUiThread(() -> {
                                Commons.toastMessage(activity, "Can't connect to the server!");
                                signUpActivity.getSignupNextButton().setEnabled(true);
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();

                                if (!body.isEmpty()) {
                                    Map<?, ?> jsonData = ObjectMapper.convertJsonToMapObject(body);
                                    boolean isEmailExists = (boolean) jsonData.get("emailExists");
                                    boolean isPhoneNumberExists = (boolean) jsonData.get("phoneNumberExists");

                                    if (isEmailExists) {
                                        // EXECUTE THIS CODE IF EMAIL EXISTS
                                        activity.runOnUiThread(() -> {
                                            signUpActivity.getSignupEmailField().setError("Email already exists!");
                                        });
                                    }

                                    if (isPhoneNumberExists) {
                                        // EXECUTE THIS CODE IF PHONE NUMBER EXISTS
                                        activity.runOnUiThread(() -> {
                                            signUpActivity.getSignupPhoneNumField().setError("Phone number already exists!");
                                        });
                                    }

                                    if (!isEmailExists && !isPhoneNumberExists) {
                                        Intent intent = new Intent(activity, SignUpProfileActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("phoneNumber", phoneNumber);
                                        intent.putExtra("password", password);
                                        intent.putExtra("confirmPassword", confirmPassword);
                                        activity.startActivity(intent);
                                    }
                                }
                            }

                            activity.runOnUiThread(() -> {
                                signUpActivity.getSignupNextButton().setEnabled(true);
                            });
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                    activity.runOnUiThread(() -> {
                        signUpActivity.getSignupNextButton().setEnabled(true);
                    });
                }
            } else {
                toastNoInternetConnection();
                activity.runOnUiThread(() -> {
                    signUpActivity.getSignupNextButton().setEnabled(true);
                });
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signup activity instance!");
            });
            throw new RuntimeException();
        }
    }

    public void submitUserRegistrationForm(JSONObject form) {
        if (activity instanceof SignUpProfileActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignUpProfileActivity signUpActivity = (SignUpProfileActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String REGISTRATION_FORM_SUBMIT_URL = serverIpAddress + "/register/submit";

                try {
                    RequestBody body = RequestBody.create(form.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(REGISTRATION_FORM_SUBMIT_URL)
                            .post(body)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            activity.runOnUiThread(() -> {
                                Commons.toastMessage(activity, "Can't connect to the server!");
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();

                                if (!body.isEmpty()) {
                                    Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                    String status = (String) data.get("status");
                                    String formId = (String) data.get("formId");

                                    if (status != null && !status.isEmpty() && formId != null && !formId.isEmpty()) {
                                        switch (status) {
                                            case "NEED_VERIFICATION":
                                                Intent intent = new Intent(activity, SignUpVerificationActivity.class);
                                                intent.putExtra("formId", formId);
                                                activity.startActivity(intent);
                                                break;
                                            case "NOT_SAME_PASSWORD":
                                                Commons.toastMessage(activity, "Invalid password!");
                                                break;
                                            case "EMAIL_PHONE_NUMBER_EXISTS":
                                                Commons.toastMessage(activity, "Email or phone number exists!");
                                                break;
                                        }
                                    } else toastErrorMessage();
                                }
                            }

                            activity.runOnUiThread(() -> {
                                signUpActivity.getSignupSubmitBtn().setEnabled(true);
                            });
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                    signUpActivity.getSignupSubmitBtn().setEnabled(true);
                }
            } else {
                toastNoInternetConnection();
                signUpActivity.getSignupSubmitBtn().setEnabled(true);
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid signup profile \n" +
                        "activity instance!");

                throw new RuntimeException();
            });
        }
    }

    public void verifyRegistrationOtp(JSONObject form) {
        if (activity instanceof SignUpVerificationActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignUpVerificationActivity signUpVerificationActivity = (SignUpVerificationActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String REGISTRATION_VERIFICATION_URL = serverIpAddress + "/register/verify";

                try {
                    RequestBody body = RequestBody.create(form.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(REGISTRATION_VERIFICATION_URL)
                            .post(body)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            activity.runOnUiThread(() -> {
                                signUpVerificationActivity.getSignupVerSubmitBtn().setEnabled(true);
                            });
                            toastErrorMessage();
                            Log.i("", Objects.requireNonNull(e.getMessage()));
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                assert response.body() != null;
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                Log.i("", data.toString());

                                boolean isEmailOtpMatches = (boolean) data.get("emailOtpMatches");
                                boolean isSmsOtpMatches = (boolean) data.get("smsOtpMatches");

                                if (!isEmailOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        signUpVerificationActivity.getSignupVerEmailOtpField().setError("Incorrect Email OTP!");
                                    });
                                }

                                if (!isSmsOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        signUpVerificationActivity.getSignupVerSmsOtpField().setError("Incorrect Sms OTP!");
                                    });
                                }

                                if (isEmailOtpMatches && isSmsOtpMatches) {
                                    activity.runOnUiThread(() -> {
                                        Intent intent = new Intent(activity, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(intent);
                                        Commons.toastMessage(activity, "Authentication Success!");
                                        signUpVerificationActivity.getSignupVerSubmitBtn().setEnabled(true);
                                    });
                                }

                                activity.runOnUiThread(() -> {
                                    signUpVerificationActivity.getSignupVerSubmitBtn().setEnabled(true);
                                });

                            } catch (Exception e) {
                                toastErrorMessage();
                                Log.i("", Objects.requireNonNull(e.getMessage()));
                                activity.runOnUiThread(() -> {
                                    signUpVerificationActivity.getSignupVerSubmitBtn().setEnabled(true);
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                    signUpVerificationActivity.getSignupVerSubmitBtn().setEnabled(true);
                }
            }
        }
    }

    public void toastErrorMessage() {
        activity.runOnUiThread(() -> {
            Commons.toastMessage(activity, "Something went wrong!");
        });
    }

    public void toastNoInternetConnection() {
        activity.runOnUiThread(() -> {
            Commons.toastMessage(activity, "No Internet Connection");
        });
    }

    public void resendRegistrationEmailOtp() {
        if (activity instanceof SignUpVerificationActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignUpVerificationActivity signUpVerificationActivity = (SignUpVerificationActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                String formId = signUpVerificationActivity.getIntent().getStringExtra("formId");
                assert formId != null;
                Log.i("", formId);
                final String RESEND_EMAIL_OTP = serverIpAddress + "/register/resend/email-otp/" + formId;

                try {
                    Request request = new Request.Builder()
                            .url(RESEND_EMAIL_OTP)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            toastErrorMessage();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                String status = (String) data.get("status");

                                if (status != null && !status.isEmpty()) {
                                    switch (status) {
                                        case "NEW_EMAIL_OTP_SENT":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "New Email OTP sent!");
                                            });
                                            break;
                                        case "REGISTRATION_FORM_ALREADY_VERIFIED":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "This form already verified!");
                                                Intent intent = new Intent(activity, SignInActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                activity.startActivity(intent);
                                            });
                                            break;
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                }
            }
        }
    }

    public void resendRegistrationSmsOtp() {
        if (activity instanceof SignUpVerificationActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            SignUpVerificationActivity signUpVerificationActivity = (SignUpVerificationActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                String formId = signUpVerificationActivity.getIntent().getStringExtra("formId");
                final String RESEND_EMAIL_OTP = serverIpAddress + "/register/resend/sms-otp/" + formId;

                try {
                    Request request = new Request.Builder()
                            .url(RESEND_EMAIL_OTP)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            toastErrorMessage();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.body() != null) {
                                String body = response.body().string();
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);

                                String status = (String) data.get("status");

                                if (status != null && !status.isEmpty()) {
                                    switch (status) {
                                        case "NEW_SMS_OTP_SENT":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "New Sms OTP sent!");
                                            });
                                            break;
                                        case "REGISTRATION_FORM_ALREADY_VERIFIED":
                                            activity.runOnUiThread(() -> {
                                                Commons.toastMessage(activity, "This form already verified!");
                                                Intent intent = new Intent(activity, SignInActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                activity.startActivity(intent);
                                            });
                                            break;
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    toastErrorMessage();
                }
            }
        }
    }

    // ==================================== END OF USER REGISTRATION ====================================

    // ==================================== FORGOT PASSWORD ====================================

    public void forgotPasswordSearchAccount(String email) {
        if (activity instanceof ForgotPasswordActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            ForgotPasswordActivity forgotPasswordActivity = (ForgotPasswordActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String SEARCH_ACCOUNT_URL = serverIpAddress + "/forgot-password/search-account/" + email;

                Request request = new Request.Builder()
                        .url(SEARCH_ACCOUNT_URL)
                        .get()
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        toastErrorMessage();
                        Log.i("", Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();

                            if (!body.isEmpty()) {
                                Map<?, ?> responseData = ObjectMapper.convertJsonToMapObject(body);
                                String id = (String) responseData.get("id");
                                boolean isExists = (boolean) responseData.get("isExists");

                                if (isExists) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", id);

                                    forgotPasswordActivity.getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.forgotPasswordContainer, ForgotPasswordVerificationFragment.class, bundle)
                                            .commit();
                                } else {
                                    SearchAccountFragment searchAccountFragment = (SearchAccountFragment) forgotPasswordActivity
                                            .getSupportFragmentManager().findFragmentById(R.id.forgotPasswordContainer);

                                    if (searchAccountFragment != null) {
                                        activity.runOnUiThread(() -> {
                                            searchAccountFragment.getSearchAccountField().setError("Email not found!");
                                        });
                                    } else {
                                        Log.i("", "Search Account Fragment is null!");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid Forgot Password Activity!");
                throw new RuntimeException();
            });
        }
    }

    public void forgotPasswordResendEmailOtp() {
        if (activity instanceof ForgotPasswordActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            ForgotPasswordActivity forgotPasswordActivity = (ForgotPasswordActivity) activity;
            ForgotPasswordVerificationFragment forgotPasswordVerificationFragment = (ForgotPasswordVerificationFragment) forgotPasswordActivity
                    .getSupportFragmentManager().findFragmentById(R.id.forgotPasswordContainer);

            assert forgotPasswordVerificationFragment != null;
            assert forgotPasswordVerificationFragment.getArguments() != null;
            String formId = forgotPasswordVerificationFragment.getArguments().getString("id");

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String RESEND_EMAIL_URL = serverIpAddress + "/forgot-password/resend/email-otp/" + formId;

                Request request = new Request.Builder()
                        .url(RESEND_EMAIL_URL)
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
                            if (body.contains("NEW_EMAIL_OTP_RESENT")) {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "New OTP Resent!");
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "New OTP failed to send!");
                                });
                            }
                        }
                    }
                });
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid Forgot Password Activity!");
            });
        }
    }

    public void forgotPasswordResendSmsOtp() {
        if (activity instanceof ForgotPasswordActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            ForgotPasswordActivity forgotPasswordActivity = (ForgotPasswordActivity) activity;
            ForgotPasswordVerificationFragment forgotPasswordVerificationFragment = (ForgotPasswordVerificationFragment) forgotPasswordActivity
                    .getSupportFragmentManager().findFragmentById(R.id.forgotPasswordContainer);

            assert forgotPasswordVerificationFragment != null;
            assert forgotPasswordVerificationFragment.getArguments() != null;
            String formId = forgotPasswordVerificationFragment.getArguments().getString("id");

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String RESEND_EMAIL_URL = serverIpAddress + "/forgot-password/resend/sms-otp/" + formId;

                Request request = new Request.Builder()
                        .url(RESEND_EMAIL_URL)
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
                            if (body.contains("NEW_EMAIL_OTP_RESENT")) {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "New OTP Resent!");
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "New OTP failed to send!");
                                });
                            }
                        }
                    }
                });
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid Forgot Password Activity!");
            });
        }
    }

    public void forgotPasswordSubmitOtp(JSONObject form) {
        if (activity instanceof ForgotPasswordActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            ForgotPasswordActivity forgotPasswordActivity = (ForgotPasswordActivity) activity;
            ForgotPasswordVerificationFragment forgotPasswordVerificationFragment = (ForgotPasswordVerificationFragment) forgotPasswordActivity
                    .getSupportFragmentManager().findFragmentById(R.id.forgotPasswordContainer);


            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String FORGOT_PASS_OTP_URL = serverIpAddress + "/forgot-password/verify-otp";
                RequestBody requestBody = RequestBody.create(form.toString(), JSON);

                Request request = new Request.Builder()
                        .url(FORGOT_PASS_OTP_URL)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        activity.runOnUiThread(() -> {
                            assert forgotPasswordVerificationFragment != null;
                            forgotPasswordVerificationFragment.getForgotPassVerSubmitBtn().setEnabled(true);
                        });

                        Log.i("", Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();

                            if (!body.isEmpty()) {
                                Map<?, ?> data = ObjectMapper.convertJsonToMapObject(body);
                                Log.i("", data.toString());

                                String id = (String) data.get("id");
                                String message = (String) data.get("message");
                                boolean isEmailOtpSame = (boolean) data.get("emailOtpSame");
                                boolean isSmsOtpSame = (boolean) data.get("smsOtpSame");

                                if (message.equals("TOKEN_EXISTS")) {

                                    if (!isEmailOtpSame) {
                                        activity.runOnUiThread(() -> {
                                            assert forgotPasswordVerificationFragment != null;
                                            forgotPasswordVerificationFragment.getForgotPassVerEmailOtpField().setError("Incorrect OTP!");
                                        });
                                    } else {
                                        activity.runOnUiThread(() -> {
                                            assert forgotPasswordVerificationFragment != null;
                                            forgotPasswordVerificationFragment.getForgotPassVerEmailOtpField().setError(null);
                                        });
                                    }

                                    if (!isSmsOtpSame) {
                                        activity.runOnUiThread(() -> {
                                            assert forgotPasswordVerificationFragment != null;
                                            forgotPasswordVerificationFragment.getForgotPassVerSmsOtpField().setError("Incorrect OTP!");
                                        });
                                    } else {
                                        activity.runOnUiThread(() -> {
                                            assert forgotPasswordVerificationFragment != null;
                                            forgotPasswordVerificationFragment.getForgotPassVerSmsOtpField().setError(null);
                                        });
                                    }

                                    if (isEmailOtpSame && isSmsOtpSame) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", id);
                                        forgotPasswordActivity.getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.forgotPasswordContainer, ForgotPasswordFragment.class, bundle)
                                                .commit();
                                    }

                                } else {
                                    activity.runOnUiThread(() -> {
                                        Intent intent = new Intent(activity, SignInActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(intent);
                                        Commons.toastMessage(activity, "This form is expired!");
                                    });
                                }
                            }
                        }

                        activity.runOnUiThread(() -> {
                            assert forgotPasswordVerificationFragment != null;
                            forgotPasswordVerificationFragment.getForgotPassVerSubmitBtn().setEnabled(true);
                        });
                    }
                });
            }
        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid Forgot Password Activity!");
                throw new RuntimeException();
            });
        }
    }

    public void forgotPasswordSubmit(JSONObject form) {
        if (activity instanceof ForgotPasswordActivity) {
            serverIpAddress = networkConfig.getServerIpAddress(activity);
            ForgotPasswordActivity forgotPasswordActivity = (ForgotPasswordActivity) activity;

            if (serverIpAddress != null && !serverIpAddress.isEmpty()) {
                final String FORGOT_PASSWORD_SUBMIT = serverIpAddress + "/forgot-password/submit";
                RequestBody requestBody = RequestBody.create(form.toString(), JSON);

                Request request = new Request.Builder()
                        .url(FORGOT_PASSWORD_SUBMIT)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("", Objects.requireNonNull(e.getMessage()));
                        toastErrorMessage();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();

                            if (body.contains("CHANGE_PASSWORD_SUCCESS")) {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "Changed password successfully!");
                                });

                                Intent intent = new Intent(activity, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(intent);
                            } else {
                                activity.runOnUiThread(() -> {
                                    Commons.toastMessage(activity, "Change password failed!");
                                });
                            }
                        }
                    }
                });
            }

        } else {
            activity.runOnUiThread(() -> {
                Commons.toastMessage(activity, "Invalid Forgot Password Activity!");
                throw new RuntimeException();
            });
        }
    }

    // ==================================== END OF FORGOT PASSWORD ====================================
}
