package group.intelliboys.smms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.forms.UserCredential;
import group.intelliboys.smms.models.results.SignInResult;
import group.intelliboys.smms.services.remote.RemoteUserService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    private Activity activityRef;
    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private Button signupButton;
    private Button loginButton;
    private ProgressBar signinProgress;
    private Drawable drawable;
    private boolean isEmailValid, isPasswordValid;
    private OkHttpClient okHttpClient;
    private String ipAddress;
    private TextView forgotPasswordLbl;

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRef = this;
        setContentView(R.layout.activity_signin);

        if (NetworkConfig.getInstance().isNetworkActive()) {
            ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        }

        emailEditTxt = findViewById(R.id.emailEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        signinProgress = findViewById(R.id.signinProgress);
        forgotPasswordLbl = findViewById(R.id.forgotPasswordLbl);
        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());
        drawable = getDrawable(R.drawable.error);
        assert drawable != null;
        drawable.setBounds(0, 0, 45, 45);

        requestPermissions();

        emailEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(s).matches();

                    if (!isEmailValid) {
                        emailEditTxt.setError("INVALID EMAIL ADDRESS!", drawable);
                    } else emailEditTxt.setError(null);

                    loginButton.setEnabled(isEmailValid && isPasswordValid);
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        passwordEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    isPasswordValid = passwordEditTxt.length() >= 8;

                    if (!isPasswordValid) {
                        passwordEditTxt.setError("PASSWORD MUST HAVE ATLEAST 8 CHARACTERS!", drawable);
                    } else passwordEditTxt.setError(null);

                    loginButton.setEnabled(isEmailValid && isPasswordValid);
                };

                handler.postDelayed(runnable, DELAY);
            }
        });
        loginButton.setOnClickListener(btn -> {
            if (NetworkConfig.getInstance().isNetworkActive()) {
                ipAddress = NetworkConfig.getInstance().getServerIpAddress();
            }

            if (NetworkConfig.getInstance().isNetworkActive() && ipAddress != null) {
                signinProgress.setVisibility(View.VISIBLE);

                @SuppressLint("HardwareIds")
                String deviceId = Settings.Secure.getString(getApplicationContext()
                        .getContentResolver(), Settings.Secure.ANDROID_ID);
                String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
                String email = emailEditTxt.getText().toString();
                String password = passwordEditTxt.getText().toString();

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("deviceId", deviceId);
                    jsonObject.put("deviceName", deviceName);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);

                    doLogin(jsonObject);
                } catch (JSONException e) {
                    Log.i("", Objects.requireNonNull(e.getMessage()));
                }
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION!", Toast.LENGTH_LONG).show();
            }
        });

        signupButton.setOnClickListener(btn -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        forgotPasswordLbl.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void doLogin(JSONObject loginForm) {
        loginButton.setEnabled(false);
        signupButton.setEnabled(false);

        final String LOGIN_URL = ipAddress + "/login";
        final MediaType JSON = MediaType.get("application/json");

        Log.i("", "URL: " + LOGIN_URL);

        RequestBody requestBody = RequestBody.create(loginForm.toString(), JSON);

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));

                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "SERVER ERROR!", Toast.LENGTH_LONG).show();
                    signinProgress.setVisibility(View.INVISIBLE);
                    loginButton.setEnabled(true);
                    signupButton.setEnabled(true);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ObjectMapper mapper = new ObjectMapper();

                assert response.body() != null;
                String body = response.body().string();
                SignInResult signInResult = mapper.readValue(body, SignInResult.class);

                if (signInResult.getStatus().equals("EMAIL_NOT_FOUND")) {
                    runOnUiThread(() -> {
                        Toast toast = new Toast(getApplicationContext());
                        toast.setText("EMAIL NOT FOUND!");
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();

                        emailEditTxt.setError("EMAIL NOT FOUND!", drawable);
                    });
                } else if (signInResult.getStatus().equals(("AUTHENTICATION_FAILED"))) {
                    runOnUiThread(() -> {
                        Toast toast = new Toast(getApplicationContext());
                        toast.setText("WRONG PASSWORD!");
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.show();
                    });
                } else if (signInResult.getStatus().equals("AUTHENTICATION_SUCCESS")) {
                    @SuppressLint("HardwareIds")
                    String deviceId = Settings.Secure.getString(getApplicationContext()
                            .getContentResolver(), Settings.Secure.ANDROID_ID);
                    String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

                    RemoteUserService remoteUserService = new RemoteUserService(activityRef);
                    UserCredential credential = UserCredential.builder()
                            .token(signInResult.getToken())
                            .deviceId(deviceId)
                            .deviceName(deviceName)
                            .build();

                    remoteUserService.fetchUserData(credential);
                } else if (signInResult.getStatus().equals("NEED_VERIFICATION")) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Please Verify your login!", Toast.LENGTH_LONG).show();
                    });

                    Intent intent = new Intent(getApplicationContext(), SignInVerificationActivity.class);
                    intent.putExtra("formId", signInResult.getFormId());
                    startActivity(intent);
                }

                runOnUiThread(() -> {
                    signinProgress.setVisibility(View.INVISIBLE);
                    loginButton.setEnabled(true);
                    signupButton.setEnabled(true);
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> finishAffinity())
                .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                    // CODE
                }))
                .show();
    }

    private static final int REQUEST_PERMISSIONS = 1;

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.i("", "Permission Granted!");
            } else {
                // Permission denied
                Log.i("", "Permission Denied!");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
