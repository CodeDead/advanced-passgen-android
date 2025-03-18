package com.codedead.advancedpassgen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.codedead.advancedpassgen.domain.LocaleHelper;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        LocaleHelper.setLocale(this, sharedPreferences.getString("appLanguage", "en"));
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting_layout), (v, windowInsets) -> {
            final Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
        if (key == null)
            return;

        switch (key) {
            case "appLanguage" -> {
                LocaleHelper.setLocale(getApplicationContext(), prefs.getString("appLanguage", "en"));
                recreate();
            }
            case "minimumLength" -> {
                try {
                    int min = Integer.parseInt(prefs.getString("minimumLength", "8"));
                    int max = Integer.parseInt(prefs.getString("maximumLength", "30"));

                    if (min > max) {
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("minimumLength", String.valueOf(max));
                        editor.apply();
                    }
                } catch (final NumberFormatException ex) {
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("minimumLength", "8");
                    editor.apply();
                }
            }
            case "maximumLength" -> {
                try {
                    int min = Integer.parseInt(prefs.getString("minimumLength", "8"));
                    int max = Integer.parseInt(prefs.getString("maximumLength", "30"));

                    if (max < min) {
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("maximumLength", String.valueOf(min));
                        editor.apply();
                    }
                } catch (final NumberFormatException ex) {
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("maximumLength", "30");
                    editor.apply();
                }
            }
            case "passwordAmount" -> {
                try {
                    int amount = Integer.parseInt(prefs.getString("passwordAmount", "100"));

                    if (amount < 1) {
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("passwordAmount", "1");
                        editor.apply();
                    }
                } catch (final NumberFormatException ex) {
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("passwordAmount", "100");
                    editor.apply();
                }
            }
            case "poolSize" -> {
                try {
                    int amount = Integer.parseInt(prefs.getString("poolSize", "1"));

                    if (amount < 1) {
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("poolSize", "1");
                        editor.apply();
                    }
                } catch (final NumberFormatException ex) {
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("poolSize", "1");
                    editor.apply();
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final List<String> keys = Arrays.asList("minimumLength", "maximumLength", "passwordAmount", "poolSize");
            keys.forEach(e -> {
                final EditTextPreference editTextPreference = getPreferenceManager().findPreference(e);
                if (editTextPreference != null) {
                    editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
                }
            });
        }
    }
}
