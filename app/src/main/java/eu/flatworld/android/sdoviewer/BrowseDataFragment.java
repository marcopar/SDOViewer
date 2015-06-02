package eu.flatworld.android.sdoviewer;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataFragment extends ListFragment {
    int year = -1;
    int month = -1;
    int day = -1;
    SDOImageType type = null;

    private int resolution;

    public BrowseDataFragment() {
        setHasOptionsMenu(true);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public SDOImageType getType() {
        return type;
    }

    public void setType(SDOImageType type) {
        this.type = type;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        resolution = Integer.parseInt(pref.getString("resolution", "2048"));
        if (savedInstanceState != null) {
            day = savedInstanceState.getInt("day", -1);
            month = savedInstanceState.getInt("month", -1);
            year = savedInstanceState.getInt("year", -1);
            type = (SDOImageType) savedInstanceState.getSerializable("type");
        }
        new DownloadImageListTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.browse_data);
        if (type != null) {
            bar.setSubtitle(String.format("%d/%d/%d/%s : %s", year, month, day, type.getShortCode(), "Select image"));
        } else if (day != -1) {
            bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, "Select image type"));
        } else if (month != -1) {
            bar.setSubtitle(String.format("%d/%d: %s", year, month, "Select day"));
        } else if (year != -1) {
            bar.setSubtitle(String.format("%d: %s", year, "Select month"));
        } else {
            bar.setSubtitle("Select year");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("day", day);
        outState.putInt("month", month);
        outState.putInt("year", year);
        outState.putSerializable("type", type);
    }

    BrowseDataListAdapter createAdapter() throws IOException {
        if (type != null) {
            return new BrowseDataListAdapter(getActivity(), Util.getImages(year, month, day, type, resolution));
        } else if (day != -1) {
            return new BrowseDataListAdapter(getActivity(), Util.getImageTypes());
        } else if (month != -1) {
            return new BrowseDataListAdapter(getActivity(), Util.getDays(year, month));
        } else if (year != -1) {
            return new BrowseDataListAdapter(getActivity(), Util.getMonths(year));
        } else {
            return new BrowseDataListAdapter(getActivity(), Util.getYears());
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
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack(null).commit();
        } else if (month != -1) {
            int day = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            bdf.setDay(day);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack(null).commit();
        } else if (year != -1) {
            int month = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            bdf.setMonth(month);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack(null).commit();
        } else {
            int year = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdf = new BrowseDataFragment();
            bdf.setYear(year);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdf).addToBackStack(null).commit();
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
        protected BrowseDataListAdapter doInBackground(Void... v) {
            try {
                return createAdapter();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(BrowseDataListAdapter result) {
            if (result != null) {
                setListAdapter(result);
            } else {
                Toast.makeText(getActivity(), "Error getting the image list.", Toast.LENGTH_LONG);
            }
        }
    }

}
