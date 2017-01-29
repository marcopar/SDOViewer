package eu.flatworld.android.sdoviewer.muzei;


import android.content.Intent;
import android.net.Uri;
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

import eu.flatworld.android.sdoviewer.Main;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.SDOImageType;
import eu.flatworld.android.sdoviewer.Util;

/**
 * Created by marcopar on 26/01/17.
 */

public class SDOMuzeiSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "NASA-SDO";
    private static final int ROTATE_TIME_MILLIS = 2 * 60 * 1000;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");


    public SDOMuzeiSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        List<SDOImageType> lTypes = new ArrayList<>(Arrays.asList(SDOImageType.AIA_193, SDOImageType.AIA_304, SDOImageType.AIA_171, SDOImageType.AIA_211, SDOImageType.AIA_131, SDOImageType.AIA_335, SDOImageType.AIA_094));
        if (getCurrentArtwork() != null && getCurrentArtwork().getToken() != null) {
            try {
                SDOImageType currentType = SDOImageType.valueOf(getCurrentArtwork().getToken());
                lTypes.remove(currentType);
            } catch (Exception ex) {
            }
        }
        Collections.shuffle(lTypes);
        SDOImageType type = lTypes.get(0);

        String url = Util.getLatestURL(type, 2048, false);
        Date lastModified = null;
        try {
            lastModified = Util.getLastModified(url);
        } catch (IOException ex) {
            Log.e(Main.LOGTAG, "Error retrieving last modified header", ex);
            throw new RetryException();
        }
        Uri uri = Uri.parse(String.format("%s?Last-Modified=%s", url, sdf.format(lastModified)));
        String token = type.name();
        Log.i(Main.LOGTAG, String.format("Publish artwork token[%s] url[%s]", token, uri.toString()));
        publishArtwork(new Artwork.Builder()
                .title(type.toString())
                .byline(getResources().getString(R.string.muzei_byline))
                .token(token)
                .imageUri(uri)
                .viewIntent(new Intent(Intent.ACTION_VIEW, uri))
                .build());

        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }
}
