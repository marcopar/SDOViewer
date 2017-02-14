package eu.flatworld.android.sdoviewer.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;


public class SolarWindFragment extends Fragment {


    public SolarWindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solar_wind, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.solar_wind);
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(null);

    }

}
