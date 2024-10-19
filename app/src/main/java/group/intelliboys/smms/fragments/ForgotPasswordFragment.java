package group.intelliboys.smms.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.SignInActivity;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.results.ForgotPasswordResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordFragment extends Fragment {
    private EditText newPasswordEditTxt;
    private EditText confirmNewPasswordEditTxt;
    private Button submitBtn;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        newPasswordEditTxt = view.findViewById(R.id.fgpNewPassEditTxt);
        confirmNewPasswordEditTxt = view.findViewById(R.id.fgpConfirmPassEditTxt);
        submitBtn = view.findViewById(R.id.fgpSubmitBtn);

        if (NetworkConfig.getInstance().isNetworkActive()) {
            ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        }

        okHttpClient = CustomOkHttpClient.getOkHttpClient(requireContext());

        newPasswordEditTxt.addTextChangedListener(new TextWatcher() {
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

                    boolean isPasswordValid = (password.length() >= 7 && password.length() <= 32) && containsUpperCase
                            && containsLowerCase && containsSpecialChar && containsDigit;

                    if (isPasswordValid) {
                        newPasswordEditTxt.setError(null);
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.check);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            newPasswordEditTxt.setCompoundDrawables(null, null, drawable, null);
                        });
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        newPasswordEditTxt.setError("INVALID PASSWOD! \n" +
                                "Password Must Constains; \n" +
                                "1. 8+ characters \n" +
                                "2. Uppercase Letter \n" +
                                "3. Lowercase Character \n" +
                                "4. Special Character \n" +
                                "5. Number", drawable);
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        confirmNewPasswordEditTxt.addTextChangedListener(new TextWatcher() {
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
                    String newPassword = newPasswordEditTxt.getText().toString();
                    String confirmNewPassword = confirmNewPasswordEditTxt.getText().toString();

                    if (confirmNewPassword.equals(newPassword)) {
                        confirmNewPasswordEditTxt.setError(null);
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.check);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);

                        requireActivity().runOnUiThread(() -> {
                            confirmNewPasswordEditTxt.setCompoundDrawables(null, null, drawable, null);
                        });

                        submitBtn.setEnabled(true);
                    } else {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                        assert drawable != null;
                        drawable.setBounds(0, 0, 45, 45);
                        confirmNewPasswordEditTxt.setError("Password not matches!", drawable);
                        submitBtn.setEnabled(false);
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        submitBtn.setOnClickListener(v -> {
            final String URL = ipAddress + "/forgot-password/submit";
            String newPassword = newPasswordEditTxt.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditTxt.getText().toString();

            Log.i("", "Confirm Password: " + confirmNewPassword);

            assert getArguments() != null;
            String formId = getArguments().getString("formId");

            try {
                JSONObject json = new JSONObject();
                json.put("formId", formId);
                json.put("newPassword", newPassword);
                json.put("confirmPassword", confirmNewPassword);

                final MediaType JSON = MediaType.get("application/json");
                RequestBody requestBody = RequestBody.create(json.toString(), JSON);

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
                        assert response.body() != null;
                        String body = response.body().string();

                        ObjectMapper mapper = new ObjectMapper();
                        ForgotPasswordResult result = mapper.readValue(body, ForgotPasswordResult.class);

                        if (result.getStatus().equals("CHANGE_PASSWORD_SUCCESS")) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireActivity(), "PASSWORD CHANGED SUCCESSFUL!", Toast.LENGTH_LONG).show();
                            });

                            Intent intent = new Intent(requireActivity(), SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            requireActivity().startActivity(intent);
                        } else if (result.getStatus().equals("CHANGE_PASSWORD_FAILED")) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireActivity(), "PASSWORD CHANGED FAILED!", Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                });
            } catch (JSONException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        });

        return view;
    }
}
