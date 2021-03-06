package eu.flatworld.android.sdoviewer.io;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.data.Util;

/**
 * Created by marcopar on 06/02/17.
 */

public class PicassoInstance {
    private static Picasso picasso = null;

    public static synchronized Picasso getPicasso(Context ctx) {
        boolean httpCompat = Util.getHttpsSafeModeEnabled(ctx);
        if (picasso == null) {
            Picasso.Builder picassoBuilder = new Picasso.Builder(ctx);
            picasso = picassoBuilder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e(GlobalConstants.LOGTAG, "Picasso error", exception);
                    //SDOUtil.firebaseLog(getActivity(), "Picasso error", exception);
                }
            }).downloader(new OkHttp3Downloader(OkHttpClientFactory.getNewOkHttpClient(httpCompat))).build();
        }
        return picasso;
    }

    public static void reset() {
        if (picasso != null) {
            picasso.shutdown();
            picasso = null;
        }
    }


}
