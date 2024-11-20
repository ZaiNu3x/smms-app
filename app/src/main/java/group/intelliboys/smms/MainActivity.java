package group.intelliboys.smms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import group.intelliboys.smms.activities.dashboard.HomeActivity;
import group.intelliboys.smms.activities.signin.SignInActivity;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.orm.repository.UserRepository;
import group.intelliboys.smms.services.DatabaseService;
import group.intelliboys.smms.utils.Executor;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Executor.run(() -> {
            // CHECK IF USER CACHE PRESENT IN THE DATABASE
            User authenticatedUser = new UserRepository(DatabaseService.getInstance(getApplicationContext()))
                    .getAuthenticatedUser();

            if (authenticatedUser != null) {
                // PREPARE FIRST THE USER DATA BEFORE REDIRECTING TO HOME
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
