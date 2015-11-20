package eu.flatworld.android.sdoviewer;

/**
 * Created by marcopar on 04/11/15.
 */
public class Globals {
    private static Globals instance;
    boolean twoPanesModeEnabled = false;

    public static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    public boolean isTwoPanesModeEnabled() {
        return twoPanesModeEnabled;
    }

    public void setTwoPanesModeEnabled(boolean twoPanesModeEnabled) {
        this.twoPanesModeEnabled = twoPanesModeEnabled;
    }
}
