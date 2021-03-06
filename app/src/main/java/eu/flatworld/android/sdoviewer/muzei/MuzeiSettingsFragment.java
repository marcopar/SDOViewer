package eu.flatworld.android.sdoviewer.muzei;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import eu.flatworld.android.sdoviewer.R;

/**
 * Created by marcopar on 03/07/15.
 */
public class MuzeiSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public MuzeiSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.muzei_preferences);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MuzeiSettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.muzei_settings);
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
        Preference pref = findPreference(key);
        if (pref.getKey().equals(GlobalConstants.PREFERENCES_MUZEINETWORKMODE)) {
            ListPreference lp = (ListPreference) pref;
            if (lp.getValue().equals(GlobalConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE_ROAMING)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Warning")
                        .setMessage("Downloading images when roaming may cause significant charges")
                        .setCancelable(true)
                        .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        }
        updatePrefSummary(pref);
    }
}
