package eu.flatworld.android.sdoviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class GridActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private GridImageAdapter gridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        setContentView(R.layout.activity_grid);

        final GridView gridview = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridImageAdapter(this);
        gridview.setAdapter(gridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridImageAdapter a = (GridImageAdapter) gridview.getAdapter();
                Intent intent = new Intent(getBaseContext(), DetailViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("IMAGE", a.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent i = new Intent(this, AboutActivity.class);
            this.startActivity(i);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            this.startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
