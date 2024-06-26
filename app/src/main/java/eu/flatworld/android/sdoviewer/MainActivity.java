package eu.flatworld.android.sdoviewer;

import android.app.FragmentManager;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import eu.flatworld.android.sdoviewer.data.Util;
import eu.flatworld.android.sdoviewer.gui.AboutFragment;
import eu.flatworld.android.sdoviewer.gui.SettingsFragment;
import eu.flatworld.android.sdoviewer.gui.browse.BrowseDataFragment;
import eu.flatworld.android.sdoviewer.gui.solarwind.SolarWindFragment;
import eu.flatworld.android.sdoviewer.gui.thesunnow.TheSunNowFragment;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    public MainActivity() {
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
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
                                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new TheSunNowFragment()).commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_browse_data) {
                                BrowseDataFragment bdfy = new BrowseDataFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("year").commit();
                            }
                            if (menuItem.getItemId() == R.id.nav_solar_wind) {
                                SolarWindFragment bdfy = new SolarWindFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bdfy).addToBackStack("solarwind").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_settings) {
                                SettingsFragment f = new SettingsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack("settings").commit();
                            }
                            if (menuItem.getItemId() == R.id.action_about) {
                                AboutFragment f = new AboutFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack("about").commit();
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