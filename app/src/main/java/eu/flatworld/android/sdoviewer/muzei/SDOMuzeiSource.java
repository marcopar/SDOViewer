package eu.flatworld.android.sdoviewer.muzei;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.SDO;
import eu.flatworld.android.sdoviewer.Util;
import eu.flatworld.android.sdoviewer.io.OkHttpClientFactory;
import okhttp3.OkHttpClient;

/**
 * Created by marcopar on 26/01/17.
 */

public class SDOMuzeiSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "NASA-SDO";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    int updateInterval = 3600000;
    int resolution = 2048;
    OkHttpClient httpClient;
    String networkMode = GlobalConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE;
    private FirebaseAnalytics firebaseAnalytics;

    public SDOMuzeiSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        httpClient = OkHttpClientFactory.getNewOkHttpsSafeClient();

        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        updateInterval = Integer.parseInt(pref.getString(GlobalConstants.PREFERENCES_MUZEIUPDATEINTERVAL, "60"));
        Log.i(GlobalConstants.LOGTAG, String.format("Update interval is set to %d", updateInterval));
        networkMode = pref.getString(GlobalConstants.PREFERENCES_MUZEINETWORKMODE, GlobalConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE);
        resolution = Integer.parseInt(pref.getString(GlobalConstants.PREFERENCES_MUZEIRESOLUTION, "2048"));

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onEnabled() {
        firebaseAnalytics.logEvent(GlobalConstants.ANALYTICS_MUZEI_ENABLED, Bundle.EMPTY);
    }

    @Override
    public void onDisabled() {
        firebaseAnalytics.logEvent(GlobalConstants.ANALYTICS_MUZEI_DISABLED, Bundle.EMPTY);
    }


    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        Log.i(GlobalConstants.LOGTAG, String.format("Network mode: %s", networkMode));
        switch (networkMode) {
            case GlobalConstants.PREFERENCES_MUZEINETWORKMODE_WIFI:
                if (!Util.isWifiConnected(this)) {
                    Log.i(GlobalConstants.LOGTAG, "No WIFI, skipping publish");
                    throw new RetryException();
                }
                break;
            case GlobalConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE:
                if (Util.isMobileConnected(this) && Util.isRoaming(this)) {
                    Log.i(GlobalConstants.LOGTAG, "Roaming is active, skipping publish");
                    scheduleNextUpdate();
                    throw new RetryException();
                }
                break;
        }

        List<SDO> lTypes = new ArrayList<>(Arrays.asList(SDO.AIA_193, SDO.AIA_304, SDO.AIA_171, SDO.AIA_211, SDO.AIA_131, SDO.AIA_335, SDO.AIA_094));
        if (getCurrentArtwork() != null && getCurrentArtwork().getToken() != null) {
            try {
                //remove the last loaded type so the image changes type every time
                SDO currentType = SDO.valueOf(getCurrentArtwork().getToken());
                lTypes.remove(currentType);
            } catch (Exception ex) {
            }
        }
        Collections.shuffle(lTypes);
        SDO type = lTypes.get(0);

        String url = SDO.getLatestURL(type, resolution, false);
        Date lastModified = null;
        try {
            lastModified = Util.getLastModified(httpClient, url);
        } catch (IOException ex) {
            Log.e(GlobalConstants.LOGTAG, "Error retrieving last modified header", ex);
            throw new RetryException();
        }
        //muzei caches images by uri, if we use the same uri (like it happens for the "latest" images, muzei never updates
        //for this reason we add a parameter in the url containing the last modified date
        //this way we force muzei to update but we force it only when a different image is effectively online
        Uri uri = Uri.parse(String.format("%s?Last-Modified=%s", url, sdf.format(lastModified)));
        String token = type.name();
        Log.i(GlobalConstants.LOGTAG, String.format("Publish artwork token[%s] url[%s]", token, uri.toString()));
        publishArtwork(new Artwork.Builder()
                .title(type.toString())
                .byline(getResources().getString(R.string.muzei_byline))
                .token(token)
                .imageUri(uri)
                .viewIntent(new Intent(Intent.ACTION_VIEW, uri))
                .build());


        scheduleNextUpdate();
    }

    void scheduleNextUpdate() {
        scheduleUpdate(System.currentTimeMillis() + (updateInterval * 60 * 1000));
    }
}
