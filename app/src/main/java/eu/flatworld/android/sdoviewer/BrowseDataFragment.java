package eu.flatworld.android.sdoviewer;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataFragment extends ListFragment {
    int year = -1;
    int month = -1;
    int day = -1;
    SDOImageType type = null;
    Elements links = null;

    DownloadImageListTask task = null;

    private int resolution;

    public BrowseDataFragment() {
        setHasOptionsMenu(true);
    }

    public void setLinks(Elements links) {
        this.links = links;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setType(SDOImageType type) {
        this.type = type;
    }

    void dumpBackstack(String s) {
        Log.d(Main.LOGTAG, s + "====================");
        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
            Log.d(Main.LOGTAG, getActivity().getSupportFragmentManager().getBackStackEntryAt(i).toString());
        }
        Log.d(Main.LOGTAG, "====================");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            day = savedInstanceState.getInt("day", -1);
            month = savedInstanceState.getInt("month", -1);
            year = savedInstanceState.getInt("year", -1);
            type = (SDOImageType) savedInstanceState.getSerializable("type");
            links = (Elements) savedInstanceState.getSerializable("links");
            Log.d(Main.LOGTAG, String.format("fragment created instance %d,%d,%d,%s,%s", year, month, day, type, links == null ? -1 : links.size()));
        } else {
            Log.d(Main.LOGTAG, String.format("fragment created no instance"));
        }

        dumpBackstack("fragment created");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setFastScrollEnabled(true);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        resolution = Integer.parseInt(pref.getString("resolution", "2048"));

        Log.d(Main.LOGTAG, "Start AsyncTask");
        task = new DownloadImageListTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dumpBackstack("view created");
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.browse_data);
        if (type != null) {
            bar.setSubtitle(String.format("%d/%d/%d/%s : %s", year, month, day, type.getShortCode(), "Loading hours..."));
        } else if (day != -1) {
            bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, "Loading images..."));
        } else if (month != -1) {
            bar.setSubtitle(String.format("%d/%d: %s", year, month, "Loading days..."));
        } else if (year != -1) {
            bar.setSubtitle(String.format("%d: %s", year, "Loading months..."));
        } else {
            bar.setSubtitle("Loading years...");
        }
        Log.d(Main.LOGTAG, String.format("resume %d,%d,%d,%s,%s", year, month, day, type, links == null ? -1 : links.size()));

        dumpBackstack("resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) {
            Log.d(Main.LOGTAG, "Cancel AsyncTask");
            task.cancel(true);
        }
        Log.d(Main.LOGTAG, String.format("pause %d,%d,%d,%s,%s", year, month, day, type, links == null ? -1 : links.size()));


        dumpBackstack("pause");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("day", day);
        outState.putInt("month", month);
        outState.putInt("year", year);
        outState.putSerializable("type", type);
        outState.putSerializable("links", links);
        Log.d(Main.LOGTAG, String.format("save instance %d,%d,%d,%s,%s", year, month, day, type, links == null ? -1 : links.size()));

        dumpBackstack("save instance");
    }


    List<BrowseDataListItem> loadYears() {
        int maxYear = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
        List<BrowseDataListItem> l = new ArrayList<>();
        for (int i = 2010; i <= maxYear; i++) {
            String s = Integer.toString(i);
            l.add(new BrowseDataListItem(s, s));
        }
        return l;
    }

    List<BrowseDataListItem> loadMonths(int year) {
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

    List<BrowseDataListItem> loadDays(int year, int month) {
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

    List<BrowseDataListItem> loadImageTypes() {
        List<BrowseDataListItem> l = new ArrayList<>();
        for (SDOImageType t : SDOImageType.values()) {
            l.add(new BrowseDataListItem(String.format("%s (%s)", t.toString(), t.getShortCode()), t.name()));
        }
        return l;
    }

    List<BrowseDataListItem> loadImages(SDOImageType type, int resolution) throws IOException {
        List<BrowseDataListItem> l = new ArrayList<>();
        String regex = String.format("%d%02d%02d_\\d\\d\\d\\d\\d\\d_%d_%s.jpg", year, month, day, resolution, type.getShortCode());
        Pattern p = Pattern.compile(regex);
        Log.d(Main.LOGTAG, "Parse links");
        for (Element link : links) {
            String s = link.attr("abs:href");
            String url = s.substring(s.lastIndexOf('/') + 1);
            if (p.matcher(url).matches()) {
                String text = String.format("%s:%s:%s", url.substring(9, 11), url.substring(11, 13), url.substring(13, 15));
                l.add(new BrowseDataListItem(text, url));
            }
        }
        Log.d(Main.LOGTAG, "Parse links complete");
        return l;
    }

    Elements loadLinks(int year, int month, int day) throws IOException {
        String baseUrl = String.format("http://sdo.gsfc.nasa.gov/assets/img/browse/%d/%02d/%02d/", year, month, day);
        Log.d(Main.LOGTAG, "Load links");
        Document doc = Jsoup.connect(baseUrl).maxBodySize(0).get();
        return doc.select("a[href]");
    }

    BrowseDataListAdapter createAdapter() throws IOException {
        if (type != null) {
            Log.d(Main.LOGTAG, "Load images");
            return new BrowseDataListAdapter(getActivity(), loadImages(type, resolution));
        } else if (day != -1) {
            Log.d(Main.LOGTAG, "Load image types");
            if (links == null) {
                links = loadLinks(year, month, day);
            }
            return new BrowseDataListAdapter(getActivity(), loadImageTypes());
        } else if (month != -1) {
            Log.d(Main.LOGTAG, "Load days");
            return new BrowseDataListAdapter(getActivity(), loadDays(year, month));
        } else if (year != -1) {
            Log.d(Main.LOGTAG, "Load months");
            return new BrowseDataListAdapter(getActivity(), loadMonths(year));
        } else {
            Log.d(Main.LOGTAG, "Load years");
            return new BrowseDataListAdapter(getActivity(), loadYears());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BrowseDataListItem bdli = (BrowseDataListItem) getListAdapter().getItem(position);
        if (type != null) {

        } else if (day != -1) {
            SDOImageType type = SDOImageType.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            bdf.setDay(day);
            bdf.setType(type);
            bdf.setLinks(links);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("ora").commit();
        } else if (month != -1) {
            int day = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            bdf.setDay(day);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("tipo").commit();
        } else if (year != -1) {
            int month = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("giorno").commit();
        } else {
            int year = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("mese").commit();
        }


        dumpBackstack("click");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        //inflater.inflate(R.menu.menu_detail_view, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageListTask extends AsyncTask<Void, Void, BrowseDataListAdapter> {

        protected BrowseDataListAdapter doInBackground(Void... v) {
            try {
                return createAdapter();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(BrowseDataListAdapter result) {
            Log.d(Main.LOGTAG, "AsyncTask completed");
            task = null;
            if (result != null) {
                setListAdapter(result);
                ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
                if (type != null) {
                    bar.setSubtitle(String.format("%d/%d/%d/%s : %s", year, month, day, type.getShortCode(), "Select hour"));
                } else if (day != -1) {
                    bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, "Select image"));
                } else if (month != -1) {
                    bar.setSubtitle(String.format("%d/%d: %s", year, month, "Select day"));
                } else if (year != -1) {
                    bar.setSubtitle(String.format("%d: %s", year, "Select month"));
                } else {
                    bar.setSubtitle("Select year");
                }
            } else {
                Toast.makeText(getActivity(), "Error getting the image list.", Toast.LENGTH_LONG);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
