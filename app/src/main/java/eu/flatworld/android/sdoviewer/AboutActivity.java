package eu.flatworld.android.sdoviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String version;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ex) {
            version = "-";
            ex.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.aboutTVName);
        try {
            tv.setText(getResources().getString(R.string.app_name) + " " + version + "\nwww.flatworld.eu");
        } catch (Exception e) {
            tv.setText(getResources().getString(R.string.app_name) + "\nwww.flatworld.eu");
        }

        tv = (TextView) findViewById(R.id.aboutTVName);
        tv.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.aboutTVSDO);
        tv.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.aboutTVName) {
            Uri webpage = Uri.parse("http://www.flatworld.eu");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent != null) {
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.aboutTVSDO) {
            Uri webpage = Uri.parse("http://sdo.gsfc.nasa.gov/");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent != null) {
                startActivity(intent);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
