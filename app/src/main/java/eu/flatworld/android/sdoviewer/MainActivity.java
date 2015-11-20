package eu.flatworld.android.sdoviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import de.greenrobot.event.EventBus;
import eu.flatworld.android.sdoviewer.eventbus.BrowseDataEvent;
import eu.flatworld.android.sdoviewer.eventbus.ImageSelectedEvent;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SDOViewerApplication application;
    DrawerLayout mDrawerLayout;
    TheSunNowFragment theSunNowFragment;
    BrowseDataFragment browseDataFrament;
    SettingsFragment settingsFragment;
    AboutFragment aboutFragment;
    ImageDetailFragment imageDetailFragment;
    ImageDetailEmptyFragment imageDetailEmptyFragment;
    SCREEN currentScreen = SCREEN.THESUNNOW;

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

    void setImageDetail(Bundle bundle) {
        ImageDetailFragment f = getImageDetailFragment();
        f.setArguments(bundle);
        Integer id = null;
        if (findViewById(R.id.frame_double).getVisibility() == View.VISIBLE) {
            id = R.id.frame_double_detail;
            getFragmentManager().beginTransaction().
                    replace(id, f, null).
                    commit();
        } else {
            id = R.id.frame_single_content;
            getFragmentManager().beginTransaction().
                    replace(id, f, null).
                    addToBackStack(null).
                    commit();
        }
    }

    void setupScreen(SCREEN newScreen, Bundle bundle) {
        int orientation = this.getResources().getConfiguration().orientation;
        boolean setDoubleView = false;
        Integer contentId;
        Integer detailId;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (newScreen.equals(SCREEN.THESUNNOW) || newScreen.equals(SCREEN.BROWSE)) {
                setDoubleView = true;
            }
        }
        if (setDoubleView == false) {
            findViewById(R.id.frame_double).setVisibility(View.GONE);
            findViewById(R.id.frame_single).setVisibility(View.VISIBLE);
            contentId = R.id.frame_single_content;
            detailId = null;
        } else {
            findViewById(R.id.frame_double).setVisibility(View.VISIBLE);
            findViewById(R.id.frame_single).setVisibility(View.GONE);
            contentId = R.id.frame_double_content;
            detailId = R.id.frame_double_detail;
        }
        Fragment content = null;
        Fragment detail = null;
        currentScreen = newScreen;
        switch (newScreen) {
            case THESUNNOW:
                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (detailId != null) {
                    content = getTheSunNowFragment();
                    detail = getImageDetailEmptyFragment();
                } else {
                    content = getTheSunNowFragment();
                }
                break;
            case BROWSE:
                if (detailId != null) {
                    content = getBrowseDataFragment();
                    content.setArguments(bundle);
                    detail = getImageDetailEmptyFragment();
                } else {
                    content = getBrowseDataFragment();
                    content.setArguments(bundle);
                }
                break;
            case SETTINGS:
                content = getSettingsFragment();
                detail = null;
                break;
            case ABOUT:
                content = getAboutFragment();
                detail = null;
                break;
        }
        if (detailId != null) {
            getFragmentManager().beginTransaction().
                    replace(contentId, content, null).
                    replace(detailId, detail, null).
                    addToBackStack(null).
                    commit();
        } else {
            getFragmentManager().beginTransaction().
                    replace(contentId, content, null).
                    addToBackStack(null).
                    commit();
        }
    }

    TheSunNowFragment getTheSunNowFragment() {
        //if(theSunNowFragment == null) {
        theSunNowFragment = new TheSunNowFragment();
        //}
        return theSunNowFragment;
    }

    BrowseDataFragment getBrowseDataFragment() {
        //if(browseDataFrament == null) {
        browseDataFrament = new BrowseDataFragment();
        //}
        return browseDataFrament;
    }

    SettingsFragment getSettingsFragment() {
        //if(settingsFragment == null) {
        settingsFragment = new SettingsFragment();
        //}
        return settingsFragment;
    }

    AboutFragment getAboutFragment() {
        //if(aboutFragment == null) {
        aboutFragment = new AboutFragment();
        //}
        return aboutFragment;
    }

    ImageDetailFragment getImageDetailFragment() {
        //if(imageDetailFragment == null) {
        imageDetailFragment = new ImageDetailFragment();
        //}
        return imageDetailFragment;
    }

    ImageDetailEmptyFragment getImageDetailEmptyFragment() {
        //if(imageDetailEmptyFragment == null) {
        imageDetailEmptyFragment = new ImageDetailEmptyFragment();
        //}
        return imageDetailEmptyFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SDO);
        super.onCreate(savedInstanceState);

        application = (SDOViewerApplication) getApplication();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        setupScreen(currentScreen, null);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_the_sun_now) {
            setupScreen(SCREEN.THESUNNOW, null);
        }
        if (menuItem.getItemId() == R.id.nav_browse_data) {
            EventBus.getDefault().post(new BrowseDataEvent(null));
        }
        if (menuItem.getItemId() == R.id.action_settings) {
            setupScreen(SCREEN.SETTINGS, null);
        }
        if (menuItem.getItemId() == R.id.action_about) {
            setupScreen(SCREEN.ABOUT, null);
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void onEvent(BrowseDataEvent event) {
        setupScreen(SCREEN.BROWSE, event.getBundle());
    }

    public void onEvent(ImageSelectedEvent event) {
        setImageDetail(event.getBundle());
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    enum SCREEN {THESUNNOW, BROWSE, SETTINGS, ABOUT}
}