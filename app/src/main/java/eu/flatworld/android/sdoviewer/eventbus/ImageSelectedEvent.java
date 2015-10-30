package eu.flatworld.android.sdoviewer.eventbus;

import android.os.Bundle;

/**
 * Created by marcopar on 30/10/15.
 */
public class ImageSelectedEvent {
    Bundle bundle;

    public ImageSelectedEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
