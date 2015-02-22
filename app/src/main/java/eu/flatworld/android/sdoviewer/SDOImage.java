package eu.flatworld.android.sdoviewer;

/**
 * Created by marcopar on 22/02/15.
 */
public enum SDOImage {
    AIA_193,
    AIA_304,
    AIA_171,
    AIA_211,
    AIA_131,
    AIA_335,
    AIA_094,
    AIA_1600,
    AIA_1700,
    AIA_211_193_171,
    AIA_304_211_171,
    AIA_094_335_193,
    AIA_171_HMIB,
    HMI_Magnetogram,
    HMI_Colorized_Magnetogram,
    HMI_Intensitygram_Colored,
    HMI_Intensitygram_Flattened,
    HMI_Intensitygram,
    HMI_Dopplergram;

    public String toString() {
        return name().replaceAll("_", " ");
    }
}
