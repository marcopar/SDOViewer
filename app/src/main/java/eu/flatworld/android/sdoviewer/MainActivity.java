package eu.flatworld.android.sdoviewer;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.nav_the_sun_now) {
                                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new TheSunNowFragment()).commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_browse_data) {
                                BrowseDataFragment bdfy = new BrowseDataFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("year").commit();
                            }
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new TheSunNowFragment(), "thesunnow").commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}