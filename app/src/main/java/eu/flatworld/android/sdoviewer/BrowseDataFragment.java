package eu.flatworld.android.sdoviewer;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataFragment extends ListFragment {
    int year = -1;
    int month = -1;
    int day = -1;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            day = savedInstanceState.getInt("day", -1);
            month = savedInstanceState.getInt("month", -1);
            year = savedInstanceState.getInt("year", -1);
        }
        setListAdapter(createAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.browse_data);
        if (day != -1) {
            bar.setSubtitle(String.format("%d/%d/%d: %s", year, month, day, "Select image"));
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
    }

    BrowseDataListAdapter createAdapter() {
        if (day != -1) {
            return new BrowseDataListAdapter(getActivity(), new ArrayList<BrowseDataListItem>());
        } else if (month != -1) {
            return new BrowseDataListAdapter(getActivity(), Util.getDays(getActivity(), year, month));
        } else if (year != -1) {
            return new BrowseDataListAdapter(getActivity(), Util.getMonths(getActivity(), year));
        } else {
            return new BrowseDataListAdapter(getActivity(), Util.getYears(getActivity()));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BrowseDataListItem bdli = (BrowseDataListItem) getListAdapter().getItem(position);
        BrowseDataListAdapter adapter = createAdapter();
        if (month != -1) {
            int day = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdfi = new BrowseDataFragment();
            bdfi.setYear(year);
            bdfi.setMonth(month);
            bdfi.setDay(day);
            bdfi.setListAdapter(adapter);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfi).addToBackStack(null).commit();
        } else if (year != -1) {
            int month = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdfd = new BrowseDataFragment();
            bdfd.setYear(year);
            bdfd.setMonth(month);
            bdfd.setListAdapter(adapter);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfd).addToBackStack(null).commit();
        } else {
            int year = Integer.valueOf(bdli.getUrl());
            BrowseDataFragment bdfm = new BrowseDataFragment();
            bdfm.setYear(year);
            bdfm.setListAdapter(adapter);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfm).addToBackStack(null).commit();
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

}
