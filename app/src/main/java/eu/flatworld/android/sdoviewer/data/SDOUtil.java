package eu.flatworld.android.sdoviewer.data;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.gui.browse.BrowseDataListItem;
import okhttp3.OkHttpClient;

/**
 * Created by marcopar on 22/02/15.
 */
public class SDOUtil {

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
        for (SDO t : SDO.values()) {
            l.add(new BrowseDataListItem(String.format("%s (%s)", t.toString(), t.getShortCode()), t.name()));
        }
        return l;
    }

    public static List<BrowseDataListItem> loadImages(int year, int month, int day, ArrayList<String> links, SDO type, int resolution) throws IOException {
        List<BrowseDataListItem> l = new ArrayList<>();
        String regex = String.format("%d%02d%02d_\\d\\d\\d\\d\\d\\d_%d_%s.jpg", year, month, day, resolution, type.getShortCode());
        Pattern p = Pattern.compile(regex);
        Log.d(GlobalConstants.LOGTAG, "Parse links");
        for (String link : links) {
            String url = link.substring(link.lastIndexOf('/') + 1);
            if (p.matcher(url).matches()) {
                String text = String.format("%s:%s:%s", url.substring(9, 11), url.substring(11, 13), url.substring(13, 15));
                String fullUrl = String.format("%s/%d/%02d/%02d/%s", SDO.URL_BROWSE, year, month, day, url);
                l.add(new BrowseDataListItem(text, fullUrl));
            }
        }
        Log.d(GlobalConstants.LOGTAG, "Parse links complete");
        return l;
    }

    public static ArrayList<String> loadLinks(OkHttpClient httpClient, int year, int month, int day) throws IOException {
        String baseUrl = String.format("%s%d/%02d/%02d/", SDO.URL_BROWSE, year, month, day);
        Log.d(GlobalConstants.LOGTAG, "Load links");
        ArrayList<String> al = new ArrayList<>();
        try {
            String sb = Util.getUrl(httpClient, baseUrl).body().string();
            Document doc = Jsoup.parse(sb, baseUrl);
            Elements elements = doc.select("a[href]");
            for (Element e : elements) {
                String s = e.attr("abs:href");
                al.add(s);
            }
            Log.d(GlobalConstants.LOGTAG, "Load links completed " + baseUrl + " " + al.size());
        } catch (IOException ex) {
            Log.d(GlobalConstants.LOGTAG, "Load links completed with errors", ex);
            throw ex;
        }
        return al;
    }


}
