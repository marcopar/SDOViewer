package eu.flatworld.android.sdoviewer.gui;

/**
 * Created by marcopar on 18/02/17.
 */
public class Speed {

    private int WindSpeed;
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
