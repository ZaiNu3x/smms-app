package group.intelliboys.smms.fragments.forgot_password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class ForgotPasswordVerificationFragment extends Fragment {
    @Getter
    private EditText forgotPassVerEmailOtpField;
    private TextView forgotPassVerResendEmailOtpLbl;
    private TextView forgotPassVerEmailTimerLbl;
    @Getter
    private EditText forgotPassVerSmsOtpField;
    private TextView forgotPassVerResendSmsOtpLbl;
    private TextView forgotPassVerSmsTimerLbl;
    @Getter
    private Button forgotPassVerSubmitBtn;

    private ServerAPIs serverAPIs;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_otp_verification, container, false);

        forgotPassVerEmailOtpField = view.findViewById(R.id.verEmailOtpField);
        forgotPassVerResendEmailOtpLbl = view.findViewById(R.id.verResendEmailOtpLbl);
        forgotPassVerEmailTimerLbl = view.findViewById(R.id.verEmailTimerLbl);
        forgotPassVerSmsOtpField = view.findViewById(R.id.verSmsOtpField);
        forgotPassVerResendSmsOtpLbl = view.findViewById(R.id.verResendSmsOtpLbl);
        forgotPassVerSmsTimerLbl = view.findViewById(R.id.verSmsTimerLbl);
        forgotPassVerSubmitBtn = view.findViewById(R.id.verSubmitBtn);

        serverAPIs = new ServerAPIs(requireActivity());

        startCountdownOnSmsOtp();
        startCountdownOnEmailOtp();

        forgotPassVerResendEmailOtpLbl.setOnClickListener(v -> {
            startCountdownOnEmailOtp();
            serverAPIs.forgotPasswordResendEmailOtp();
        });

        forgotPassVerResendSmsOtpLbl.setOnClickListener(v -> {
            startCountdownOnSmsOtp();
            serverAPIs.forgotPasswordResendSmsOtp();
        });

        forgotPassVerSubmitBtn.setOnClickListener(v -> {
            forgotPassVerSubmitBtn.setEnabled(false);

            try {
                assert getArguments() != null;
                String formId = getArguments().getString("id");
                String emailOtp = forgotPassVerEmailOtpField.getText().toString();
                String smsOtp = forgotPassVerSmsOtpField.getText().toString();

                JSONObject form = new JSONObject();
                form.put("id", formId);
                form.put("emailOtp", emailOtp);
                form.put("smsOtp", smsOtp);

                serverAPIs.forgotPasswordSubmitOtp(form);
            } catch (Exception e) {
                Commons.toastMessage(requireActivity(), "Something went wrong!");
                Log.i("", Objects.requireNonNull(e.getMessage()));
                forgotPassVerSubmitBtn.setEnabled(true);
            }
        });

        return view;
    }

    private void startCountdownOnEmailOtp() {
        forgotPassVerResendEmailOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                forgotPassVerEmailTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                forgotPassVerResendEmailOtpLbl.setEnabled(true);
            }
        }.start();
    }

    private void startCountdownOnSmsOtp() {
        forgotPassVerResendSmsOtpLbl.setEnabled(false);

        // Countdown timer for 30 seconds
        new CountDownTimer(30000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                // Display the remaining seconds
                forgotPassVerSmsTimerLbl.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                // Countdown finished
                forgotPassVerResendSmsOtpLbl.setEnabled(true);
            }
        }.start();
    }
}
