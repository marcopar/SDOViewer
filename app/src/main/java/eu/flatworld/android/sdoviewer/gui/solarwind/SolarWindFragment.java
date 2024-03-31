package eu.flatworld.android.sdoviewer.gui.solarwind;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.data.SolarWind;
import eu.flatworld.android.sdoviewer.data.Util;
import eu.flatworld.android.sdoviewer.io.OkHttpClientFactory;
import okhttp3.OkHttpClient;


public class SolarWindFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView tvFlux;
    TextView tvSpeed;
    TextView tvBt;
    TextView tvBz;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
    private SwipeRefreshLayout swipeLayout;

    public SolarWindFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        swipeLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_solar_wind, container, false);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        return swipeLayout;
    }

    @Override
    public void onDestroyView() {
        //to avoid overlapping fragment when going back during refresh
        //https://code.google.com/p/android/issues/detail?id=78062
        swipeLayout.setRefreshing(false);
        swipeLayout.clearAnimation();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ((MainActivity) activity).getSupportActionBar().setTitle(R.string.solar_wind);
        ((MainActivity) activity).getSupportActionBar().setSubtitle(null);

        tvBt = (TextView) view.findViewById(R.id.tvValueBt);
        tvBz = (TextView) view.findViewById(R.id.tvValueBz);
        tvFlux = (TextView) view.findViewById(R.id.tvFluxValue);
        tvSpeed = (TextView) view.findViewById(R.id.tvSpeedValue);

        swipeLayout.setRefreshing(true);
        new RefreshTask().execute("");
    }

    boolean refresh() {
        final Activity activity = getActivity();
        if (activity == null) {
            return true;
        }
        Log.i(GlobalConstants.LOGTAG, "Loading solar wind data");
        OkHttpClient client = OkHttpClientFactory.getNewOkHttpClient(Util.getHttpsSafeModeEnabled(activity));
        Speed s = null;
        Flux f = null;
        MagneticField mf = null;
        boolean errors = false;
        try {
            s = gson.fromJson(Util.getUrl(client, SolarWind.getLatestURL(SolarWind.SPEED)).body().string(), Speed.class);
        } catch (Exception ex) {
            Log.e(GlobalConstants.LOGTAG, "Solar Wind speed error", ex);
            errors = true;
        }
        try {
            f = gson.fromJson(Util.getUrl(client, SolarWind.getLatestURL(SolarWind.FLUX)).body().string(), Flux.class);
        } catch (Exception ex) {
            Log.e(GlobalConstants.LOGTAG, "Solar Wind flux error", ex);
            errors = true;
        }
        try {
            mf = gson.fromJson(Util.getUrl(client, SolarWind.getLatestURL(SolarWind.MAGNETIC_FIELD)).body().string(), MagneticField.class);
        } catch (Exception ex) {
            Log.e(GlobalConstants.LOGTAG, "Solar Wind magnetic field error", ex);
            errors = true;
        }

        final boolean fErrors = errors;
        final Speed fS = s;
        final Flux fF = f;
        final MagneticField fMf = mf;
        final Activity fActivity = activity;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (fErrors) {
                    Toast.makeText(fActivity, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show();
                }
                setGUIValues(fActivity, fS, fF, fMf);
            }
        });

        return errors;
    }

    void setGUIValues(Activity activity, Speed speed, Flux flux, MagneticField magneticField) {
        Resources r = activity.getResources();
        String sbt = r.getText(R.string.t_magneticfield_value_error).toString();
        String sbz = r.getText(R.string.z_magneticfield_value_error).toString();
        String sflux = r.getText(R.string.flux_value_error).toString();
        String sspeed = r.getText(R.string.speed_value_error).toString();
        if (speed != null) {
            sspeed = String.format(r.getText(R.string.speed_value_format).toString(), speed.getWindSpeed());
        }
        if (flux != null) {
            sflux = String.format(r.getText(R.string.flux_value_format).toString(), flux.getFlux());
        }
        if (magneticField != null) {
            sbt = String.format(r.getText(R.string.t_magneticfield_value_format).toString(), magneticField.getBt());
            sbz = String.format(r.getText(R.string.z_magneticfield_value_format).toString(), magneticField.getBz());
        }
        tvBt.setText(sbt);
        tvBz.setText(sbz);
        tvFlux.setText(sflux);
        tvSpeed.setText(sspeed);
    }

    @Override
    public void onRefresh() {
        new RefreshTask().execute("");
    }

    private class RefreshTask extends AsyncTask<String, Integer, Boolean> {
        public RefreshTask() {

        }

        protected Boolean doInBackground(String... none) {
            return refresh();
        }

        protected void onProgressUpdate(Integer... none) {
        }

        protected void onPostExecute(Boolean result) {
            swipeLayout.setRefreshing(false);
        }
    }

}
