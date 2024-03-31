package eu.flatworld.android.sdoviewer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.Date;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcopar on 26/02/17.
 */

public class Util {
    public static Response getUrl(OkHttpClient httpClient, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return httpClient.newCall(request).execute();
    }

    public static Date getLastModified(OkHttpClient httpClient, String url) throws IOException {

        Request request = new Request.Builder().url(url).head().build();

        return httpClient.newCall(request).execute().headers().getDate("Last-Modified");
    }

    public static synchronized boolean getHttpsSafeModeEnabled(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean defaultHttpMode = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            defaultHttpMode = true;
        }
        if (pref.getBoolean(GlobalConstants.PREFERENCES_FIRSTRUN, true)) {
            pref.edit().putBoolean(GlobalConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode)
                    .putBoolean(GlobalConstants.PREFERENCES_FIRSTRUN, false).apply();
        }
        return pref.getBoolean(GlobalConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode);
    }
}
