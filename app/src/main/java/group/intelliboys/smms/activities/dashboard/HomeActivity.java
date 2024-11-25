package group.intelliboys.smms.activities.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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
import group.intelliboys.smms.activities.signin.SignInActivity;
import group.intelliboys.smms.fragments.dashboard_menu.AccidentHistoryFragment;
import group.intelliboys.smms.fragments.dashboard_menu.ClubsFragment;
import group.intelliboys.smms.fragments.dashboard_menu.HomeFragment;
import group.intelliboys.smms.fragments.dashboard_menu.MonitoringFragment;
import group.intelliboys.smms.fragments.dashboard_menu.SettingsFragment;
import group.intelliboys.smms.fragments.dashboard_menu.TravelHistoryFragment;
import group.intelliboys.smms.fragments.profile_update.ProfileFragment;
import group.intelliboys.smms.models.data.view_models.HomeFragmentViewModel;
import group.intelliboys.smms.orm.data.User;
import group.intelliboys.smms.orm.repository.UserRepository;
import group.intelliboys.smms.security.SecurityContextHolder;
import group.intelliboys.smms.services.TravelHistoryService;
import group.intelliboys.smms.utils.Executor;
import group.intelliboys.smms.utils.converters.ImageConverter;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private View navHeader;
    private CircleImageView profilePic;
    private TextView navUsername;
    private TextView navUserEmail;
    private User authenticatedUser;

    @SuppressLint({"CommitTransaction", "ResourceType", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticatedUser = SecurityContextHolder.getInstance()
                .getAuthenticatedUser();

        setContentView(R.layout.activity_home);
        setTitle(null);

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

        setupProfileInfo();

        Executor.run(() -> {
            TravelHistoryService.getInstance().updateNullStartLocation();
            TravelHistoryService.getInstance().updateNullEndLocation();
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
                            logout();
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

    private void logout() {
        Executor.run(() -> {
            HomeFragmentViewModel viewModel = HomeFragmentViewModel.getInstance();
            viewModel.destroy();
            new UserRepository().deleteAllUsers();

            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    public void setupProfileInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(authenticatedUser.getLastName()).append(", ")
                .append(authenticatedUser.getFirstName());

        // USER INFO SETUP
        navUsername.setText(new String(builder));
        navUserEmail.setText(authenticatedUser.getEmail());

        if (authenticatedUser.getProfilePic() != null) {
            profilePic.setImageBitmap(ImageConverter.byteArrayToBitmap(authenticatedUser.getProfilePic()));

            profilePic.setOnClickListener((view) -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.class, null).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }

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
