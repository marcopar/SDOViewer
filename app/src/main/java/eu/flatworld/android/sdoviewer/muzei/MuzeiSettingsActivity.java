package eu.flatworld.android.sdoviewer.muzei;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import eu.flatworld.android.sdoviewer.R;


public class MuzeiSettingsActivity extends AppCompatActivity {

    public MuzeiSettingsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzei_settings);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.content_frame, new MuzeiSettingsFragment(), "muzeisettings").commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}