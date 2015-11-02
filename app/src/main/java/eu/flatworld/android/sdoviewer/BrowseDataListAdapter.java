package eu.flatworld.android.sdoviewer;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by marcopar on 31/05/15.
 */
public class BrowseDataListAdapter extends ArrayAdapter<BrowseDataListItem> {
    public BrowseDataListAdapter(Context context, List<BrowseDataListItem> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
}
