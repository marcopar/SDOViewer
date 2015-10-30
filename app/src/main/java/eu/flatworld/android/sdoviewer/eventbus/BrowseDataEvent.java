package eu.flatworld.android.sdoviewer.eventbus;

import android.os.Bundle;

/**
 * Created by marcopar on 30/10/15.
 */
public class BrowseDataEvent {
    Bundle bundle;

    public BrowseDataEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
