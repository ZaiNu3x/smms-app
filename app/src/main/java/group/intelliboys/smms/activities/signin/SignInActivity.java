package group.intelliboys.smms.activities.signin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.forgot_password.ForgotPasswordActivity;
import group.intelliboys.smms.activities.signup.SignUpActivity;
import group.intelliboys.smms.configs.DeviceSpecs;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;
import okhttp3.OkHttpClient;

public class SignInActivity extends AppCompatActivity {
    @Getter
    private EditText signInEmailField;

    @Getter
    private EditText signInPasswordField;

    private TextView signInForgotPasswordLbl;
    private Button signInButton;
    private Button signUpButton;

    @Getter
    private ProgressBar signInProgress;

    private boolean isEmailValid, isPasswordValid;

    private final NetworkConfig networkConfig = NetworkConfig.getInstance();
    private String serverIpAddress;
    private OkHttpClient httpClient;
    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInEmailField = findViewById(R.id.signInEmailField);
        signInPasswordField = findViewById(R.id.signInPasswordField);
        signInForgotPasswordLbl = findViewById(R.id.signInForgotPasswordLbl);
        signInButton = findViewById(R.id.sigInButton);
        signUpButton = findViewById(R.id.signUpButton);
        signInProgress = findViewById(R.id.signInProgress);

        signInEmailField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

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
                    String email = s.toString();
                    Matcher matcher = pattern.matcher(email);

                    if (matcher.matches()) {
                        isEmailValid = true;
                    } else {
                        isEmailValid = false;
                        signInEmailField.setError("INVALID EMAIL!");
                    }

                    isEmailAndPasswordAreValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signInPasswordField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;

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
                    String password = s.toString();

                    isPasswordValid = password.length() > 8;
                    isEmailAndPasswordAreValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signInForgotPasswordLbl.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> {
            // CODES FOR SIGNING IN INTO SYSTEM
            signInProgress.setVisibility(View.VISIBLE);
            if (serverAPIs == null) {
                serverAPIs = new ServerAPIs(this);
            }

            String email = signInEmailField.getText().toString();
            String password = signInPasswordField.getText().toString();

            try {
                JSONObject loginCredential = new JSONObject();
                loginCredential.put("email", email);
                loginCredential.put("password", password);
                loginCredential.put("deviceId", DeviceSpecs.DEVICE_ID);
                loginCredential.put("deviceName", DeviceSpecs.DEVICE_NAME);

                serverAPIs.signIn(loginCredential);
            } catch (JSONException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        });

        signUpButton.setOnClickListener(V -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void isEmailAndPasswordAreValid() {
        signInButton.setEnabled(isEmailValid && isPasswordValid);
    }
}
