package group.intelliboys.smms.fragments.forgot_password;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.utils.ServerAPIs;

public class ForgotPasswordFragment extends Fragment {
    private EditText newPasswordField;
    private EditText confirmPasswordField;
    private Button submitBtn;

    private ServerAPIs serverAPIs;
    private boolean isNewPasswordValid, isConfirmPasswordValid;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        newPasswordField = view.findViewById(R.id.forgotPasswordNewPassField);
        confirmPasswordField = view.findViewById(R.id.forgotPasswordConfirmPassField);
        submitBtn = view.findViewById(R.id.forgotPasswordSubmitBtn);

        serverAPIs = new ServerAPIs(requireActivity());

        newPasswordField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;
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
                    String newPassword = s.toString();
                    Matcher matcher = pattern.matcher(newPassword);

                    if (matcher.matches()) {
                        isNewPasswordValid = true;
                        isPasswordSame();
                    } else {
                        isNewPasswordValid = false;
                        newPasswordField.setError("Invalid Password!");
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        confirmPasswordField.addTextChangedListener(new TextWatcher() {
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
                    String newPassword = newPasswordField.getText().toString();
                    String confirmPassword = s.toString();

                    if (confirmPassword.equals(newPassword)) {
                        isConfirmPasswordValid = true;
                        isPasswordSame();
                    } else {
                        isConfirmPasswordValid = false;
                        confirmPasswordField.setError("Confirm Password Incorrect!");
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        submitBtn.setOnClickListener(v -> {
            assert getArguments() != null;
            String id = getArguments().getString("id");
            String newPassword = newPasswordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();

            try {
                JSONObject form = new JSONObject();
                form.put("formId", id);
                form.put("newPassword", newPassword);
                form.put("confirmPassword", confirmPassword);

                serverAPIs.forgotPasswordSubmit(form);
            } catch (Exception e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        });

        return view;
    }

    private void isPasswordSame() {
        submitBtn.setEnabled(isNewPasswordValid && isConfirmPasswordValid);
    }
}
