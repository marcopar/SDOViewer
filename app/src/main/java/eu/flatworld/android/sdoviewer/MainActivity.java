package eu.flatworld.android.sdoviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
    int currentOrientation = Configuration.ORIENTATION_UNDEFINED;
    Bundle currentBundle;

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
            id = R.id.frame_single_master;
            getFragmentManager().beginTransaction().
                    replace(id, f, null).
                    addToBackStack(null).
                    commit();
        }
    }

    void setupScreen(SCREEN newScreen, Bundle bundle) {
        int newOrientation = this.getResources().getConfiguration().orientation;
        boolean setDoubleView;
        Integer contentId;
        Integer detailId;

        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setDoubleView = newScreen.equals(SCREEN.THESUNNOW) || newScreen.equals(SCREEN.BROWSE);
        } else {
            setDoubleView = false;
        }

        if (setDoubleView == false) {
            findViewById(R.id.frame_double).setVisibility(View.GONE);
            findViewById(R.id.frame_single).setVisibility(View.VISIBLE);
            contentId = R.id.frame_single_master;
            detailId = null;
            //cleanup double view fragments
            Fragment fdm = getFragmentManager().findFragmentById(R.id.frame_double_master);
            Fragment fdd = getFragmentManager().findFragmentById(R.id.frame_double_detail);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (fdm != null) {
                ft.remove(fdm);
            }
            if (fdd != null) {
                ft.remove(fdd);
            }
            ft.commit();
        } else {
            findViewById(R.id.frame_double).setVisibility(View.VISIBLE);
            findViewById(R.id.frame_single).setVisibility(View.GONE);
            contentId = R.id.frame_double_master;
            detailId = R.id.frame_double_detail;
            //cleanup single view fragment
            Fragment fdm = getFragmentManager().findFragmentById(R.id.frame_single_master);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (fdm != null) {
                ft.remove(fdm);
            }
            ft.commit();
        }
        Fragment content = null;
        Fragment detail = null;
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
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(contentId, content, null).
                    replace(detailId, detail, null);
            if (currentOrientation == newOrientation) {
                ft.addToBackStack(null);
            }
            ft.commit();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(contentId, content, null);
            if (currentOrientation == newOrientation) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
        currentScreen = newScreen;
        currentOrientation = newOrientation;
        currentBundle = bundle;

    }

    TheSunNowFragment getTheSunNowFragment() {
        theSunNowFragment = new TheSunNowFragment();
        return theSunNowFragment;
    }

    BrowseDataFragment getBrowseDataFragment() {
        browseDataFrament = new BrowseDataFragment();
        return browseDataFrament;
    }

    SettingsFragment getSettingsFragment() {
        settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    AboutFragment getAboutFragment() {
        aboutFragment = new AboutFragment();
        return aboutFragment;
    }

    ImageDetailFragment getImageDetailFragment() {
        imageDetailFragment = new ImageDetailFragment();
        return imageDetailFragment;
    }

    ImageDetailEmptyFragment getImageDetailEmptyFragment() {
        imageDetailEmptyFragment = new ImageDetailEmptyFragment();
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

        if (savedInstanceState != null) {
            currentScreen = (SCREEN) savedInstanceState.getSerializable("currentscreen");
            if (currentScreen == null) {
                currentScreen = SCREEN.THESUNNOW;
            }
            currentBundle = savedInstanceState.getBundle("currentbundle");
        }

        setupScreen(currentScreen, currentBundle);
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
        outState.putSerializable("currentscreen", currentScreen);
        outState.putBundle("currentbundle", currentBundle);
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