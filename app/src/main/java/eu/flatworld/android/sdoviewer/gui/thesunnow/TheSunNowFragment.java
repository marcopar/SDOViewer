package eu.flatworld.android.sdoviewer.gui.thesunnow;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.data.SDO;
import eu.flatworld.android.sdoviewer.gui.ImageDetailFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TheSunNowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeLayout;
    private GridImageAdapter gridAdapter;

    public TheSunNowFragment() {
        setHasOptionsMenu(true);
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
        swipeLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        final GridView gridview = (GridView) swipeLayout.findViewById(R.id.gridView);
        gridAdapter = new GridImageAdapter(getActivity());
        gridview.setAdapter(gridAdapter);

        gridview.setOnItemClickListener((parent, v, position, id) -> {
            GridImageAdapter a = (GridImageAdapter) gridview.getAdapter();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int resolution = Integer.parseInt(pref.getString("resolution", "2048"));
            Bundle bundle = new Bundle();
            SDO imageType = a.getItem(position);
            bundle.putSerializable("imageType", imageType);
            bundle.putString("imageUrl", SDO.getLatestURL(imageType, resolution, false));
            bundle.putString("pfssUrl", SDO.getLatestURL(imageType, resolution, true));
            bundle.putString("description", SDO.getDescription(imageType));
            ImageDetailFragment f = new ImageDetailFragment();
            f.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        });

        return swipeLayout;
    }

    @Override
    public void onDestroyView() {
        //to avoid overlapping fragment when going back during refresh
        //https://code.google.com/p/android/issues/detail?id=78062
        swipeLayout.setRefreshing(false);
        swipeLayout.clearAnimation();
        super.onDestroyView();
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
        new Handler().postDelayed(() -> swipeLayout.setRefreshing(false), 1000);
    }
}
