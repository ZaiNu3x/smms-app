package group.intelliboys.smms;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import group.intelliboys.smms.activities.HomeActivity;
import group.intelliboys.smms.activities.SignInActivity;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbUserService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.getInstance().setApplicationContext(getApplicationContext());

        LocalDbUserService userService = new LocalDbUserService(this);
        User loggedInUser = userService.retrieveLoggedInUser();

        if (loggedInUser != null) {
            Utils.getInstance().getLoggedInUser().updateUserModel(loggedInUser);

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        }
    }
}