package eu.flatworld.android.sdoviewer;

import android.os.Build;
import android.util.Log;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Factory for OkHttpClient, supports the creation of clients enabling TLS on devices where it's not enabled by default (mainly pre lollipop)
 */

public class OkHttpClientFactory {
    /**
     * Creates an OkHttpClient optionally enabling TLS
     */
    public static OkHttpClient getNewOkHttpClient(boolean enableTLS) {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS);
        if (enableTLS) {
            client = enableTls12(client);
        }
        return client.build();
    }

    /**
     * Creates a new OkHttpClient
     */
    public static OkHttpClient getNewOkHttpClient() {
        return getNewOkHttpClient(false);
    }

    /**
     * Creates a new OkHttpClient detecting if TLS needs to be enabled
     */
    public static OkHttpClient getNewOkHttpsSafeClient() {
        return getNewOkHttpClient(isTLSEnableNeeded());
    }

    /**
     * True if enabling TLS is needed on current device (SDK version >= 16 and < 22)
     */
    public static boolean isTLSEnableNeeded() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            return true;
        }
        return false;
    }


    /**
     * Enable TLS on the OKHttp builder by setting a custom SocketFactory
     */
    private static OkHttpClient.Builder enableTls12(OkHttpClient.Builder client) {
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

            client.sslSocketFactory(new TLSSocketFactory(), trustManager);

            ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                    .build();

            List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(cs);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);

            client.connectionSpecs(specs);
        } catch (Exception exc) {
            Log.e(SDOViewerConstants.LOGTAG, "Error while setting TLS", exc);
        }

        return client;
    }
}
