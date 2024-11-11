package group.intelliboys.smms.activities.signup;

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

public class SignUpVerificationActivity extends AppCompatActivity {
    @Getter
    private EditText signupVerEmailOtpField;
    private TextView signupVerResendEmailOtpLbl;
    private TextView signupVerEmailTimerLbl;
    @Getter
    private EditText signupVerSmsOtpField;
    private TextView signupVerResendSmsOtpLbl;
    private TextView signupVerSmsTimerLbl;
    @Getter
    private Button signupVerSubmitBtn;

    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        signupVerEmailOtpField = findViewById(R.id.verEmailOtpField);
        signupVerResendEmailOtpLbl = findViewById(R.id.verResendEmailOtpLbl);
        signupVerEmailTimerLbl = findViewById(R.id.verEmailTimerLbl);
        signupVerSmsOtpField = findViewById(R.id.verSmsOtpField);
        signupVerResendSmsOtpLbl = findViewById(R.id.verResendSmsOtpLbl);
        signupVerSmsTimerLbl = findViewById(R.id.verSmsTimerLbl);
        signupVerSubmitBtn = findViewById(R.id.verSubmitBtn);

        serverAPIs = new ServerAPIs(this);

        startCountdownOnSmsOtp();
        startCountdownOnEmailOtp();

        signupVerResendEmailOtpLbl.setOnClickListener(v -> {
            startCountdownOnEmailOtp();
            serverAPIs.resendRegistrationEmailOtp();
        });

        signupVerResendSmsOtpLbl.setOnClickListener(v -> {
            startCountdownOnSmsOtp();
            serverAPIs.resendRegistrationSmsOtp();
        });

        signupVerSubmitBtn.setOnClickListener(v -> {
            signupVerSubmitBtn.setEnabled(false);

            try {
                String formId = getIntent().getStringExtra("formId");
                String emailOtp = signupVerEmailOtpField.getText().toString();
                String smsOtp = signupVerSmsOtpField.getText().toString();

                JSONObject form = new JSONObject();
                form.put("formId", formId);
                form.put("emailOtp", emailOtp);
                form.put("smsOtp", smsOtp);

                Log.i("", form.toString());
                serverAPIs.verifyRegistrationOtp(form);
            } catch (Exception e) {
                Commons.toastMessage(this, "Something went wrong!");
                Log.i("", Objects.requireNonNull(e.getMessage()));
                signupVerSubmitBtn.setEnabled(true);
            }
        });
    }

    private void startCountdownOnEmailOtp() {
        signupVerResendEmailOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                signupVerEmailTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                signupVerResendEmailOtpLbl.setEnabled(true);
            }
        }.start();
    }

    private void startCountdownOnSmsOtp() {
        signupVerResendSmsOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                signupVerSmsTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                signupVerResendSmsOtpLbl.setEnabled(true);
            }
        }.start();
    }
}
