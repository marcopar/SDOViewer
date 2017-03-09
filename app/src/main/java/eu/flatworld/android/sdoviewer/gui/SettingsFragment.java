package eu.flatworld.android.sdoviewer.gui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.io.PicassoInstance;

/**
 * Created by marcopar on 03/07/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().removePreference(getPreferenceScreen().findPreference(GlobalConstants.PREFERENCES_FIRSTRUN));

        PreferenceCategory pc = new PreferenceCategory(getActivity());
        pc.setTitle(R.string.muzei_settings_main_app);
        getPreferenceScreen().addPreference(pc);
        addPreferencesFromResource(R.xml.muzei_preferences);


        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        ((MainActivity) activity).getSupportActionBar().setTitle(R.string.settings);
        ((MainActivity) activity).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else if (p instanceof PreferenceScreen) {
            PreferenceScreen pCat = (PreferenceScreen) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }

    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            String s = p.getSummary().toString();
            s = s.substring(0, s.indexOf(":") + 1);
            p.setSummary(String.format("%s %s", s, String.valueOf(listPref.getEntry())));
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            String s = p.getSummary().toString();
            s = s.substring(0, s.indexOf(":") + 1);
            p.setSummary(String.format("%s %s", s, String.valueOf(editTextPref.getText())));
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
        if (key.equals(GlobalConstants.PREFERENCES_HTTPCOMPATIBILITYMODE)) {
            PicassoInstance.reset();
        }
    }
}
