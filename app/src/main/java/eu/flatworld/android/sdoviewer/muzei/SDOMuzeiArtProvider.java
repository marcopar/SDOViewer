package eu.flatworld.android.sdoviewer.muzei;

import com.google.android.apps.muzei.api.provider.MuzeiArtProvider;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SDOMuzeiArtProvider extends MuzeiArtProvider {

    @Override
    protected void onLoadRequested(boolean initial) {
        WorkManager.getInstance().enqueue(
                new OneTimeWorkRequest.Builder(SDOMuzeiWorker.class)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()
        );
    }

}