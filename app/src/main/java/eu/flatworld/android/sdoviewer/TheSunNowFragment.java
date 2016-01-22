package eu.flatworld.android.sdoviewer;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import de.greenrobot.event.EventBus;
import eu.flatworld.android.sdoviewer.eventbus.ImageSelectedEvent;


/**
 * A simple {@link Fragment} subclass.
 */
public class TheSunNowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IFragmentProperties {
    private SwipeRefreshLayout swipeLayout;
    private GridImageAdapter gridAdapter;

    public TheSunNowFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.THESUNNOW;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        swipeLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_thesunnow, container, false);
        swipeLayout.setOnRefreshListener(this);

        final GridView gridview = (GridView) swipeLayout.findViewById(R.id.gridView);
        gridAdapter = new GridImageAdapter(getActivity());
        gridview.setAdapter(gridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridImageAdapter a = (GridImageAdapter) gridview.getAdapter();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int resolution = Integer.parseInt(pref.getString("resolution", "2048"));
                Bundle bundle = new Bundle();
                SDOImageType imageType = a.getItem(position);
                bundle.putSerializable("imageType", imageType);
                bundle.putString("imageUrl", Util.getLatestURL(imageType, resolution, false));
                bundle.putString("pfssUrl", Util.getLatestURL(imageType, resolution, true));
                bundle.putString("description", Util.getDescription(imageType));
                EventBus.getDefault().post(new ImageSelectedEvent(bundle));
            }
        });

        return swipeLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.the_sun_now);
        bar.setSubtitle(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onRefresh() {
        gridAdapter.invalidateCache();
        gridAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
