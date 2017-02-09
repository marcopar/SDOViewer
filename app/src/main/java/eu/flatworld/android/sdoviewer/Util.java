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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcopar on 22/02/15.
 */
public class Util {
    public static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES).connectTimeout(10, TimeUnit.SECONDS)
            .build();


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

    public static String getLatestURL(SDOImageType si, int size, boolean pfss) {
        String pfssString = "";
        if (pfss) {
            pfssString = "pfss";
        }
        switch (si) {
            case AIA_193:
                return String.format(BASE_URL_LATEST + "latest_%d_0193%s.jpg", size, pfssString);
            case AIA_304:
                return String.format(BASE_URL_LATEST + "latest_%d_0304%s.jpg", size, pfssString);
            case AIA_171:
                return String.format(BASE_URL_LATEST + "latest_%d_0171%s.jpg", size, pfssString);
            case AIA_211:
                return String.format(BASE_URL_LATEST + "latest_%d_0211%s.jpg", size, pfssString);
            case AIA_131:
                return String.format(BASE_URL_LATEST + "latest_%d_0131%s.jpg", size, pfssString);
            case AIA_335:
                return String.format(BASE_URL_LATEST + "latest_%d_0335%s.jpg", size, pfssString);
            case AIA_094:
                return String.format(BASE_URL_LATEST + "latest_%d_0094%s.jpg", size, pfssString);
            case AIA_1600:
                return String.format(BASE_URL_LATEST + "latest_%d_1600%s.jpg", size, pfssString);
            case AIA_1700:
                return String.format(BASE_URL_LATEST + "latest_%d_1700%s.jpg", size, pfssString);
            case AIA_211_193_171:
                return String.format(BASE_URL_LATEST + "f_211_193_171%s_%d.jpg", pfssString, size);
            case AIA_304_211_171:
                return String.format(BASE_URL_LATEST + "f_304_211_171%s_%d.jpg", pfssString, size);
            case AIA_094_335_193:
                return String.format(BASE_URL_LATEST + "f_094_335_193%s_%d.jpg", pfssString, size);
            case AIA_171_HMIB:
                return String.format(BASE_URL_LATEST + "f_HMImag_171%s_%d.jpg", pfssString, size);
            case HMI_Magnetogram:
                return String.format(BASE_URL_LATEST + "latest_%d_HMIB%s.jpg", size, pfssString);
            case HMI_Colorized_Magnetogram:
                if (pfss) {
                    return null;
                }
                return String.format(BASE_URL_LATEST + "latest_%d_HMIBC.jpg", size);
            case HMI_Intensitygram_Colored:
                if (pfss) {
                    return null;
                }
                return String.format(BASE_URL_LATEST + "latest_%d_HMIIC.jpg", size);
            case HMI_Intensitygram_Flattened:
                if (pfss) {
                    return null;
                }
                return String.format(BASE_URL_LATEST + "latest_%d_HMIIF.jpg", size);
            case HMI_Intensitygram:
                if (pfss) {
                    return null;
                }
                return String.format(BASE_URL_LATEST + "latest_%d_HMII.jpg", size);
            case HMI_Dopplergram:
                if (pfss) {
                    return null;
                }
                return String.format(BASE_URL_LATEST + "latest_%d_HMID.jpg", size);
        }
        return null;
    }

    public static String getDescription(SDOImageType si) {
        switch (si) {
            case AIA_193:
                return "<p>This channel highlights the outer atmosphere of the Sun - called the corona - as well as hot flare plasma. Hot active regions, solar flares, and coronal mass ejections will appear bright here. The dark areas - called coronal holes - are places where very little radiation is emitted, yet are the main source of solar wind particles.</p><p><strong>Where:</strong> Corona and hot flare plasma<br><strong>Wavelength:</strong>  193 angstroms (0.0000000193 m) = Extreme Ultraviolet<br><strong>Primary ions seen:</strong> 11 times ionized iron (Fe XII)<br><strong>Characteristic temperature:</strong> 1.25 million K (2.25 million F)</p>";
            case AIA_304:
                return "<p>This channel is especially good at showing areas where cooler dense plumes of plasma (filaments and prominences) are located above the visible surface of the Sun. Many of these features either can't be seen or appear as dark lines in the other channels. The bright areas show places where the plasma has a high density.</p><p><strong>Where:</strong> Upper chromosphere and lower transition region<br><strong>Wavelength:</strong> 304 angstroms (0.0000000304 m) = Extreme Ultraviolet<br><strong>Primary ions seen:</strong> singly ionized helium (He II)<br><strong>Characteristic temperature:</strong> 50,000 K (90,000 F)</p>";
            case AIA_171:
                return "<p>This channel is especially good at showing coronal loops - the arcs extending off of the Sun where plasma moves along magnetic field lines. The brightest spots seen here are locations where the magnetic field near the surface is exceptionally strong.</p><p><strong>Where:</strong> Quiet corona and upper transition region<br><strong>Wavelength:</strong> 171 angstroms (0.0000000171 m) = Extreme Ultraviolet<br /><strong>Primary ions seen:</strong>  8 times ionized iron (Fe IX)<br><strong>Characteristic temperature:</strong> 1 million K (1.8 million F)</p>";
            case AIA_211:
                return "<p>This channel (as well as AIA 335) highlights the active region of the outer atmosphere of the Sun - the corona. Active regions, solar flares, and coronal mass ejections will appear bright here. The dark areas - called coronal holes - are places where very little radiation is emitted, yet are the main source of solar wind particles.</p><p><strong>Where:</strong> Active regions of the corona<br><strong>Wavelength:</strong>  211 angstroms (0.0000000211 m) = Extreme Ultraviolet<br><strong>Primary ions seen:</strong> 13 times ionized iron (Fe XIV)<br><strong>Characteristic temperature:</strong> 2 million K (3.6 million F)</p>";
            case AIA_131:
                return "<p>This channel (as well as AIA 094) is designed to study solar flares. It measures extremely hot temperatures around 10 million K (18 million F), as well as cool plasmas around 400,000 K (720,000 F). It can take images every 2 seconds (instead of 10) in a reduced field of view in order to look at flares in more detail.</p><p><strong>Where:</strong> Flaring regions of the corona<br /><strong>Wavelength:</strong>  131 angstroms (0.0000000131 m) = Extreme Ultraviolet<br><strong>Primary ions seen:</strong>  20 and 7 times ionized iron (Fe VIII, Fe XXI)<br><strong>Characteristic temperatures:</strong> 10 million K (18 million F)</p>";
            case AIA_335:
                return "<p>This channel (as well as AIA 211) highlights the active region of the outer atmosphere of the Sun - the corona. Active regions, solar flares, and coronal mass ejections will appear bright here. The dark areas - or coronal holes - are places where very little radiation is emitted, yet are the main source of solar wind particles.</p><p><strong>Where:</strong> Active regions of the corona<br><strong>Wavelength:</strong>  335 angstroms (0.0000000335 m) = Extreme Ultraviolet<br><strong>Primary ions seen:</strong> 15 times ionized iron (Fe XVI)<br><strong>Characteristic temperature:</strong> 2.8 million K (5 million F)</p>";
            case AIA_094:
                return "<p>This channel (as well as AIA 131) is designed to study solar flares. It measures extremely hot temperatures around 6 million Kelvin (10.8 million F). It can take images every 2 seconds (instead of 10) in a reduced field of view in order to look at flares in more detail.</p><p><strong>Where:</strong> Flaring regions of the corona<br /><strong>Wavelength:</strong>  94 angstroms (0.0000000094 m) = Extreme Ultraviolet/soft X-rays<br /><strong>Primary ions seen:</strong>  17 times ionized iron (Fe XVIII)<br><strong>Characteristic temperature:</strong> 6 million K (10.8 million F)</p>";
            case AIA_1600:
                return "<p>This channel (as well as AIA 1700) often shows a web-like pattern of bright areas that highlight places where bundles of magnetic fields lines are concentrated. However, small areas with a lot of field lines will appear black, usually near sunspots and active regions.</p><p><strong>Where:</strong> Transition region and upper photosphere<br><strong>Wavelength:</strong>  1600 angstroms (0.00000016 m) = Far Ultraviolet<br><strong>Primary ions seen:</strong>  thrice ionized carbon (C IV) and Continuum<br><strong>Characteristic temperatures:</strong> 6,000 K (11,000 F), and 100,000 K (180,000 F)</p>";
            case AIA_1700:
                return "<p>This channel (as well as AIA 1600) often shows a web-like pattern of bright areas that highlight places where bundles of magnetic fields lines are concentrated. However, small areas with a lot of field lines will appear black, usually near sunspots and active regions.</p><p><strong>Where:</strong> Temperature minimum and photosphere<br><strong>Wavelength:</strong> 1700 angstroms (0.00000017 m) = Far Ultraviolet<br><strong>Primary ions seen:</strong> Continuum<br /><strong>Characteristic temperature:</strong> 6,000 K (11,000 F)</p>";
            case AIA_211_193_171:
                return "<p>This image combines three images with different, but very similar, temperatures. The colors are assigned differently than in the single images. Here AIA 211 is red, AIA 193 is green, and AIA 171 is blue.  Each highlights a different part of the corona.</p>";
            case AIA_304_211_171:
                return "<p>This image combines three images with quite different temperatures. The colors are assigned differently than in the single images. Here AIA 304 is red (showing the chromosphere), AIA 211 is green (corona), and AIA 171 is dark blue (corona).</p>";
            case AIA_094_335_193:
                return "<p>This image combines three images with different temperatures. Each image is assigned a color, and they are not the same used in the single images. Here AIA 094 is red, AIA 335 is green, and AIA 193 is blue.  Each highlights a different part of the corona.</p>";
            case AIA_171_HMIB:
                return "<p>No description is available for this image</p>";
            case HMI_Magnetogram:
                return "<p>This image comes from HMI, another instrument on SDO. It shows the magnetic field directions near the surface of the Sun. White and black areas indicate opposite magnetic polarities, with white showing north (outward) polarity and black showing south (inward) polarity.</p><p><strong>Where:</strong> Photosphere<br><strong>Wavelength:</strong> 6173 angstroms (0.0000006173 m) = Visible (orange)<br><strong>Primary ions seen:</strong> Neutral iron (Fe I)<br><strong>Characteristic temperature:</strong> 6,000 K (11,000 F)</p>";
            case HMI_Colorized_Magnetogram:
                return "<p>This image comes from HMI, another instrument on SDO. It shows the magnetic field directions near the surface of the Sun. White and black areas indicate opposite magnetic polarities, with white showing north (outward) polarity and black showing south (inward) polarity.</p><p><strong>Where:</strong> Photosphere<br><strong>Wavelength:</strong> 6173 angstroms (0.0000006173 m) = Visible (orange)<br><strong>Primary ions seen:</strong> Neutral iron (Fe I)<br><strong>Characteristic temperature:</strong> 6,000 K (11,000 F)</p>";
            case HMI_Intensitygram_Colored:
                return "<p>No description is available for this image</p>";
            case HMI_Intensitygram_Flattened:
                return "<p>No description is available for this image</p>";
            case HMI_Intensitygram:
                return "<p>No description is available for this image</p>";
            case HMI_Dopplergram:
                return "<p>No description is available for this image</p>";
        }
        return null;
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

    public static ArrayList<String> loadLinks2(int year, int month, int day) throws IOException {
        String baseUrl = String.format("%s/%d/%02d/%02d/", BASE_URL_BROWSE, year, month, day);
        Log.d(SDOViewerConstants.LOGTAG, "Load links");
        Document doc = Jsoup.connect(baseUrl).maxBodySize(0).get();
        Elements elements = doc.select("a[href]");
        ArrayList<String> al = new ArrayList<>();
        for (Element e : elements) {
            String s = e.attr("abs:href");
            al.add(s);
        }
        Log.d(SDOViewerConstants.LOGTAG, "Load links completed");
        return al;
    }

    public static ArrayList<String> loadLinks(int year, int month, int day) throws IOException {
        String baseUrl = String.format("%s/%d/%02d/%02d/", BASE_URL_BROWSE, year, month, day);
        Log.d(SDOViewerConstants.LOGTAG, "Load links");
        ArrayList<String> al = new ArrayList<>();
        try {
            String sb = getUrl(baseUrl).body().string();
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

    public static Response getUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return httpClient.newCall(request).execute();
    }

    public static Date getLastModified(String url) throws IOException {

        Request request = new Request.Builder().url(url).head().build();

        return httpClient.newCall(request).execute().headers().getDate("Last-Modified");
    }


    public static synchronized boolean getHttpModeEnabled(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean defaultHttpMode = false;
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            defaultHttpMode = true;
        }
        if (pref.getBoolean(SDOViewerConstants.PREFERENCES_FIRSTRUN, true)) {
            pref.edit().putBoolean(SDOViewerConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode)
                    .putBoolean(SDOViewerConstants.PREFERENCES_FIRSTRUN, false).commit();
        }
        return pref.getBoolean(SDOViewerConstants.PREFERENCES_HTTPCOMPATIBILITYMODE, defaultHttpMode);
    }

}
