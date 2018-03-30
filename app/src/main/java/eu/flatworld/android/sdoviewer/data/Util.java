package eu.flatworld.android.sdoviewer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

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
    public static void firebaseLog(Context ctx, String text, Throwable ex) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String s = String.format("ID: %s, MSG: %s", android_id, text);
        Log.e(GlobalConstants.LOGTAG, s, ex);
        Crashlytics.log(s);
        Crashlytics.logException(ex);
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }

    public static boolean isRoaming(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info == null) {
            return false;
        }
        return info.isRoaming();
    }

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
                    .putBoolean(GlobalConstants.PREFERENCES_FIRSTRUN, false).commit();
        }
        return pref.getBoolean(GlobalConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode);
    }
}
