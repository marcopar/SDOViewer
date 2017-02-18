package eu.flatworld.android.sdoviewer.gui.thesunnow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.SDO;
import eu.flatworld.android.sdoviewer.io.PicassoInstance;

/**
 * Created by marcopar on 20/02/15.
 */
public class GridImageAdapter extends BaseAdapter {
    private final List<SDO> mItems;
    private final LayoutInflater mInflater;

    private Context mContext;

    public GridImageAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
        mContext = c;
        mItems = new ArrayList<SDO>();
        for (SDO i : SDO.values()) {
            mItems.add(i);
        }
    }

    public void invalidateCache() {
        for (SDO i : mItems) {
            PicassoInstance.getPicasso(mContext).invalidate(SDO.getLatestURL(i, 512, false));
            if (SDO.getLatestURL(i, 512, true) != null) {
                PicassoInstance.getPicasso(mContext).invalidate(SDO.getLatestURL(i, 512, true));
            }
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public SDO getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        PicassoInstance.getPicasso(mContext).load(SDO.getLatestURL(mItems.get(position), 512, false)).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(picture);
        name.setText(mItems.get(position).toString());

        return v;

    }

}


