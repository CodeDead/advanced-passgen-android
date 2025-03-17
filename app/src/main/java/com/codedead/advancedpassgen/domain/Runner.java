package com.codedead.advancedpassgen.domain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class Runner extends Application {

    @Override
    protected void attachBaseContext(final Context base) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(base);
        super.attachBaseContext(LocaleHelper.onAttach(base, sharedPref.getString("appLanguage", "en")));
    }
}
