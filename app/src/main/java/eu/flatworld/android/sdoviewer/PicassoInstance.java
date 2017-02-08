package eu.flatworld.android.sdoviewer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Created by marcopar on 06/02/17.
 */

public class PicassoInstance {
    private static Picasso picasso = null;

    public static synchronized Picasso getPicasso(Context ctx) {
        if (picasso == null) {
            boolean enableHttpCompat = Util.getHttpModeEnabled(ctx);
            Picasso.Builder picassoBuilder = new Picasso.Builder(ctx);
            picasso = picassoBuilder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e(SDOViewerConstants.LOGTAG, "Picasso error", exception);
                    //Util.firebaseLog(getActivity(), "Picasso error", exception);
                }
            }).downloader(new OkHttp3Downloader(getNewHttpClient(enableHttpCompat))).build();
        }
        return picasso;
    }

    public static void reset() {
        picasso.shutdown();
        picasso = null;
    }

    private static OkHttpClient getNewHttpClient(boolean enableHttpCompat) {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);
        if (enableHttpCompat) {
            client = enableTls12OnPreLollipop(client);
        }
        return client.build();
    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        Log.i(SDOViewerConstants.LOGTAG, "Enabling HTTPS compatibility mode");
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, null);
            client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), trustManager);

            ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build();

            List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(cs);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);

            client.connectionSpecs(specs);
        } catch (Exception exc) {
            Log.e(SDOViewerConstants.LOGTAG, "Error while setting TLS 1.2", exc);
        }

        return client;
    }
}
