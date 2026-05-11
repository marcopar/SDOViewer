package eu.flatworld.android.sdoviewer.gui.solarwind;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcopar on 18/02/17.
 */
public class Speed {

    @SerializedName("proton_speed")
    private int WindSpeed;
    @SerializedName("time_tag")
    private String TimeStamp;

    public Speed() {
    }

    public int getWindSpeed() {
        return this.WindSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.WindSpeed = windSpeed;
    }

    public String getTimeStamp() {
        return this.TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }
}
