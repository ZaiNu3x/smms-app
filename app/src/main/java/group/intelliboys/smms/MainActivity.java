package group.intelliboys.smms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import group.intelliboys.smms.activities.SignInActivity;
import group.intelliboys.smms.activities.SignUpProfileActivity;
import group.intelliboys.smms.services.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.getInstance().setApplicationContext(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}