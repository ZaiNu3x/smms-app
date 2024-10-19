package group.intelliboys.smms.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.results.OtpVerificationResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordVerificationFragment extends Fragment {
    private EditText emailOtpEditTxt;
    private EditText smsOtpEditTxt;
    private TextView resendEmailOtpLbl;
    private TextView resendSmsOtpLbl;
    private TextView emailOtpTimer;
    private TextView smsOtpTimer;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_verification, container, false);
        emailOtpEditTxt = view.findViewById(R.id.fgpNewPassEditTxt);
        smsOtpEditTxt = view.findViewById(R.id.fgpConfirmPassEditTxt);
        resendEmailOtpLbl = view.findViewById(R.id.fgpResendEmailOtpLbl);
        resendSmsOtpLbl = view.findViewById(R.id.fgpResendSmsOtpLbl);
        emailOtpTimer = view.findViewById(R.id.fgpEmailOtpTimer);
        smsOtpTimer = view.findViewById(R.id.fgpSmsOtpTimer);
        Button submit = view.findViewById(R.id.fgpSubmitBtn);
        okHttpClient = CustomOkHttpClient.getOkHttpClient(requireContext());

        if (NetworkConfig.getInstance().isNetworkActive()) {
            ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        }

        resendEmailOtpTimer();
        resendSmsOtpTimer();

        resendEmailOtpLbl.setOnClickListener(v -> {
            resendEmailOtpTimer();
        });

        resendSmsOtpLbl.setOnClickListener(v -> {
            resendSmsOtpTimer();
        });

        submit.setOnClickListener(v -> {
            Bundle bundle = this.getArguments();
            String smsOtp = smsOtpEditTxt.getText().toString();
            String emailOtp = emailOtpEditTxt.getText().toString();

            if (bundle != null) {
                JSONObject changePasswordToken = new JSONObject();
                try {
                    changePasswordToken.put("id", bundle.getString("id"));
                    changePasswordToken.put("smsOtp", smsOtp);
                    changePasswordToken.put("emailOtp", emailOtp);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                verifyOtp(changePasswordToken);
            }
        });

        return view;
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

    public void verifyOtp(JSONObject token) {
        final String URL = ipAddress + "/forgot-password/verify-otp";

        final MediaType JSON = MediaType.get("application/json");
        RequestBody requestBody = RequestBody.create(token.toString(), JSON);

        Request request = new Request.Builder()
                .post(requestBody)
                .url(URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String body = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();

                    OtpVerificationResult result = mapper.readValue(body, OtpVerificationResult.class);

                    if (result.isEmailSame()) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.check);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            emailOtpEditTxt.setCompoundDrawables(null, null, drawable, null);
                        });
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            emailOtpEditTxt.setError("WRONG OTP!", drawable);
                        });
                    }

                    if (result.isSmsOtpSame()) {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.check);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            smsOtpEditTxt.setCompoundDrawables(null, null, drawable, null);
                        });
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            smsOtpEditTxt.setError("WRONG OTP!", drawable);
                        });
                    }

                    if (result.isSmsOtpSame() && result.isEmailSame()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("formId", result.getId());
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.forgotPasswordContainer, ForgotPasswordFragment.class, bundle).commit();
                    }
                }
            }
        });
    }
}
