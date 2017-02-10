package eu.flatworld.android.sdoviewer;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataFragment extends ListFragment {
    int year = -1;
    int month = -1;
    int day = -1;
    SDOImageType type = null;
    ArrayList<String> links = null;

    DownloadImageListTask task = null;
    OkHttpClient httpClient;
    private int resolution;

    public BrowseDataFragment() {
        setHasOptionsMenu(true);
    }

    public void setLinks(ArrayList<String> links) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpClient = Util.getNewHttpClient(getActivity());
        if (savedInstanceState != null) {
            day = savedInstanceState.getInt("day", -1);
            month = savedInstanceState.getInt("month", -1);
            year = savedInstanceState.getInt("year", -1);
            type = (SDOImageType) savedInstanceState.getSerializable("type");
            links = (ArrayList<String>) savedInstanceState.getSerializable("links");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setFastScrollEnabled(true);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        resolution = Integer.parseInt(pref.getString("resolution", "2048"));

        setListShown(true);
        setListAdapter(null);
        setEmptyText(getString(R.string.loading___));
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.browse_data);
        if (type != null) {
            bar.setSubtitle(String.format("%d/%d/%d/%s : %s", year, month, day, type.getShortCode(), getString(R.string.loading_hours)));
        } else if (day != -1) {
            bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, getString(R.string.loading_images)));
        } else if (month != -1) {
            bar.setSubtitle(String.format("%d/%d: %s", year, month, getString(R.string.loading_days)));
        } else if (year != -1) {
            bar.setSubtitle(String.format("%d: %s", year, getString(R.string.loading_months)));
        } else {
            bar.setSubtitle(R.string.loading_years);
        }

        Log.d(SDOViewerConstants.LOGTAG, "Start AsyncTask");
        task = new DownloadImageListTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null && !task.isCancelled()) {
            Log.d(SDOViewerConstants.LOGTAG, "Cancel AsyncTask");
            task.cancel(true);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("day", day);
        outState.putInt("month", month);
        outState.putInt("year", year);
        outState.putSerializable("type", type);
        outState.putSerializable("links", links);
    }


    BrowseDataListAdapter createAdapter() throws IOException {
        if (type != null) {
            Log.d(SDOViewerConstants.LOGTAG, "Load images");
            return new BrowseDataListAdapter(getActivity(), Util.loadImages(year, month, day, links, type, resolution));
        } else if (day != -1) {
            Log.d(SDOViewerConstants.LOGTAG, "Load image types");
            if (links == null) {
                links = Util.loadLinks(httpClient, year, month, day);
            }
            return new BrowseDataListAdapter(getActivity(), Util.loadImageTypes());
        } else if (month != -1) {
            Log.d(SDOViewerConstants.LOGTAG, "Load days");
            return new BrowseDataListAdapter(getActivity(), Util.loadDays(year, month));
        } else if (year != -1) {
            Log.d(SDOViewerConstants.LOGTAG, "Load months");
            return new BrowseDataListAdapter(getActivity(), Util.loadMonths(year));
        } else {
            Log.d(SDOViewerConstants.LOGTAG, "Load years");
            return new BrowseDataListAdapter(getActivity(), Util.loadYears());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BrowseDataListItem bdli = (BrowseDataListItem) getListAdapter().getItem(position);
        if (type != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("imageType", type);
            bundle.putString("imageUrl", bdli.getUrl());
            bundle.putString("description", Util.getDescription(type));
            ImageDetailFragment f = new ImageDetailFragment();
            f.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        } else if (day != -1) {
            SDOImageType type = SDOImageType.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            bdf.setDay(day);
            bdf.setType(type);
            bdf.setLinks(links);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("hour").commit();
        } else if (month != -1) {
            int day = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            bdf.setDay(day);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("type").commit();
        } else if (year != -1) {
            int month = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("day").commit();
        } else {
            int year = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack("month").commit();
        }
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
        String errorString = null;

        protected BrowseDataListAdapter doInBackground(Void... v) {
            try {
                errorString = null;
                return createAdapter();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    errorString = getString(R.string.error_getting_data) + ":  " + getString(R.string.timeout);
                } else {
                    errorString = getString(R.string.error_getting_data) + ": " + e.getMessage();
                }
                Log.d(SDOViewerConstants.LOGTAG, "AsyncTask completed with error: " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(BrowseDataListAdapter result) {
            task = null;
            if (result != null) {
                Log.d(SDOViewerConstants.LOGTAG, "AsyncTask completed");
                setListAdapter(result);
                ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
                if (type != null) {
                    bar.setSubtitle(String.format("%d/%d/%d/%s : %s", year, month, day, type.getShortCode(), getString(R.string.select_hour)));
                } else if (day != -1) {
                    bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, getString(R.string.select_image)));
                } else if (month != -1) {
                    bar.setSubtitle(String.format("%d/%d: %s", year, month, getString(R.string.select_day)));
                } else if (year != -1) {
                    bar.setSubtitle(String.format("%d: %s", year, getString(R.string.select_month)));
                } else {
                    bar.setSubtitle(getString(R.string.select_year));
                }
            } else {
                setEmptyText(errorString);
                setListAdapter(null);
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(SDOViewerConstants.LOGTAG, "AsyncTask cancelled");
            super.onCancelled();
        }
    }

}
