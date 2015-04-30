package eu.flatworld.android.sdoviewer;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDetailFragment extends Fragment {
    private static final int WALLPAPER_RESOLUTION = 2048;

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    Callback imageLoadedCallback = new Callback() {

        @Override
        public void onSuccess() {
            mAttacher.update();
        }

        @Override
        public void onError() {

        }
    };
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
                    setFitWallpaper(bitmap);
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
    private int resolution;
    private boolean pfssAvailable = false;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //for transparent actionbar
        //((MainActivity)getActivity()).supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        mImageView = (ImageView) view.findViewById(R.id.imageView);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        resolution = Integer.parseInt(pref.getString("resolution", "2048"));
        if (resolution > 2048) {
            //if bigger than 2048 performances are very bad with hardware acceleration so we disable it
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setMaximumScale(10);
        if (savedInstanceState != null) {
            mAttacher.setScale(savedInstanceState.getFloat("scale"), savedInstanceState.getFloat("x"), savedInstanceState.getFloat("y"), false);
            pfssVisible = savedInstanceState.getBoolean("pfss");
        }
        mAttacher.update();
        progressDialog = new ProgressDialog(getActivity());

        SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
        if (Util.getURL(img, resolution, true) != null) {
            pfssAvailable = true;
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(img.toString());
        //for transparent actionbar
        //((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_view, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
        if (Util.getDescription(img) == null) {
            MenuItem item = menu.findItem(R.id.action_about_this_image);
            item.setVisible(false);
        }
        MenuItem item = menu.findItem(R.id.action_pfss);
        item.setVisible(pfssAvailable);
        item.setChecked(pfssVisible);
        if (pfssVisible) {
            item.setIcon(R.drawable.ic_action_pfss_on);
        } else {
            item.setIcon(R.drawable.ic_action_pfss_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set_wallpaper) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.set_wallpaper)
                    .setMessage(getString(R.string.do_you_want_to_set))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressDialog.setMessage(getString(R.string.setting_wallpaper_));
                            progressDialog.show();
                            SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
                            Picasso.with(getActivity()).load(Util.getURL(img, WALLPAPER_RESOLUTION, pfssAvailable & pfssVisible)).into(targetSetWallpaper);
                        }
                    })
                    .setNegativeButton(R.string.no, null).show();
            return true;
        }
        if (id == R.id.action_about_this_image) {
            SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
            new AlertDialog.Builder(getActivity())
                    .setTitle(img.toString())
                    .setMessage(Html.fromHtml(Util.getDescription(img)))
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
            progressDialog.setMessage(getString(R.string.preparing_the_image_));
            progressDialog.show();
            SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
            Picasso.with(getActivity()).load(Util.getURL(img, resolution, pfssAvailable & pfssVisible)).into(targetShare);
            return true;
        }
        if (id == R.id.action_pfss) {
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
        try {
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "x", "x");
            Uri uri = Uri.parse(path);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_the_image_using_)));
        } catch (final Exception ex) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), getString(R.string.error_shareing_the_image_) + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }
    }

    void setFitWallpaper(Bitmap source) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
            int screenWidth = wallpaperManager.getDesiredMinimumWidth();
            int screenHeight = wallpaperManager.getDesiredMinimumHeight();
            Bitmap target;
            float scale = screenHeight * 1f / source.getHeight();
            target = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * scale), (int) (source.getHeight() * scale), true);
            wallpaperManager.setBitmap(target);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), getString(R.string.wallpaper_is_set), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception ex) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), getString(R.string.error_setting_the_wallpaper_) + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }
    }

    void loadImage(boolean invalidateCache) {
        //if we do not do this hack, when picasso sets the placeholder, photoview
        //is not updated and shows the placeholder with the wrong size
        mImageView.setImageResource(R.drawable.ic_sun);
        mAttacher.update();

        SDOImage img = (SDOImage) getArguments().getSerializable("IMAGE");
        if (invalidateCache) {
            Picasso.with(getActivity()).invalidate(Util.getURL(img, resolution, false));
            if (pfssAvailable) {
                Picasso.with(getActivity()).invalidate(Util.getURL(img, resolution, true));
            }
        }
        Picasso.with(getActivity()).load(Util.getURL(img, resolution, pfssAvailable & pfssVisible)).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView, imageLoadedCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImage(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        float scale = Math.max(mAttacher.getMinimumScale(), Math.min(mAttacher.getScale(), mAttacher.getMaximumScale()));
        RectF original = new RectF();
        original.top = mImageView.getTop();
        original.bottom = mImageView.getBottom();
        original.left = mImageView.getLeft();
        original.right = mImageView.getRight();
        RectF relative = mAttacher.getDisplayRect();

        float focalX = (0.5f * (original.left + original.right) - relative.left) / scale;
        float focalY = (0.5f * (original.top + original.bottom) - relative.top) / scale;
        outState.putFloat("scale", scale);
        outState.putFloat("x", focalX);
        outState.putFloat("y", focalY);
        outState.putBoolean("pfss", pfssVisible);
    }

}
