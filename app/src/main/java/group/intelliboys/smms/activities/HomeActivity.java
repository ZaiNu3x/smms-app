package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import group.intelliboys.smms.R;
import group.intelliboys.smms.fragments.AccidentHistoryFragment;
import group.intelliboys.smms.fragments.ClubsFragment;
import group.intelliboys.smms.fragments.HomeFragment;
import group.intelliboys.smms.fragments.MonitoringFragment;
import group.intelliboys.smms.fragments.SettingsFragment;
import group.intelliboys.smms.fragments.TravelHistoryFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @SuppressLint({"CommitTransaction", "ResourceType"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

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
            Log.i("", "Executed!");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.class, null).commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i("", String.valueOf(item.getItemId()));

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
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> finishAffinity())
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
