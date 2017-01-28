package eu.flatworld.android.sdoviewer.muzei;


import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

/**
 * Created by marcopar on 26/01/17.
 */

public class MuzeiSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "Solar Dynamics Observatory";

    private static final int ROTATE_TIME_MILLIS = 3 * 60 * 60 * 1000; // rotate every 3 hours

    public MuzeiSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {

    }
}
