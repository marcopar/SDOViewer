package eu.flatworld.android.sdoviewer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import uk.co.senab.photoview.PhotoViewAttacher;


public class DetailViewActivity extends ActionBarActivity {
    private static final int WALLPAPER_RESOLUTION = 2048;
    Callback imageLoadedCallback = new Callback() {

        @Override
        public void onSuccess() {
            mAttacher.update();
        }

        @Override
        public void onError() {

        }
    };
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
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private ProgressDialog progressDialog;
    private int resolution;
    private boolean pfssAvailable = false;
    private boolean pfssVisible = false;

    void setFitWallpaper(Bitmap source) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            int screenWidth = wallpaperManager.getDesiredMinimumWidth();
            int screenHeight = wallpaperManager.getDesiredMinimumHeight();
            Bitmap target;
            float scale = screenHeight * 1f / source.getHeight();
            target = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * scale), (int) (source.getHeight() * scale), true);
            wallpaperManager.setBitmap(target);
            DetailViewActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(DetailViewActivity.this, "Wallpaper is set", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception ex) {
            DetailViewActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(DetailViewActivity.this, "Error setting the wallpaper: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for transparent actionbar
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        mImageView = (ImageView) findViewById(R.id.imageView);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting wallpaper...");

        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        if (Util.getURL(img, resolution, true) != null) {
            pfssAvailable = true;
        }
        getSupportActionBar().setTitle(img.toString());
        //for transparent actionbar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
    }

    void loadImage(boolean invalidateCache) {
        //if we do not do this hack, when picasso sets the placeholder, photoview
        //is not updated and shows the placeholder with the wrong size
        mImageView.setImageResource(R.drawable.ic_sun);
        mAttacher.update();
        //
        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        if (invalidateCache) {
            Picasso.with(this).invalidate(Util.getURL(img, resolution, false));
            if (pfssAvailable) {
                Picasso.with(this).invalidate(Util.getURL(img, resolution, true));
            }
        }
        Picasso.with(this).load(Util.getURL(img, resolution, pfssAvailable & pfssVisible)).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView, imageLoadedCallback);
    }

    @Override
    protected void onResume() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        if (Util.getDescription(img) == null) {
            MenuItem item = menu.findItem(R.id.action_about_this_image);
            item.setVisible(false);
        }
        MenuItem item = menu.findItem(R.id.action_pfss);
        item.setVisible(pfssAvailable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set_wallpaper) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.set_wallpaper)
                    .setMessage(getString(R.string.do_you_want_to_set))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressDialog.show();
                            SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
                            Picasso.with(DetailViewActivity.this).load(Util.getURL(img, WALLPAPER_RESOLUTION, pfssAvailable & pfssVisible)).into(targetSetWallpaper);
                        }
                    })
                    .setNegativeButton(R.string.no, null).show();
            return true;
        }
        if (id == R.id.action_about_this_image) {
            SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
            new AlertDialog.Builder(this)
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
}
