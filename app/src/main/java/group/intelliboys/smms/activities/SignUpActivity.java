package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.results.RegistrationResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private EditText confirmPasswordEditTxt;
    private EditText phoneNumberEditTxt;
    private Button nextBtn;

    private boolean isEmailValid, isPhoneNumberValid, isPasswordValid, isConfirmPasswordValid;
    private OkHttpClient okHttpClient;

    private String ipAddress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        nextBtn = findViewById(R.id.nextBtn);
        emailEditTxt = findViewById(R.id.signUpEmailEditTxt);
        passwordEditTxt = findViewById(R.id.signUpPasswordEditTxt);
        confirmPasswordEditTxt = findViewById(R.id.signUpConfirmPasswordEditTxt);
        phoneNumberEditTxt = findViewById(R.id.signUpPhoneNumberEditTxt);

        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());

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

                    if (isEmailValid) {
                        emailEditTxt.setError(null);
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        emailEditTxt.setError("INVALID EMAIL ADDRESS!", drawable);
                    }

                    isFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        phoneNumberEditTxt.addTextChangedListener(new TextWatcher() {
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
                    isPhoneNumberValid = s.toString().startsWith("09") && s.length() == 11;

                    if (isPhoneNumberValid) {
                        phoneNumberEditTxt.setError(null);
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        phoneNumberEditTxt.setError("Invalid Phone Number!", drawable);
                    }

                    isFormValid();
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
                    String password = s.toString();
                    boolean containsUpperCase = false;
                    boolean containsLowerCase = false;
                    boolean containsSpecialChar;
                    boolean containsDigit = false;

                    for (char c : password.toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            containsUpperCase = true;
                        }

                        if (Character.isLowerCase(c)) {
                            containsLowerCase = true;
                        }

                        if (Character.isDigit(c)) {
                            containsDigit = true;
                        }
                    }

                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(password);

                    containsSpecialChar = matcher.find();

                    isPasswordValid = (password.length() >= 7 && password.length() <= 32) && containsUpperCase
                            && containsLowerCase && containsSpecialChar && containsDigit;

                    if (isPasswordValid) {
                        passwordEditTxt.setError(null);
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        passwordEditTxt.setError("INVALID PASSWORD! \n" +
                                "Password Must Contains; \n" +
                                "1. 8+ characters \n" +
                                "2. Uppercase Letter \n" +
                                "3. Lowercase Character \n" +
                                "4. Special Character \n" +
                                "5. Number", drawable);
                    }

                    isFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        confirmPasswordEditTxt.addTextChangedListener(new TextWatcher() {
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
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String password = passwordEditTxt.getText().toString();
                    String confirmPassword = confirmPasswordEditTxt.getText().toString();

                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = getDrawable(R.drawable.error);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    isConfirmPasswordValid = confirmPassword.equals(password);

                    if (isConfirmPasswordValid) {
                        confirmPasswordEditTxt.setError(null);
                    } else {
                        confirmPasswordEditTxt.setError("Password not matches!", drawable);
                    }

                    isFormValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        nextBtn.setOnClickListener(btn -> {
            String email = emailEditTxt.getText().toString();
            String phoneNumber = phoneNumberEditTxt.getText().toString();

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("email", email);
                jsonObject.put("phoneNumber", phoneNumber);
            } catch (JSONException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            NetworkConfig networkConfig = NetworkConfig.getInstance();

            if (networkConfig.isNetworkActive()) {
                verifyCredentialIfNotExists(jsonObject);
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void isFormValid() {
        nextBtn.setEnabled(isEmailValid && isPhoneNumberValid &&
                isPasswordValid && isConfirmPasswordValid);
    }

    private void verifyCredentialIfNotExists(JSONObject regForm) {
        nextBtn.setEnabled(false);

        if (ipAddress == null) {
            ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        }

        final String VERIFICATION_URL = ipAddress + "/register/is-account-exists";
        final MediaType JSON = MediaType.get("application/json");

        RequestBody requestBody = RequestBody.create(regForm.toString(), JSON);

        Request request = new Request.Builder()
                .url(VERIFICATION_URL)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_LONG).show();
                    nextBtn.setEnabled(true);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();
                ObjectMapper mapper = new ObjectMapper();

                RegistrationResult result = mapper.readValue(responseBody, RegistrationResult.class);

                if (result != null) {

                    if (result.getStatus().equals("EMAIL_PHONE_NOT_NUMBER_EXISTS")) {
                        runOnUiThread(() -> {
                            String formId = UUID.randomUUID().toString();
                            String email = emailEditTxt.getText().toString();
                            String phoneNumber = phoneNumberEditTxt.getText().toString();
                            String password = passwordEditTxt.getText().toString();
                            String confirmPassword = confirmPasswordEditTxt.getText().toString();

                            Intent intent = new Intent(getApplicationContext(), SignUpProfileActivity.class);

                            intent.putExtra("formId", formId);
                            intent.putExtra("email", email);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("password", password);
                            intent.putExtra("confirmPassword", confirmPassword);

                            startActivity(intent);
                        });
                    } else if (result.getStatus().equals("EMAIL_PHONE_NUMBER_EXISTS")) {

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Email or Phone number already taken!", Toast.LENGTH_LONG).show();

                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getDrawable(R.drawable.error);
                            assert drawable != null;
                            drawable.setBounds(0, 0, 45, 45);
                        });
                    }

                    if (result.isEmailExists()) {
                        runOnUiThread(() -> {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getDrawable(R.drawable.error);
                            assert drawable != null;
                            drawable.setBounds(0, 0, 45, 45);

                            emailEditTxt.setError("Email already taken!", drawable);
                        });
                    } else {
                        runOnUiThread(() -> {
                            emailEditTxt.setError(null);
                        });
                    }

                    if (result.isPhoneNumberExists()) {
                        runOnUiThread(() -> {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable drawable = getDrawable(R.drawable.error);
                            assert drawable != null;
                            drawable.setBounds(0, 0, 45, 45);

                            phoneNumberEditTxt.setError("Phone Number already taken!", drawable);
                        });
                    } else {
                        runOnUiThread(() -> {
                            phoneNumberEditTxt.setError(null);
                        });
                    }
                }

                runOnUiThread(() -> {
                    nextBtn.setEnabled(true);
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> super.onBackPressed())
                .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                    // CODE
                }))
                .show();
    }
}
