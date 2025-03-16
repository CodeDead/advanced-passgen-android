package com.codedead.advancedpassgen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.codedead.advancedpassgen.ui.info.InfoFragment;

public class UtilController {



    /**
     * Open a website in the default browser
     *
     * @param context The context that can be used to start the activity
     * @param url     The URL that should be opened
     */
    public static void openWebsite(final Context context, final String url) {
        try {
            final Uri uri = Uri.parse(url);
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            context.startActivity(intent);
        } catch (final Exception ex) {
            Log.e(UtilController.class.getSimpleName(), "An error occurred while trying to open the website: " + ex.getMessage());
        }
    }
}
