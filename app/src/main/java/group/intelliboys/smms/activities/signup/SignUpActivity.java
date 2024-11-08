package group.intelliboys.smms.activities.signup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class SignUpActivity extends AppCompatActivity {
    @Getter
    private EditText signupEmailField;
    @Getter
    private EditText signupPhoneNumField;
    private EditText signupPasswordField;
    private EditText signupConfirmPassField;
    @Getter
    private Button signupNextButton;

    private boolean isEmailValid, isPhoneNumberValid, isPasswordValid, isPasswordSame;
    private final NetworkConfig networkConfig = NetworkConfig.getInstance();
    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmailField = findViewById(R.id.signupEmailField);
        signupPhoneNumField = findViewById(R.id.signupPhoneNumField);
        signupPasswordField = findViewById(R.id.signupPasswordField);
        signupConfirmPassField = findViewById(R.id.signupConfirmPassField);
        signupNextButton = findViewById(R.id.signupNextButton);

        signupEmailField.addTextChangedListener(new TextWatcher() {
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
                        signupEmailField.setError("Invalid Email!");
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupPhoneNumField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String PHONE_NUMBER_REGEX = "^(\\+639|09)\\d{9}$";
            private final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);

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
                    String phoneNumber = s.toString();
                    Matcher matcher = pattern.matcher(phoneNumber);

                    if (matcher.matches()) {
                        isPhoneNumberValid = true;
                    } else {
                        isPhoneNumberValid = false;
                        signupPhoneNumField.setError("Invalid Phone Number!");
                    }

                    isSignupFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupPasswordField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            private final Pattern pattern = Pattern.compile(PASSWORD_REGEX);

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
                    Matcher matcher = pattern.matcher(password);

                    if (matcher.matches()) {
                        isPasswordValid = true;
                    } else {
                        isPasswordValid = false;
                        signupPasswordField.setError("Invalid Password!");
                    }

                    isSignupFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupConfirmPassField.addTextChangedListener(new TextWatcher() {
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
                    String password = signupPasswordField.getText().toString();
                    String confirmPassword = s.toString();

                    if (confirmPassword.equals(password)) {
                        isPasswordSame = true;
                    } else {
                        isPasswordSame = false;
                        signupConfirmPassField.setError("Invalid Confirm Password!");
                    }

                    isSignupFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupNextButton.setOnClickListener(v -> {
            // FOR CLICKING NEXT BUTTON
            signupNextButton.setEnabled(false);

            if (serverAPIs == null) {
                serverAPIs = new ServerAPIs(this);
            }

            String email = signupEmailField.getText().toString();
            String phoneNumber = signupPhoneNumField.getText().toString();
            String password = signupPasswordField.getText().toString();
            String confirmPassword = signupConfirmPassField.getText().toString();

            try {
                JSONObject formData = new JSONObject();
                formData.put("email", email);
                formData.put("phoneNumber", phoneNumber);
                formData.put("password", password);
                formData.put("confirmPassword", confirmPassword);

                serverAPIs.verifyEmailAndPhoneNumberIfNotExists(formData);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Commons.toastMessage(SignUpActivity.this, "Something went wrong!");
                    signupNextButton.setEnabled(true);
                });
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    private boolean isSignupFormValid() {
        boolean isFormValid = isEmailValid && isPhoneNumberValid
                && isPasswordValid && isPasswordSame;
        signupNextButton.setEnabled(isFormValid);

        return isFormValid;
    }
}
