package eu.flatworld.android.sdoviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import eu.flatworld.android.sdoviewer.gui.browse.BrowseDataListItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcopar on 22/02/15.
 */
public class Util {
    public static final String SDO_URL = "https://sdo.gsfc.nasa.gov/";
    public static final String BASE_URL_LATEST = SDO_URL + "assets/img/latest/";
    public static final String BASE_URL_BROWSE = SDO_URL + "assets/img/browse/";

    public static void firebaseLog(Context ctx, String text, Throwable ex) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String s = String.format("ID: %s, MSG: %s", android_id, text);
        Log.e(SDOViewerConstants.LOGTAG, s, ex);
        FirebaseCrash.log(s);
        FirebaseCrash.report(ex);
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info.isConnected();
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return info.isConnected();
    }

    public static boolean isRoaming(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return info.isRoaming();
    }

    public static List<BrowseDataListItem> loadYears() {
        int maxYear = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
        List<BrowseDataListItem> l = new ArrayList<>();
        for (int i = 2010; i <= maxYear; i++) {
            String s = Integer.toString(i);
            l.add(new BrowseDataListItem(s, s));
        }
        return l;
    }

    public static List<BrowseDataListItem> loadMonths(int year) {
        int maxMonth = 12;
        if (year == GregorianCalendar.getInstance().get(GregorianCalendar.YEAR)) {
            maxMonth = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1;
        }
        List<BrowseDataListItem> l = new ArrayList<>();
        for (int i = 1; i <= maxMonth; i++) {
            String s = Integer.toString(i);
            l.add(new BrowseDataListItem(s, s));
        }
        return l;
    }

    public static List<BrowseDataListItem> loadDays(int year, int month) {
        Calendar gc = GregorianCalendar.getInstance();
        gc.set(GregorianCalendar.YEAR, year);
        gc.set(GregorianCalendar.MONTH, month - 1);
        int maxDays = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        if (year == GregorianCalendar.getInstance().get(GregorianCalendar.YEAR)) {
            if (month == (GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1)) {
                maxDays = GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH);
            }
        }
        List<BrowseDataListItem> l = new ArrayList<>();
        for (int i = 1; i <= maxDays; i++) {
            String s = Integer.toString(i);
            l.add(new BrowseDataListItem(s, s));
        }
        return l;
    }

    public static List<BrowseDataListItem> loadImageTypes() {
        List<BrowseDataListItem> l = new ArrayList<>();
        for (SDOImageType t : SDOImageType.values()) {
            l.add(new BrowseDataListItem(String.format("%s (%s)", t.toString(), t.getShortCode()), t.name()));
        }
        return l;
    }

    public static List<BrowseDataListItem> loadImages(int year, int month, int day, ArrayList<String> links, SDOImageType type, int resolution) throws IOException {
        List<BrowseDataListItem> l = new ArrayList<>();
        String regex = String.format("%d%02d%02d_\\d\\d\\d\\d\\d\\d_%d_%s.jpg", year, month, day, resolution, type.getShortCode());
        Pattern p = Pattern.compile(regex);
        Log.d(SDOViewerConstants.LOGTAG, "Parse links");
        for (String link : links) {
            String url = link.substring(link.lastIndexOf('/') + 1);
            if (p.matcher(url).matches()) {
                String text = String.format("%s:%s:%s", url.substring(9, 11), url.substring(11, 13), url.substring(13, 15));
                String fullUrl = String.format("%s/%d/%02d/%02d/%s", BASE_URL_BROWSE, year, month, day, url);
                l.add(new BrowseDataListItem(text, fullUrl));
            }
        }
        Log.d(SDOViewerConstants.LOGTAG, "Parse links complete");
        return l;
    }

    public static ArrayList<String> loadLinks(OkHttpClient httpClient, int year, int month, int day) throws IOException {
        String baseUrl = String.format("%s/%d/%02d/%02d/", BASE_URL_BROWSE, year, month, day);
        Log.d(SDOViewerConstants.LOGTAG, "Load links");
        ArrayList<String> al = new ArrayList<>();
        try {
            String sb = getUrl(httpClient, baseUrl).body().string();
            Document doc = Jsoup.parse(sb, baseUrl);
            Elements elements = doc.select("a[href]");
            for (Element e : elements) {
                String s = e.attr("abs:href");
                al.add(s);
            }
            Log.d(SDOViewerConstants.LOGTAG, "Load links completed " + baseUrl + " " + al.size());
        } catch (IOException ex) {
            Log.d(SDOViewerConstants.LOGTAG, "Load links completed with errors", ex);
            throw ex;
        }
        return al;
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


    public static synchronized boolean getHttpModeEnabled(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean defaultHttpMode = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            defaultHttpMode = true;
        }
        if (pref.getBoolean(SDOViewerConstants.PREFERENCES_FIRSTRUN, true)) {
            pref.edit().putBoolean(SDOViewerConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode)
                    .putBoolean(SDOViewerConstants.PREFERENCES_FIRSTRUN, false).commit();
        }
        return pref.getBoolean(SDOViewerConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode);
    }

}
