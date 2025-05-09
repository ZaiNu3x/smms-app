package group.intelliboys.smms.activities.forgot_password;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import group.intelliboys.smms.R;
import group.intelliboys.smms.fragments.forgot_password.SearchAccountFragment;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.forgotPasswordContainer, SearchAccountFragment.class, null)
                    .commit();
        }
    }
}
