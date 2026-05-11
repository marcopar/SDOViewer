package eu.flatworld.android.sdoviewer.gui.solarwind;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcopar on 18/02/17.
 */
public class MagneticField {

    @SerializedName("bt")
    private int Bt;
    @SerializedName("bz_gsm")
    private int Bz;
    @SerializedName("time_tag")
    private String TimeStamp;

    public MagneticField() {
    }

    public int getBt() {
        return this.Bt;
    }

    public void setBt(int bt) {
        this.Bt = bt;
    }

    public int getBz() {
        return this.Bz;
    }

    public void setBz(int bz) {
        this.Bz = bz;
    }

    public String getTimeStamp() {
        return this.TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }
}
