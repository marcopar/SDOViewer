package eu.flatworld.android.sdoviewer;

/**
 * Created by marcopar on 22/02/15.
 */
public enum SDOImageType {
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

    public String getShortCode() {
        switch (this) {

            case AIA_193:
                return "0193";
            case AIA_304:
                return "0304";
            case AIA_171:
                return "0171";
            case AIA_211:
                return "0211";
            case AIA_131:
                return "0131";
            case AIA_335:
                return "0335";
            case AIA_094:
                return "0094";
            case AIA_1600:
                return "1600";
            case AIA_1700:
                return "1700";
            case AIA_211_193_171:
                return "211_193_171";
            case AIA_304_211_171:
                return "304_211_171";
            case AIA_094_335_193:
                return "094_335_193";
            case AIA_171_HMIB:
                return "171_HMIB";
            case HMI_Magnetogram:
                return "HMIB";
            case HMI_Colorized_Magnetogram:
                return "HMIBC";
            case HMI_Intensitygram_Colored:
                return "HMIIC";
            case HMI_Intensitygram_Flattened:
                return "HMIIF";
            case HMI_Intensitygram:
                return "HMII";
            case HMI_Dopplergram:
                return "HMID";
        }
        return "UNK";
    }
}
