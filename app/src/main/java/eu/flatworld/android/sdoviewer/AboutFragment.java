package eu.flatworld.android.sdoviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment implements View.OnClickListener {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String version;
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (Exception ex) {
            version = "-";
            ex.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        TextView tv = (TextView) view.findViewById(R.id.aboutTVName);
        try {
            tv.setText(getResources().getString(R.string.app_name) + " " + version + "\nwww.flatworld.eu");
        } catch (Exception e) {
            tv.setText(getResources().getString(R.string.app_name) + "\nwww.flatworld.eu");
        }

        tv = (TextView) view.findViewById(R.id.aboutTVName);
        tv.setOnClickListener(this);
        tv = (TextView) view.findViewById(R.id.aboutTVSDO);
        tv.setOnClickListener(this);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.about);
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(null);
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
}
