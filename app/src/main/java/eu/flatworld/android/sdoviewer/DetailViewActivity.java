package eu.flatworld.android.sdoviewer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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


public class DetailViewActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    ImageView mImageView;
    PhotoViewAttacher mAttacher;
    Callback imageLoadedCallback = new Callback() {

        @Override
        public void onSuccess() {
            swipeLayout.setRefreshing(false);
            if(mAttacher!=null){
                mAttacher.update();
            }else{
                mAttacher = new PhotoViewAttacher(mImageView);
            }
        }

        @Override
        public void onError() {
            swipeLayout.setRefreshing(false);
        }
    };
    ProgressDialog progressDialog;
    Target targetFit = new Target() {
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
    private SwipeRefreshLayout swipeLayout;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        mImageView = (ImageView) findViewById(R.id.imageView);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int res = Integer.parseInt(pref.getString("resolution", "2048"));
        if (res > 2048) {
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setMaximumScale(10);
        mAttacher.update();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting wallpaper...");

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int res = Integer.parseInt(pref.getString("resolution", "2048"));
        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        Picasso.with(this).invalidate(Util.getURL(img, res));
        Picasso.with(this).load(Util.getURL(img, res)).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView, imageLoadedCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageView = (ImageView) findViewById(R.id.imageView);
        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int res = Integer.parseInt(pref.getString("resolution", "2048"));
        Picasso.with(this).load(Util.getURL(img, res)).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView, imageLoadedCallback);
        getSupportActionBar().setTitle(img.toString());
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
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(DetailViewActivity.this);
                            int res = Integer.parseInt(pref.getString("resolution", "2048"));
                            Picasso.with(DetailViewActivity.this).load(Util.getURL(img, res)).into(targetFit);
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
        return super.onOptionsItemSelected(item);


    }
}
