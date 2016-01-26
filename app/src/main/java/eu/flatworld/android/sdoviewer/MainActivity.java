package eu.flatworld.android.sdoviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    SDOViewerApplication application;
    DrawerLayout mDrawerLayout;

    ScreenType currentScreen = ScreenType.THESUNNOW;

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
    public void onBackStackChanged() {
        IFragmentProperties f = (IFragmentProperties) getFragmentManager().findFragmentById(R.id.frame_master);
        setupScreen(f.getScreenType());
        if (findViewById(R.id.frame_detail) != null && getFragmentManager().findFragmentById(R.id.frame_detail) == null) {
            getFragmentManager().beginTransaction().replace(R.id.frame_detail, new ImageDetailEmptyFragment()).commit();
        }
    }

    void setImageDetail(Bundle bundle) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (findViewById(R.id.frame_detail) != null) {
            Fragment f = new ImageDetailFragment();
            f.setArguments(bundle);
            ft.replace(R.id.frame_detail, f, null);
        } else {
            Fragment f = new ImageDetailFragment();
            f.setArguments(bundle);
            ft.replace(R.id.frame_master, f, null).addToBackStack(null);
        }
        ft.commit();
    }

    void setupScreen(ScreenType screen) {
        boolean setDoubleView;

        if (findViewById(R.id.frame_detail) != null) {
            setDoubleView = screen.equals(ScreenType.THESUNNOW) | screen.equals(ScreenType.BROWSE);
        } else {
            setDoubleView = false;
        }

        if (setDoubleView == false) {
            if (findViewById(R.id.frame_detail) != null) {
                Fragment f = getFragmentManager().findFragmentById(R.id.frame_detail);
                if (f != null) {
                    getFragmentManager().beginTransaction().remove(f).commit();
                }
                findViewById(R.id.frame_detail).setVisibility(View.GONE);
            }
        } else {
            if (findViewById(R.id.frame_detail) != null) {
                findViewById(R.id.frame_detail).setVisibility(View.VISIBLE);
            }
        }

        currentScreen = screen;
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
            ScreenType cs = (ScreenType) savedInstanceState.getSerializable("currentscreen");
            setupScreen(cs);
            if (cs == ScreenType.THESUNNOW) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (findViewById(R.id.frame_detail) != null) {
                    Fragment f = getFragmentManager().findFragmentById(R.id.frame_master);
                    if (f instanceof ImageDetailFragment) {
                        Bundle b = f.getArguments();
                        ft.remove(f);
                        ft.replace(R.id.frame_master, new TheSunNowFragment());
                        f = new ImageDetailFragment();
                        f.setArguments(b);
                        ft.replace(R.id.frame_detail, f);
                    } else {
                        if (getFragmentManager().findFragmentById(R.id.frame_detail) == null) {
                            ft.replace(R.id.frame_detail, new ImageDetailEmptyFragment());
                        }
                    }
                    getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    Fragment f = getFragmentManager().findFragmentById(R.id.frame_detail);
                    if (f instanceof ImageDetailFragment) {
                        Bundle b = f.getArguments();
                        ft.remove(f);
                        f = new ImageDetailFragment();
                        f.setArguments(b);
                        ft.replace(R.id.frame_master, f).addToBackStack(null);
                    }
                }
                ft.commit();
            }
            if (cs == ScreenType.BROWSE) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (findViewById(R.id.frame_detail) != null) {
                    Fragment f = getFragmentManager().findFragmentById(R.id.frame_master);
                    if (f instanceof ImageDetailFragment) {
                        Bundle b = f.getArguments();
                        ft.remove(f);
                        ft.replace(R.id.frame_master, new BrowseDataFragment());
                        f = new ImageDetailFragment();
                        f.setArguments(b);
                        ft.replace(R.id.frame_detail, f);
                    } else {
                        if (getFragmentManager().findFragmentById(R.id.frame_detail) == null) {
                            ft.replace(R.id.frame_detail, new ImageDetailEmptyFragment());
                        }
                    }
                    getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    Fragment f = getFragmentManager().findFragmentById(R.id.frame_detail);
                    if (f instanceof ImageDetailFragment) {
                        Bundle b = f.getArguments();
                        ft.remove(f);
                        f = new ImageDetailFragment();
                        f.setArguments(b);
                        ft.replace(R.id.frame_master, f).addToBackStack(null);
                    }
                }
                ft.commit();
            }
        } else {
            setupScreen(ScreenType.THESUNNOW);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment f = new TheSunNowFragment();
            ft.replace(R.id.frame_master, f);
            if (findViewById(R.id.frame_detail) != null) {
                f = new ImageDetailEmptyFragment();
                ft.replace(R.id.frame_detail, f);
            }
            ft.commit();
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_the_sun_now) {
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setupScreen(ScreenType.THESUNNOW);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment f = new TheSunNowFragment();
            ft.replace(R.id.frame_master, f);
            if (findViewById(R.id.frame_detail) != null) {
                f = new ImageDetailEmptyFragment();
                ft.replace(R.id.frame_detail, f);
            }
            ft.commit();
        }
        if (menuItem.getItemId() == R.id.nav_browse_data) {
            EventBus.getDefault().post(new BrowseDataEvent(null));
        }
        if (menuItem.getItemId() == R.id.action_settings) {
            setupScreen(ScreenType.SETTINGS);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment f = new SettingsFragment();
            ft.replace(R.id.frame_master, f).addToBackStack(null);
            ft.commit();
        }
        if (menuItem.getItemId() == R.id.action_about) {
            setupScreen(ScreenType.ABOUT);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment f = new AboutFragment();
            ft.replace(R.id.frame_master, f).addToBackStack(null);
            ft.commit();
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void onEvent(BrowseDataEvent event) {
        Bundle b = event.getBundle();
        setupScreen(ScreenType.BROWSE);
    }

    public void onEvent(ImageSelectedEvent event) {
        setImageDetail(event.getBundle());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentscreen", currentScreen);
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

}