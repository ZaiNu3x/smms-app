package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.results.TwoFAVerificationResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpVerificationActivity extends AppCompatActivity {
    private EditText emailOtpEditTxt, smsOtpEditTxt;
    private TextView resendEmailOtpLbl, resendSmsOtpLbl, emailOtpTimer, smsOtpTimer;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        initializeViews();
        setupOkHttpClient();
        setupButtonListeners();

        startResendTimers();
    }

    private void initializeViews() {
        emailOtpEditTxt = findViewById(R.id.fgpNewPassEditTxt);
        smsOtpEditTxt = findViewById(R.id.fgpConfirmPassEditTxt);
        resendEmailOtpLbl = findViewById(R.id.fgpResendEmailOtpLbl);
        resendSmsOtpLbl = findViewById(R.id.fgpResendSmsOtpLbl);
        emailOtpTimer = findViewById(R.id.fgpEmailOtpTimer);
        smsOtpTimer = findViewById(R.id.fgpSmsOtpTimer);
    }

    private void setupOkHttpClient() {
        ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());
    }

    private void setupButtonListeners() {
        Button submitBtn = findViewById(R.id.fgpSubmitBtn);
        submitBtn.setOnClickListener(view -> {
            try {
                String formId = getIntent().getStringExtra("formId");
                JSONObject regForm = createRegForm(formId);
                doVerify(regForm);
            } catch (JSONException e) {
                Log.e("SignUpVerificationActivity", "Error creating registration form", e);
            }
        });

        resendEmailOtpLbl.setOnClickListener(view -> {
            resendEmailOtp();
            startEmailOtpTimer();
        });

        resendSmsOtpLbl.setOnClickListener(view -> {
            resendSmsOtp();
            startSmsOtpTimer();
        });
    }

    private JSONObject createRegForm(String formId) throws JSONException {
        JSONObject regForm = new JSONObject();
        regForm.put("formId", formId);
        regForm.put("emailOtp", emailOtpEditTxt.getText().toString());
        regForm.put("smsOtp", smsOtpEditTxt.getText().toString());
        return regForm;
    }

    private void startResendTimers() {
        startEmailOtpTimer();
        startSmsOtpTimer();
    }

    private void startEmailOtpTimer() {
        startTimer(emailOtpTimer, resendEmailOtpLbl);
    }

    private void startSmsOtpTimer() {
        startTimer(smsOtpTimer, resendSmsOtpLbl);
    }

    private void startTimer(TextView timerView, TextView label) {
        label.setVisibility(View.INVISIBLE);
        timerView.setVisibility(View.VISIBLE);

        new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long ms) {
                timerView.setText(String.valueOf(ms / 1000));
            }

            @Override
            public void onFinish() {
                timerView.setVisibility(View.INVISIBLE);
                label.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void resendEmailOtp() {
        sendOtpRequest("/register/resend/email-otp/", "FAILED TO RESEND EMAIL OTP!", "NEW EMAIL OTP HAS RESENT!");
    }

    private void resendSmsOtp() {
        sendOtpRequest("/register/resend/sms-otp/", "FAILED TO RESEND SMS OTP!", "NEW SMS OTP HAS RESENT!");
    }

    private void sendOtpRequest(String endpoint, String failureMessage, String successMessage) {
        String formId = getIntent().getStringExtra("formId");
        String url = ipAddress + endpoint + formId;

        Request request = new Request.Builder().get().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast(failureMessage);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    showToast(successMessage);
                }
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    private void doVerify(JSONObject regForm) {
        String verificationUrl = ipAddress + "/register/verify";
        MediaType JSON = MediaType.get("application/json");
        RequestBody requestBody = RequestBody.create(regForm.toString(), JSON);

        Request request = new Request.Builder().url(verificationUrl).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SignUpVerificationActivity", "Verification request failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                handleVerificationResponse(response);
            }
        });
    }

    private void handleVerificationResponse(Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            Log.e("SignUpVerificationActivity", "Response was not successful or body is null");
            return;
        }

        String responseBody = response.body().string();
        ObjectMapper mapper = new ObjectMapper();
        TwoFAVerificationResult verificationResult = mapper.readValue(responseBody, TwoFAVerificationResult.class);
        Log.i("SignUpVerificationActivity", "Result: " + verificationResult);

        updateOtpFields(verificationResult);
        if ("VERIFICATION_SUCCESS".equals(verificationResult.getStatus())) {
            showToast("REGISTRATION SUCCESS!");
            navigateToSignIn();
        }
    }

    private void updateOtpFields(TwoFAVerificationResult verificationResult) {
        updateField(emailOtpEditTxt, verificationResult.isEmailOtpMatches(), "WRONG OTP!");
        updateField(smsOtpEditTxt, verificationResult.isSmsOtpMatches(), "WRONG OTP!");
    }

    private void updateField(EditText otpField, boolean isMatch, String errorMessage) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = getDrawable(isMatch ? R.drawable.check : R.drawable.error);
        if (drawable != null) {
            drawable.setBounds(0, 0, 45, 45);
            runOnUiThread(() -> {
                if (isMatch) {
                    otpField.setCompoundDrawables(null, null, drawable, null);
                } else {
                    otpField.setError(errorMessage, drawable);
                }
            });
        }
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
