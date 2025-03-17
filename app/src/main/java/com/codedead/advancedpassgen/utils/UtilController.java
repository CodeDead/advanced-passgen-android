package com.codedead.advancedpassgen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

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

    /**
     * Apply or clear the FLAG_SECURE flag to/from the Activity this Fragment is attached to.
     *
     * @param fragmentActivity The FragmentActivity this Fragment is attached to
     * @param flagSecure       The flag to apply or clear
     */
    public static void applyFlagSecure(final FragmentActivity fragmentActivity, final boolean flagSecure) {
        final Window window = fragmentActivity.getWindow();
        final WindowManager wm = fragmentActivity.getWindowManager();

        // is change needed?
        int flags = window.getAttributes().flags;
        if (flagSecure && (flags & WindowManager.LayoutParams.FLAG_SECURE) != 0) {
            // already set, change is not needed.
            return;
        } else if (!flagSecure && (flags & WindowManager.LayoutParams.FLAG_SECURE) == 0) {
            // already cleared, change is not needed.
            return;
        }

        // apply (or clear) the FLAG_SECURE flag to/from Activity this Fragment is attached to.
        if (flagSecure) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Re-apply (re-draw) Window's DecorView so the change to the Window flags will be in place immediately.
        if (window.getDecorView().isAttachedToWindow()) {
            wm.removeViewImmediate(window.getDecorView());
            wm.addView(window.getDecorView(), window.getAttributes());
        }
    }
}
