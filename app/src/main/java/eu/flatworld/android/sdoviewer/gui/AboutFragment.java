package eu.flatworld.android.sdoviewer.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;


public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        String version;
        try {
            version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (Exception ex) {
            version = "-";
            ex.printStackTrace();
        }


        TextView tv;
        tv = (TextView) view.findViewById(R.id.aboutTVName);
        tv.setText(Html.fromHtml(getString(R.string.flatworld_website).replaceAll("###VERSION###", version).replaceAll("###APPNAME###", getString(R.string.app_name))));
        tv.setMovementMethod(new LinkMovementMethod());
        tv = (TextView) view.findViewById(R.id.aboutTVSDO);
        tv.setText(Html.fromHtml(getString(R.string.sdo_website)));
        tv.setMovementMethod(new LinkMovementMethod());
        //tv = (TextView) view.findViewById(R.id.tvPrivacyPolicy);
        //tv.setText(Html.fromHtml(getString(R.string.privacyPolicy)));
        tv.setMovementMethod(new LinkMovementMethod());

        ((MainActivity) activity).getSupportActionBar().setTitle(R.string.about);
        ((MainActivity) activity).getSupportActionBar().setSubtitle(null);

        TextView aboutCredits = (TextView) view.findViewById(R.id.aboutCredits);
        aboutCredits.setText(Html.fromHtml(getString(R.string.aboutCredits)));
        aboutCredits.setMovementMethod(new LinkMovementMethod());
    }

}
