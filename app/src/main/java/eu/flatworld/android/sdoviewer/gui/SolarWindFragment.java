package eu.flatworld.android.sdoviewer.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;


public class SolarWindFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
    private SwipeRefreshLayout swipeLayout;

    public SolarWindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        swipeLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_solar_wind, container, false);
        swipeLayout.setOnRefreshListener(this);

        return swipeLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.solar_wind);
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(null);

    }

    @Override
    public void onRefresh() {
        Speed speed = gson.fromJson("{\"WindSpeed\":\"305\",\"TimeStamp\":\"2017-02-15 21:32:00.000\"}", Speed.class);
        Flux flux = gson.fromJson("{\"Flux\":\"74\",\"TimeStamp\":\"2017-02-16 20:00:00\"}", Flux.class);
        MagneticField magneticField = gson.fromJson("{\"Bt\":\"7\",\"Bz\":\"6\",\"TimeStamp\":\"2017-02-16 21:20:00.000\"}", MagneticField.class);


        swipeLayout.setRefreshing(false);
    }


    public class Speed {

        private int WindSpeed;
        private String TimeStamp;

        public Speed() {
        }

        public int getWindSpeed() {
            return this.WindSpeed;
        }

        public void setWindSpeed(int windSpeed) {
            this.WindSpeed = windSpeed;
        }

        public String getTimeStamp() {
            return this.TimeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.TimeStamp = timeStamp;
        }
    }

    public class Flux {

        private int Flux;
        private String TimeStamp;

        public Flux() {
        }

        public int getFlux() {
            return this.Flux;
        }

        public void setFlux(int flux) {
            this.Flux = flux;
        }

        public String getTimeStamp() {
            return this.TimeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.TimeStamp = timeStamp;
        }
    }

    public class MagneticField {

        private int Bt;
        private int Bz;
        private String TimeStamp;

        public MagneticField() {
        }

        public int getBt() {
            return this.Bt;
        }

        public void setBt(int bt) {
            this.Bt = bt;
        }

        public int getBz() {
            return this.Bz;
        }

        public void setBz(int bz) {
            this.Bz = bz;
        }

        public String getTimeStamp() {
            return this.TimeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.TimeStamp = timeStamp;
        }
    }


}
