package eu.flatworld.android.sdoviewer;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import eu.flatworld.android.sdoviewer.gui.AboutFragment;
import eu.flatworld.android.sdoviewer.gui.SettingsFragment;
import eu.flatworld.android.sdoviewer.gui.SolarWindFragment;
import eu.flatworld.android.sdoviewer.gui.browse.BrowseDataFragment;
import eu.flatworld.android.sdoviewer.gui.thesunnow.TheSunNowFragment;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;

    public MainActivity() {
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SDO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configure initial values for https compat mode
        Util.getHttpModeEnabled(this);

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
                                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TheSunNowFragment()).commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_browse_data) {
                                BrowseDataFragment bdfy = new BrowseDataFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("year").commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_weather) {
                                SolarWindFragment bdfy = new SolarWindFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("solarwind").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_settings) {
                                SettingsFragment f = new SettingsFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack("settings").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_about) {
                                AboutFragment f = new AboutFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack("about").commit();
                            }
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.content_frame, new TheSunNowFragment(), "thesunnow").commit();
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