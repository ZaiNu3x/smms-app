package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;
import group.intelliboys.smms.R;
import group.intelliboys.smms.fragments.AccidentHistoryFragment;
import group.intelliboys.smms.fragments.ClubsFragment;
import group.intelliboys.smms.fragments.HomeFragment;
import group.intelliboys.smms.fragments.MonitoringFragment;
import group.intelliboys.smms.fragments.ProfileFragment;
import group.intelliboys.smms.fragments.SettingsFragment;
import group.intelliboys.smms.fragments.TravelHistoryFragment;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.services.local.LocalDbUserService;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private User user;
    private View navHeader;
    private CircleImageView profilePic;
    private TextView navUsername;
    private TextView navUserEmail;

    @SuppressLint({"CommitTransaction", "ResourceType", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
        user = (User) getIntent().getSerializableExtra("user_details");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        profilePic = navHeader.findViewById(R.id.navProfilePicView);
        navUsername = navHeader.findViewById(R.id.navUserName);
        navUserEmail = navHeader.findViewById(R.id.navUserEmail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MotoPotato");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_nav, R.string.close_nav);

        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.class, null).commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(user.getProfilePic(),
                0, user.getProfilePic().length);
        profilePic.setImageBitmap(bitmap);

        StringBuilder builder = new StringBuilder();
        builder.append(user.getLastName())
                .append(" ")
                .append(user.getFirstName()).append(" ")
                .append(user.getMiddleName().charAt(0))
                .append(".");

        navUsername.setText(new String(builder));
        navUserEmail.setText(user.getEmail());

        profilePic.setOnClickListener((view) -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.class, null).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).commit();
                break;

            case R.id.nav_clubs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ClubsFragment.class, null).commit();
                break;

            case R.id.nav_accident_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AccidentHistoryFragment.class, null).commit();
                break;

            case R.id.nav_travel_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TravelHistoryFragment.class, null).commit();
                break;

            case R.id.nav_monitoring:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MonitoringFragment.class, null).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.class, null).commit();
                break;

            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setTitle("Logout?")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                            LocalDbUserService userService = new LocalDbUserService(this);
                            userService.deleteUser(user.getEmail());

                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                            // CODE
                        }))
                        .show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> finishAffinity())
                .setNegativeButton(android.R.string.no, ((dialogInterface, i) -> {
                    // CODE
                }))
                .show();
    }
}
