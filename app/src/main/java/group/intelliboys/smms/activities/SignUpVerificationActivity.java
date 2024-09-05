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
import group.intelliboys.smms.models.results.TwoFAVerificationResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpVerificationActivity extends AppCompatActivity {

    private EditText emailOtpEditTxt;
    private EditText smsOtpEditTxt;
    private TextView resendEmailOtpLbl;
    private TextView resendSmsOtpLbl;
    private TextView emailOtpTimer;
    private TextView smsOtpTimer;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        emailOtpEditTxt = findViewById(R.id.emailOtpEditTxt);
        smsOtpEditTxt = findViewById(R.id.smsOtpEditTxt);
        resendEmailOtpLbl = findViewById(R.id.resendEmailOtpLbl);
        resendSmsOtpLbl = findViewById(R.id.resendSmsOtpLbl);
        emailOtpTimer = findViewById(R.id.emailOtpTimer);
        smsOtpTimer = findViewById(R.id.smsOtpTimer);
        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());
        Button submitBtn = findViewById(R.id.submitOtpBtn);

        resendEmailOtpTimer();
        resendSmsOtpTimer();

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

            JSONObject regForm = new JSONObject();

            try {
                regForm.put("formId", formId);
                regForm.put("emailOtp", emailOtp);
                regForm.put("smsOtp", smsOtp);
            } catch (JSONException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            doVerify(regForm);
        });
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
        final String RESEND_EMAIL_OTP_URL = "https://192.168.1.14:443/register/resend/email-otp/" + formId;

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
        final String RESEND_SMS_OTP_URL = "https://192.168.1.14:443/register/resend/sms-otp/" + formId;

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

    private void doVerify(JSONObject regForm) {
        final String VERIFICATION_URL = "https://192.168.1.14:443/register/verify";
        final MediaType JSON = MediaType.get("application/json");

        RequestBody requestBody = RequestBody.create(regForm.toString(), JSON);

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
                assert response.body() != null;
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

                if (verificationResult.getStatus().equals("VERIFICATION_SUCCESS")) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "VERIFICATION SUCCESS!", Toast.LENGTH_SHORT).show();
                    });

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("token", verificationResult.getToken());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}
