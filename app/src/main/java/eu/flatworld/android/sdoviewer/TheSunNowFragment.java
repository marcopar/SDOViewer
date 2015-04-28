package eu.flatworld.android.sdoviewer;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TheSunNowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeLayout;
    private GridImageAdapter gridAdapter;

    public TheSunNowFragment() {

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
                Intent intent = new Intent(getActivity().getBaseContext(), DetailViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("IMAGE", a.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return swipeLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setTitle(R.string.sdo_viewer);
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
