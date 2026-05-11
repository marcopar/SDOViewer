package eu.flatworld.android.sdoviewer.gui.solarwind;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcopar on 18/02/17.
 */
public class Flux {

    @SerializedName("flux")
    private int Flux;
    @SerializedName("time_tag")
    private String TimeStamp;

    public Flux() {
    }

    public int getFlux() {
        return this.Flux;
    }

    public void setFlux(int flux) {
        this.Flux = flux;
    }

    public String getTimeStamp() {
        return this.TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }
}
