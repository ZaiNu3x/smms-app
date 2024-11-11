package group.intelliboys.smms.activities.signin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class SignInVerificationActivity extends AppCompatActivity {
    @Getter
    private EditText signinVerEmailOtpField;
    private TextView signinVerResendEmailOtpLbl;
    private TextView signinVerEmailTimerLbl;
    @Getter
    private EditText signinVerSmsOtpField;
    private TextView signinVerResendSmsOtpLbl;
    private TextView signinVerSmsTimerLbl;
    @Getter
    private Button signinVerSubmitBtn;

    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        signinVerEmailOtpField = findViewById(R.id.verEmailOtpField);
        signinVerResendEmailOtpLbl = findViewById(R.id.verResendEmailOtpLbl);
        signinVerEmailTimerLbl = findViewById(R.id.verEmailTimerLbl);
        signinVerSmsOtpField = findViewById(R.id.verSmsOtpField);
        signinVerResendSmsOtpLbl = findViewById(R.id.verResendSmsOtpLbl);
        signinVerSmsTimerLbl = findViewById(R.id.verSmsTimerLbl);
        signinVerSubmitBtn = findViewById(R.id.verSubmitBtn);

        serverAPIs = new ServerAPIs(this);

        startCountdownOnSmsOtp();
        startCountdownOnEmailOtp();

        signinVerResendEmailOtpLbl.setOnClickListener(v -> {
            startCountdownOnEmailOtp();
            serverAPIs.resendLoginEmailOtp();
        });

        signinVerResendSmsOtpLbl.setOnClickListener(v -> {
            startCountdownOnSmsOtp();
            serverAPIs.resendLoginSmsOtp();
        });

        signinVerSubmitBtn.setOnClickListener(v -> {
            signinVerSubmitBtn.setEnabled(false);

            try {
                String formId = getIntent().getStringExtra("formId");
                String emailOtp = signinVerEmailOtpField.getText().toString();
                String smsOtp = signinVerSmsOtpField.getText().toString();

                JSONObject form = new JSONObject();
                form.put("formId", formId);
                form.put("emailOtp", emailOtp);
                form.put("smsOtp", smsOtp);

                serverAPIs.verifySignIn(form);
            } catch (Exception e) {
                Commons.toastMessage(this, "Something went wrong!");
                Log.i("", Objects.requireNonNull(e.getMessage()));
                signinVerSubmitBtn.setEnabled(true);
            }
        });
    }

    private void startCountdownOnEmailOtp() {
        signinVerResendEmailOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                signinVerEmailTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                signinVerResendEmailOtpLbl.setEnabled(true);
            }
        }.start();
    }

    private void startCountdownOnSmsOtp() {
        signinVerResendSmsOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                signinVerSmsTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                signinVerResendSmsOtpLbl.setEnabled(true);
            }
        }.start();
    }
}
