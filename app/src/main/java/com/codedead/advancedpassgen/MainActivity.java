package com.codedead.advancedpassgen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.codedead.advancedpassgen.domain.LocaleHelper;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.codedead.advancedpassgen.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private int flipperPosition = 0;
    private SharedPreferences sharedPreferences;
    private String lastLanguage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lastLanguage = sharedPreferences.getString("appLanguage", "en");
        LocaleHelper.setLocale(this, lastLanguage);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        final DrawerLayout drawer = binding.drawerLayout;
        final NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_info, R.id.nav_support)
                .setOpenableLayout(drawer)
                .build();

        final NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_content_main);

        assert navHostFragment != null;
        final NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Switch the selected item in the drawer
        setSelectedDrawerItem();

        navigationView.setNavigationItemSelectedListener(item -> {
            final int id = item.getItemId();

            if (id == R.id.nav_home) {
                flipperPosition = 0;
                navController.navigate(R.id.nav_home);
            } else if (id == R.id.nav_info) {
                flipperPosition = 1;
                navController.navigate(R.id.nav_info);
            } else if (id == R.id.nav_support) {
                flipperPosition = 2;
                navController.navigate(R.id.nav_support);
            }

            drawer.closeDrawers();
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_home) {
                flipperPosition = 0;
                setSelectedDrawerItem();
            } else if (destination.getId() == R.id.nav_info) {
                flipperPosition = 1;
                setSelectedDrawerItem();
            } else if (destination.getId() == R.id.nav_support) {
                flipperPosition = 2;
                setSelectedDrawerItem();
            }
        });
    }

    /**
     * Set the selected item in the drawer
     */
    private void setSelectedDrawerItem() {
        final SubMenu m0 = binding.navView.getMenu().getItem(0).getSubMenu();
        final SubMenu m1 = binding.navView.getMenu().getItem(1).getSubMenu();

        assert m0 != null;
        assert m1 != null;

        switch (flipperPosition) {
            case 0:
                binding.navView.setCheckedItem(m0.getItem(0).getItemId());
                break;
            case 1:
                binding.navView.setCheckedItem(m1.getItem(0).getItemId());
                break;
            case 2:
                binding.navView.setCheckedItem(m1.getItem(1).getItemId());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            final Intent i = new Intent(this, SettingsActivity.class);
            this.startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void onResume() {
        final String selectedLanguage = sharedPreferences.getString("appLanguage", "en");

        if (!lastLanguage.equals(selectedLanguage)) {
            LocaleHelper.setLocale(getApplicationContext(), selectedLanguage);

            final Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        super.onResume();
    }
}
