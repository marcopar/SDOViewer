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

import com.google.firebase.analytics.FirebaseAnalytics;

import eu.flatworld.android.sdoviewer.data.Util;
import eu.flatworld.android.sdoviewer.gui.AboutFragment;
import eu.flatworld.android.sdoviewer.gui.SettingsFragment;
import eu.flatworld.android.sdoviewer.gui.browse.BrowseDataFragment;
import eu.flatworld.android.sdoviewer.gui.solarwind.SolarWindFragment;
import eu.flatworld.android.sdoviewer.gui.thesunnow.TheSunNowFragment;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private FirebaseAnalytics firebaseAnalytics;

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

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //configure initial values for https compat mode
        Util.getHttpsSafeModeEnabled(this);

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
                                Bundle b = new Bundle();
                                b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "the_sun_now");
                                getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);
                                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TheSunNowFragment()).commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_browse_data) {
                                Bundle b = new Bundle();
                                b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browse");
                                getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);
                                BrowseDataFragment bdfy = new BrowseDataFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("year").commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_solar_wind) {
                                Bundle b = new Bundle();
                                b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "solar_wind");
                                getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);
                                SolarWindFragment bdfy = new SolarWindFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("solarwind").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_settings) {
                                Bundle b = new Bundle();
                                b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "settings");
                                getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);
                                SettingsFragment f = new SettingsFragment();
                                getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack("settings").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_about) {
                                Bundle b = new Bundle();
                                b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "about");
                                getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);
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

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
}