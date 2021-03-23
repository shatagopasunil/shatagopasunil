package com.vnrvjiet.tsrakshaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static SharedPreferences sharedPreferences;
    public static final String HasLogin = "login";
    public static final String MySharedPreferences = "LoginPrefs";
    private Toolbar mainToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFields();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationMenu.setCheckedItem(R.id.dashboard);
        }
    }

    private void initializeFields() {
        mainToolbar = findViewById(R.id.main_tool_bar);
        navigationMenu = findViewById(R.id.navigation_menu);
        navigationMenu.setNavigationItemSelectedListener(this);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        sharedPreferences = getSharedPreferences(MySharedPreferences, MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.sign_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to logout from the application?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(HasLogin, false);
                        editor.commit();
                        sendUserToLoginActivity();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getBoolean(HasLogin, false) == false)
            sendUserToLoginActivity();
    }

    private void sendUserToLoginActivity() {
        Intent homeIntent = new Intent(MainActivity.this, LoginActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.update_cases:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpdateCasesFragment()).commit();
                break;
            case R.id.isolation_centers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IsolationCentersFragment()).commit();
                break;
            case R.id.faqs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FaqsFragment()).commit();
                break;
            case R.id.government_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GovernmentOrders()).commit();
                break;
            case R.id.testing_centers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TestingCentersFragment()).commit();
                break;
            case R.id.video_updates:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VideoUpdatesFragment()).commit();
                break;
            case R.id.technical_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TechnicalSupport()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
