package com.vnrvjiet.tsraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.vnrvjiet.tsraksha.Constants.EMPTY;
import static com.vnrvjiet.tsraksha.Constants.EXTRA;
import static com.vnrvjiet.tsraksha.Constants.FIRST;
import static com.vnrvjiet.tsraksha.Constants.LAST;
import static com.vnrvjiet.tsraksha.Constants.NAME;
import static com.vnrvjiet.tsraksha.Constants.PHONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    public static NavigationView navigationMenu;
    public static SharedPreferences sharedPreferences;
    public static final String HasProfile = "profile";
    public static final String MySharedPreferences = "MyPrefs";
    private DashboardFragment dashboardFragment;
    private FirebaseAuth myAuth;
    private LocationManager locationManager;
    public static String latitude, longitude;
    private TextView userName, userPhone;
    private View headerView;
    private ContributeFragment contributeFragment;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(MainActivity.this);
        setContentView(R.layout.activity_main);
        initializeFields();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            navigationMenu.setCheckedItem(R.id.home);
        }
    }

    private void initializeFields() {
        homeFragment = new HomeFragment();
        mainToolbar = findViewById(R.id.main_tool_bar);
        navigationMenu = findViewById(R.id.navigation_menu);
        dashboardFragment = new DashboardFragment();
        contributeFragment = new ContributeFragment();
        navigationMenu.setNavigationItemSelectedListener(this);
        headerView = navigationMenu.getHeaderView(0);
        sharedPreferences = getSharedPreferences(MySharedPreferences, MODE_PRIVATE);
        setSupportActionBar(mainToolbar);
        userName = headerView.findViewById(R.id.menu_user_name);
        userPhone = headerView.findViewById(R.id.menu_phone_number);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myAuth = FirebaseAuth.getInstance();
        userName.setText(sharedPreferences.getString(NAME, EMPTY));
        userPhone.setText(sharedPreferences.getString(PHONE, EMPTY));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!sharedPreferences.getBoolean(HasProfile, false)) {
            sendUserToProfileActivity(FIRST);
        }
    }

    private void sendUserToProfileActivity(String s) {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        if (s.equals(FIRST))
            profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        profileIntent.putExtra(EXTRA, s);
        startActivity(profileIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.self_assessment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelfAssessmentFragment()).commit();
                break;
            case R.id.edit_profile:
                sendUserToProfileActivity(LAST);
                break;
            case R.id.isolation_centers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IsolationCentersFragment()).commit();
                break;
            case R.id.contribute:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contributeFragment).commit();
                break;
            case R.id.testing_centers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TestingCentersFragment()).commit();
                break;
            case R.id.faqs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FaqsFragment()).commit();
                break;
            case R.id.logout:
                logoutFromApplication();
                break;
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                break;
            case R.id.home_quarantine:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeQuarantineOptions()).commit();
                break;
            case R.id.video_updates:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoUpdatesFragment()).commit();
                break;
            case R.id.government_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GovernmentOrdersFragment()).commit();
                break;
            case R.id.language:
                SelectLanguage selectLanguage = new SelectLanguage(MainActivity.this);
                selectLanguage.changeLanguage();
                break;
            case R.id.technical_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TechnicalSupportFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutFromApplication() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.logout));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(HasProfile, false);
                editor.apply();
                sendUserToLoginActivity();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }
}
