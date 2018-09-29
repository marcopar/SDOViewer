package eu.flatworld.android.sdoviewer.gui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import eu.flatworld.android.sdoviewer.GlobalConstants;
import eu.flatworld.android.sdoviewer.MainActivity;
import eu.flatworld.android.sdoviewer.R;
import eu.flatworld.android.sdoviewer.data.SDO;
import eu.flatworld.android.sdoviewer.io.PicassoInstance;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDetailFragment extends Fragment {
    private ImageViewTouch mImageView;
    private ProgressDialog progressDialog;
    Target targetSetWallpaper = new Target() {
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setWallpaper(bitmap);
                }
            }).start();
        }
    };
    Target targetShare = new Target() {
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    share(bitmap);
                }
            }).start();
        }
    };
    private boolean pfssVisible = false;

    public ImageDetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        if (savedInstanceState != null) {
            pfssVisible = savedInstanceState.getBoolean("pfssVisible");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        int resolution = Integer.parseInt(pref.getString(GlobalConstants.PREFERENCES_RESOLUTION, "2048"));
        mImageView = (ImageViewTouch) view.findViewById(R.id.imageView);
        if (resolution > 2048) {
            //if bigger than 2048 performances are very bad with hardware acceleration so we disable it
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        SDO imageType = (SDO) getArguments().getSerializable("imageType");
        ((MainActivity) activity).getSupportActionBar().setTitle(imageType.toString());
        ((MainActivity) activity).getSupportActionBar().setSubtitle(null);

        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "image_detail");
        b.putString(FirebaseAnalytics.Param.ITEM_ID, imageType.name());
        ((MainActivity) activity).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, b);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_detail_view, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        String description = getArguments().getString("description");
        if (description == null) {
            MenuItem item = menu.findItem(R.id.action_about_this_image);
            item.setVisible(false);
        }
        String pfssUrl = (String) getArguments().getSerializable("pfssUrl");
        MenuItem item = menu.findItem(R.id.action_pfss);
        item.setVisible(pfssUrl != null);
        item.setChecked(pfssVisible);
        if (pfssVisible) {
            item.setIcon(R.drawable.ic_action_pfss_on);
        } else {
            item.setIcon(R.drawable.ic_action_pfss_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final SDO imageType = (SDO) getArguments().getSerializable("imageType");
        final String pfssUrl = (String) getArguments().getSerializable("pfssUrl");
        final String imageUrl = (String) getArguments().getSerializable("imageUrl");
        final String description = (String) getArguments().getSerializable("description");
        final Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        int id = item.getItemId();
        if (id == R.id.action_set_wallpaper) {
            Bundle b = new Bundle();
            b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "set_wallpaper");
            b.putString(FirebaseAnalytics.Param.ITEM_ID, imageType.name());
            ((MainActivity) activity).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SHARE, b);
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.set_wallpaper)
                    .setMessage(getString(R.string.do_you_want_to_set))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressDialog.setMessage(getString(R.string.setting_wallpaper_));
                            progressDialog.show();
                            if (pfssVisible) {
                                PicassoInstance.getPicasso(activity.getBaseContext()).load(pfssUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(targetSetWallpaper);
                            } else {
                                PicassoInstance.getPicasso(activity.getBaseContext()).load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(targetSetWallpaper);
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, null).show();
            return true;
        }
        if (id == R.id.action_about_this_image) {
            Bundle b = new Bundle();
            b.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "about_image");
            b.putString(FirebaseAnalytics.Param.ITEM_ID, imageType.name());
            ((MainActivity) activity).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, b);
            new AlertDialog.Builder(activity)
                    .setTitle(imageType.toString())
                    .setMessage(Html.fromHtml(description))
                    .setCancelable(true)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
            return true;
        }
        if (id == R.id.action_reload) {
            loadImage(true);
            return true;
        }
        if (id == R.id.action_share) {
            Bundle b = new Bundle();
            b.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share");
            b.putString(FirebaseAnalytics.Param.ITEM_ID, imageType.name());
            ((MainActivity) activity).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SHARE, b);
            progressDialog.setMessage(getString(R.string.preparing_the_image_));
            progressDialog.show();
            if (pfssVisible) {
                Picasso.with(activity).load(pfssUrl).into(targetShare);
            } else {
                Picasso.with(activity).load(imageUrl).into(targetShare);
            }
            return true;
        }
        if (id == R.id.action_pfss) {
            Bundle b = new Bundle();
            b.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "pfss");
            b.putString(FirebaseAnalytics.Param.ITEM_ID, imageType.name());
            ((MainActivity) activity).getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, b);
            pfssVisible = !pfssVisible;
            item.setChecked(pfssVisible);
            if (pfssVisible) {
                item.setIcon(R.drawable.ic_action_pfss_on);
            } else {
                item.setIcon(R.drawable.ic_action_pfss_off);
            }
            loadImage(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void share(Bitmap b) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        try {
            File cachePath = new File(activity.getCacheDir(), "images");
            if (cachePath.exists()) {
                for (File f : cachePath.listFiles()) {
                    f.delete();
                }
                cachePath.delete();
            }
            cachePath.mkdirs();
            String fileName = cachePath + "/image" + System.currentTimeMillis() + ".png";
            FileOutputStream stream = new FileOutputStream(fileName);
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File newFile = new File(fileName);
            Uri contentUri = FileProvider.getUriForFile(activity, "eu.flatworld.android.sdoviewer.fileprovider", newFile);
            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_the_image_using_)));
            }
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, getString(R.string.error_sharing_the_image_) + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }
    }

    void setWallpaper(Bitmap source) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
            int screenWidth = wallpaperManager.getDesiredMinimumWidth();
            int screenHeight = wallpaperManager.getDesiredMinimumHeight();
            Bitmap target;
            float scale = screenHeight * 1f / source.getHeight();
            target = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * scale), (int) (source.getHeight() * scale), true);
            wallpaperManager.setBitmap(target);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, getString(R.string.wallpaper_is_set), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, getString(R.string.error_setting_the_wallpaper_) + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }
    }

    void loadImage(boolean invalidateCache) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mImageView.setImageResource(R.drawable.ic_sun);
        final SDO imageType = (SDO) getArguments().getSerializable("imageType");
        final String pfssUrl = (String) getArguments().getSerializable("pfssUrl");
        final String imageUrl = (String) getArguments().getSerializable("imageUrl");
        if (invalidateCache) {
            Picasso.with(activity).invalidate(imageUrl);
            if (pfssUrl != null) {
                Picasso.with(activity).invalidate(pfssUrl);
            }
        }
        if (pfssVisible) {
            PicassoInstance.getPicasso(activity.getBaseContext()).load(pfssUrl).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView);
        } else {
            PicassoInstance.getPicasso(activity.getBaseContext()).load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImage(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pfssVisible", pfssVisible);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
