package eu.flatworld.android.sdoviewer.muzei;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.apps.muzei.api.provider.Artwork;
import com.google.android.apps.muzei.api.provider.ProviderContract;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import eu.flatworld.android.sdoviewer.BuildConfig;
import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.data.SDO;
import eu.flatworld.android.sdoviewer.data.Util;
import eu.flatworld.android.sdoviewer.io.OkHttpClientFactory;
import okhttp3.OkHttpClient;

public class SDOMuzeiWorker extends Worker {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    int resolution = 2048;
    OkHttpClient httpClient;

    public SDOMuzeiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        resolution = Integer.parseInt(pref.getString(GlobalConstants.PREFERENCES_MUZEIRESOLUTION, "2048"));

        httpClient = OkHttpClientFactory.getNewOkHttpClient(Util.getHttpsSafeModeEnabled(context));

    }

    @NonNull
    @Override
    public Result doWork() {

        List<SDO> lTypes = new ArrayList<>(Arrays.asList(SDO.AIA_193, SDO.AIA_304, SDO.AIA_171, SDO.AIA_211, SDO.AIA_131, SDO.AIA_335, SDO.AIA_094, SDO.AIA_211_193_171, SDO.AIA_304_211_171, SDO.AIA_094_335_193));
        Artwork lastArtwork = ProviderContract.Artwork.getLastAddedArtwork(getApplicationContext(), BuildConfig.APPLICATION_ID);
        if (lastArtwork != null && lastArtwork.getMetadata() != null) {
            try {
                //remove the last loaded type so the image changes type every time
                SDO currentType = SDO.valueOf(lastArtwork.getMetadata());
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
            return Result.retry();
        }
        //muzei caches images by token
        //if we use as token the same uri (like it happens for the "latest" images) muzei uses already cached images
        //for this reason we add a parameter in the url containing the last modified date
        //this way we force muzei to update but we force it only when a different image is effectively online
        Uri uri = Uri.parse(String.format("%s?Last-Modified=%s", url, sdf.format(lastModified)));
        String token = uri.toString();
        String metadata = type.name();
        Log.i(GlobalConstants.LOGTAG, String.format("Publish artwork metadata[%s] token[%s]", metadata, token));

        Artwork artwork = new Artwork.Builder()
                .title(type.toString())
                .attribution(getApplicationContext().getResources().getString(R.string.muzei_byline))
                .token(token)
                .metadata(metadata)
                .persistentUri(uri)
                .build();

        ProviderContract.Artwork.addArtwork(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID,
                artwork);
        return Result.success();
    }
}
