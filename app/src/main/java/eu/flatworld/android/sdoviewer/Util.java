package eu.flatworld.android.sdoviewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcopar on 22/02/15.
 */
public class Util {

    public static String getURL(SDOImage si, int size) {
        switch (si) {
            case AIA_193:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0193.jpg", size);
            case AIA_304:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0304.jpg", size);
            case AIA_171:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0171.jpg", size);
            case AIA_211:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0211.jpg", size);
            case AIA_131:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0131.jpg", size);
            case AIA_335:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0335.jpg", size);
            case AIA_094:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_0094.jpg", size);
            case AIA_1600:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_1600.jpg", size);
            case AIA_1700:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_1700.jpg", size);
            case AIA_211_193_171:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/f_211_193_171_%d.jpg", size);
            case AIA_304_211_171:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/f_304_211_171_%d.jpg", size);
            case AIA_094_335_193:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/f_094_335_193_%d.jpg", size);
            case AIA_171_HMIB:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/f_HMImag_171_%d.jpg", size);
            case HMI_Magnetogram:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMIB.jpg", size);
            case HMI_Colorized_Magnetogram:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMIBC.jpg", size);
            case HMI_Intensitygram_Colored:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMIIC.jpg", size);
            case HMI_Intensitygram_Flattened:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMIIF.jpg", size);
            case HMI_Intensitygram:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMII.jpg", size);
            case HMI_Dopplergram:
                return String.format("http://sdo.gsfc.nasa.gov/assets/img/latest/latest_%d_HMID.jpg", size);
        }
        return null;
    }
}
