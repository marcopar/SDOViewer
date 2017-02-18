package eu.flatworld.android.sdoviewer.gui.solarwind;

/**
 * Created by marcopar on 18/02/17.
 */
public class MagneticField {

    private int Bt;
    private int Bz;
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
