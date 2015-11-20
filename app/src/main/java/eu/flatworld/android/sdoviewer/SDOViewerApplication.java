package eu.flatworld.android.sdoviewer;

import android.app.Application;

/**
 * Created by marcopar on 04/11/15.
 */
public class SDOViewerApplication extends Application {
    Globals globals;

    @Override
    public void onCreate() {
        super.onCreate();

        globals = Globals.getInstance();
    }

    public Globals getGlobals() {
        return globals;
    }
}
