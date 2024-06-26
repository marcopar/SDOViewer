package eu.flatworld.android.sdoviewer.data;

/**
 * Created by marcopar on 22/02/15.
 */
public enum SolarWind {
    SPEED,
    MAGNETIC_FIELD,
    FLUX;

    public static final String BASE_URL = "https://services.swpc.noaa.gov/";
    public static final String URL_SOLARWIND_SPEED = BASE_URL + "products/summary/solar-wind-speed.json";
    public static final String URL_SOLARWIND_MAGNETICFIELD = BASE_URL + "products/summary/solar-wind-mag-field.json";
    public static final String URL_SOLARWIND_FLUX = BASE_URL + "products/summary/10cm-flux.json";

    public static String getDescription(SolarWind si) {
        switch (si) {
            case SPEED:
                return "";
            case MAGNETIC_FIELD:
                return "";
            case FLUX:
                return "";
        }
        return null;
    }

    public static String getLatestURL(SolarWind sw) {
        switch (sw) {
            case SPEED:
                return URL_SOLARWIND_SPEED;
            case MAGNETIC_FIELD:
                return URL_SOLARWIND_MAGNETICFIELD;
            case FLUX:
                return URL_SOLARWIND_FLUX;
        }
        return null;
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

}
