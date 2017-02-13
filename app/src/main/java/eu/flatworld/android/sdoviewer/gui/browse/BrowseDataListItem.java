package eu.flatworld.android.sdoviewer.gui.browse;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataListItem {
    String text;
    String url;

    public BrowseDataListItem() {
    }

    public BrowseDataListItem(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return text;
    }
}
