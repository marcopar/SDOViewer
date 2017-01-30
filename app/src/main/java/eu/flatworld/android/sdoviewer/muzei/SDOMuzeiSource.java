package eu.flatworld.android.sdoviewer.muzei;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.SDOImageType;
import eu.flatworld.android.sdoviewer.SDOViewerConstants;
import eu.flatworld.android.sdoviewer.Util;

/**
 * Created by marcopar on 26/01/17.
 */

public class SDOMuzeiSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "NASA-SDO";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    int updateInterval = 3600000;
    int resolution = 2048;

    String networkMode = SDOViewerConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE;

    public SDOMuzeiSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        updateInterval = Integer.parseInt(pref.getString(SDOViewerConstants.PREFERENCES_MUZEIUPDATEINTERVAL, "60"));
        Log.i(SDOViewerConstants.LOGTAG, String.format("Update interval is set to %d", updateInterval));
        networkMode = pref.getString(SDOViewerConstants.PREFERENCES_MUZEINETWORKMODE, SDOViewerConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE);
        resolution = Integer.parseInt(pref.getString(SDOViewerConstants.PREFERENCES_RESOLUTION, "2048"));
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        Log.i(SDOViewerConstants.LOGTAG, String.format("Network mode: %s", networkMode));
        switch (networkMode) {
            case SDOViewerConstants.PREFERENCES_MUZEINETWORKMODE_WIFI:
                if (!Util.isWifiConnected(this)) {
                    Log.i(SDOViewerConstants.LOGTAG, "No WIFI, skipping publish");
                    scheduleNextUpdate();
                    return;
                }
                break;
            case SDOViewerConstants.PREFERENCES_MUZEINETWORKMODE_WIFI_MOBILE:
                if (Util.isMobileConnected(this) && Util.isRoaming(this)) {
                    Log.i(SDOViewerConstants.LOGTAG, "Roaming is active, skipping publish");
                    scheduleNextUpdate();
                    return;
                }
                break;
        }

        List<SDOImageType> lTypes = new ArrayList<>(Arrays.asList(SDOImageType.AIA_193, SDOImageType.AIA_304, SDOImageType.AIA_171, SDOImageType.AIA_211, SDOImageType.AIA_131, SDOImageType.AIA_335, SDOImageType.AIA_094));
        if (getCurrentArtwork() != null && getCurrentArtwork().getToken() != null) {
            try {
                //remove the last loaded type so the image changes type every time
                SDOImageType currentType = SDOImageType.valueOf(getCurrentArtwork().getToken());
                lTypes.remove(currentType);
            } catch (Exception ex) {
            }
        }
        Collections.shuffle(lTypes);
        SDOImageType type = lTypes.get(0);

        String url = Util.getLatestURL(type, resolution, false);
        Date lastModified = null;
        try {
            lastModified = Util.getLastModified(url);
        } catch (IOException ex) {
            Log.e(SDOViewerConstants.LOGTAG, "Error retrieving last modified header", ex);
            throw new RetryException();
        }
        //muzei caches images by uri, if we use the same uri (like it happens for the "latest" images, muzei never updates
        //for this reason we add a parameter in the url containing the last modified date
        //this way we force muzei to update but we force it only when a different image is effectively online
        Uri uri = Uri.parse(String.format("%s?Last-Modified=%s", url, sdf.format(lastModified)));
        String token = type.name();
        Log.i(SDOViewerConstants.LOGTAG, String.format("Publish artwork token[%s] url[%s]", token, uri.toString()));
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
