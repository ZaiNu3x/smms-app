package group.intelliboys.smms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.SerializationUtils;

import group.intelliboys.smms.activities.dashboard.HomeActivity;
import group.intelliboys.smms.activities.signin.SignInActivity;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.orm.repository.UserRepository;
import group.intelliboys.smms.security.SecurityContextHolder;
import group.intelliboys.smms.utils.ContextHolder;
import group.intelliboys.smms.utils.Executor;

public class MainActivity extends AppCompatActivity {
    public static String accidentEmailRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SETUP
        ContextHolder.getInstance().setContext(getApplicationContext());

        Executor.run(() -> {
            // PREPARE FIRST THE USER DATA BEFORE REDIRECTING TO HOME
            // CHECK IF USER CACHE PRESENT IN THE DATABASE
            UserRepository userRepository = new UserRepository();
            User authenticatedUser = userRepository.getAuthenticatedUser();

            if (authenticatedUser != null) {
                SecurityContextHolder.getInstance().setAuthenticatedUser(
                        SerializationUtils.clone(authenticatedUser)
                );

                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                userRepository.deleteAllUsers();
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
