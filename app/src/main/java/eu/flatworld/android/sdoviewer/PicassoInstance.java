package eu.flatworld.android.sdoviewer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

/**
 * Created by marcopar on 06/02/17.
 */

public class PicassoInstance {
    private static Picasso picasso = null;

    public static synchronized Picasso getPicasso(Context ctx) {
        if (picasso == null) {
            Picasso.Builder picassoBuilder = new Picasso.Builder(ctx);
            picasso = picassoBuilder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e(SDOViewerConstants.LOGTAG, "Picasso error", exception);
                    //Util.firebaseLog(getActivity(), "Picasso error", exception);
                }
            }).build();
        }
        return picasso;
    }
}
