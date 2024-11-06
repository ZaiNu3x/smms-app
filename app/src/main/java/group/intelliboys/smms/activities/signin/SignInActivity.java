package group.intelliboys.smms.activities.signin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.activities.forgot_password.ForgotPasswordActivity;
import group.intelliboys.smms.activities.signup.SignUpActivity;

public class SignInActivity extends AppCompatActivity {
    private EditText signInEmailField;
    private EditText signInPasswordField;
    private TextView signInForgotPasswordLbl;
    private Button signInButton;
    private Button signUpButton;
    private ProgressBar signInProgress;

    private boolean isEmailValid, isPasswordValid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInEmailField = findViewById(R.id.signInEmailField);
        signInPasswordField = findViewById(R.id.signInPasswordField);
        signInForgotPasswordLbl = findViewById(R.id.signInForgotPasswordLbl);
        signInButton = findViewById(R.id.sigInButton);
        signUpButton = findViewById(R.id.signUpButton);
        signInProgress = findViewById(R.id.signInProgress);

        signInEmailField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

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
                    String email = s.toString();
                    Matcher matcher = pattern.matcher(email);

                    if (matcher.matches()) {
                        isEmailValid = true;
                    } else {
                        isEmailValid = false;
                        signInEmailField.setError("INVALID EMAIL!");
                    }

                    isEmailAndPasswordAreValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signInPasswordField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;

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

                    if (password.length() > 8) {
                        isPasswordValid = true;
                    } else isPasswordValid = false;

                    isEmailAndPasswordAreValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signInForgotPasswordLbl.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> {
            // CODES FOR SIGNING IN INTO SYSTEM

        });

        signUpButton.setOnClickListener(V -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void isEmailAndPasswordAreValid() {
        signInButton.setEnabled(isEmailValid && isPasswordValid);
    }
}
