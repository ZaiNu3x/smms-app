package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
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
import java.util.Objects;

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

public class SignInVerificationActivity extends AppCompatActivity {

    private EditText emailOtpEditTxt;
    private EditText smsOtpEditTxt;
    private TextView resendEmailOtpLbl;
    private TextView resendSmsOtpLbl;
    private TextView emailOtpTimer;
    private TextView smsOtpTimer;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        emailOtpEditTxt = findViewById(R.id.fgpNewPassEditTxt);
        smsOtpEditTxt = findViewById(R.id.fgpConfirmPassEditTxt);
        resendEmailOtpLbl = findViewById(R.id.fgpResendEmailOtpLbl);
        resendSmsOtpLbl = findViewById(R.id.fgpResendSmsOtpLbl);
        emailOtpTimer = findViewById(R.id.fgpEmailOtpTimer);
        smsOtpTimer = findViewById(R.id.fgpSmsOtpTimer);
        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());
        Button submitBtn = findViewById(R.id.fgpSubmitBtn);

        resendEmailOtpLbl.setOnClickListener(lbl -> {
            resendEmailOtpTimer();
            resendEmailOtp();
        });

        resendSmsOtpLbl.setOnClickListener(lbl -> {
            resendSmsOtpTimer();
            resendSmsOtp();
        });

        submitBtn.setOnClickListener(btn -> {
            String formId = getIntent().getStringExtra("formId");
            String emailOtp = emailOtpEditTxt.getText().toString();
            String smsOtp = smsOtpEditTxt.getText().toString();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("formId", formId);
                jsonObject.put("emailOtp", emailOtp);
                jsonObject.put("smsOtp", smsOtp);

                doVerify(jsonObject);
            } catch (JSONException e) {
                Log.i("", e.getMessage());
            }
        });

        resendEmailOtpTimer();
        resendSmsOtpTimer();
    }

    private void resendEmailOtpTimer() {
        resendEmailOtpLbl.setVisibility(View.INVISIBLE);
        emailOtpTimer.setVisibility(View.VISIBLE);

        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long ms) {
                emailOtpTimer.setText(Long.toString(ms / 1000));
            }

            @Override
            public void onFinish() {
                emailOtpTimer.setVisibility(View.INVISIBLE);
                resendEmailOtpLbl.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void resendSmsOtpTimer() {
        resendSmsOtpLbl.setVisibility(View.INVISIBLE);
        smsOtpTimer.setVisibility(View.VISIBLE);

        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long ms) {
                smsOtpTimer.setText(Long.toString(ms / 1000));
            }

            @Override
            public void onFinish() {
                smsOtpTimer.setVisibility(View.INVISIBLE);
                resendSmsOtpLbl.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void resendEmailOtp() {
        String formId = getIntent().getStringExtra("formId");
        final String RESEND_EMAIL_OTP_URL = ipAddress + "/login/2fa/resend/email-otp/" + formId;

        Request request = new Request.Builder()
                .get()
                .url(RESEND_EMAIL_OTP_URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setText("FAILED TO RESEND EMAIL OTP!");
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setText("NEW EMAIL OTP HAS RESENT!");
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                });
            }
        });
    }

    private void resendSmsOtp() {
        String formId = getIntent().getStringExtra("formId");
        final String RESEND_SMS_OTP_URL = ipAddress + "/login/2fa/resend/sms-otp/" + formId;

        Request request = new Request.Builder()
                .get()
                .url(RESEND_SMS_OTP_URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setText("FAILED TO RESEND SMS OTP!");
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setText("NEW SMS OTP HAS RESENT!");
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                });
            }
        });
    }

    private void doVerify(JSONObject jsonObject) {
        final String VERIFICATION_URL = ipAddress + "/login/2fa/verify";
        final MediaType JSON = MediaType.get("application/json");

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(VERIFICATION_URL)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();

                ObjectMapper mapper = new ObjectMapper();
                TwoFAVerificationResult verificationResult = mapper.readValue(responseBody, TwoFAVerificationResult.class);

                if (verificationResult.isEmailOtpMatches()) {
                    Drawable drawable = getDrawable(R.drawable.check);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    runOnUiThread(() -> {
                        emailOtpEditTxt.setCompoundDrawables(null, null, drawable, null);
                    });
                } else {
                    Drawable drawable = getDrawable(R.drawable.error);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    runOnUiThread(() -> {
                        emailOtpEditTxt.setError("WRONG OTP!", drawable);
                    });
                }

                if (verificationResult.isSmsOtpMatches()) {
                    Drawable drawable = getDrawable(R.drawable.check);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    runOnUiThread(() -> {
                        smsOtpEditTxt.setCompoundDrawables(null, null, drawable, null);
                    });
                } else {
                    Drawable drawable = getDrawable(R.drawable.error);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    runOnUiThread(() -> {
                        smsOtpEditTxt.setError("WRONG OTP!", drawable);
                    });
                }

                if (verificationResult.getStatus().equals("VERIFIED")) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "VERIFICATION SUCCESS!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("token", verificationResult.getToken());
                        startActivity(intent);
                    });
                }
            }
        });
    }
}
