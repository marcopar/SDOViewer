package eu.flatworld.android.sdoviewer;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataFragment extends ListFragment {
    LEVEL level;
    int year = -1;
    public BrowseDataFragment() {
        setHasOptionsMenu(true);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BrowseDataListItem bdli = (BrowseDataListItem) getListAdapter().getItem(position);
        BrowseDataListAdapter adapter = null;
        switch (level) {
            case YEAR:
                int year = Integer.valueOf(bdli.getUrl());
                adapter = new BrowseDataListAdapter(getActivity(), Util.getMonths(getActivity(), year));
                BrowseDataFragment bdfm = new BrowseDataFragment();
                bdfm.setYear(year);
                bdfm.setLevel(LEVEL.MONTH);
                bdfm.setListAdapter(adapter);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfm).addToBackStack(null).commit();
                break;
            case MONTH:
                int month = Integer.valueOf(bdli.getUrl());
                adapter = new BrowseDataListAdapter(getActivity(), Util.getDays(getActivity(), getYear(), month));
                BrowseDataFragment bdfy = new BrowseDataFragment();
                bdfy.setListAdapter(adapter);
                bdfy.setLevel(LEVEL.DAY);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack(null).commit();
                break;
            case DAY:
                break;
            case DATA:
                break;
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

    enum LEVEL {YEAR, MONTH, DAY, DATA}
}
