package eu.flatworld.android.sdoviewer;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;


public class DetailViewActivity extends ActionBarActivity {
    ImageView mImageView;
    PhotoViewAttacher mAttacher;
    ProgressDialog progressDialog;

    Callback imageLoadedCallback = new Callback() {

        @Override
        public void onSuccess() {
            if(mAttacher!=null){
                mAttacher.update();
            }else{
                mAttacher = new PhotoViewAttacher(mImageView);
                mAttacher.setMaximumScale(2f);
                mAttacher.setMediumScale(1f);
            }
        }

        @Override
        public void onError() {
        }
    };

    void setFitWallpaper(Bitmap source) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            int screenWidth = wallpaperManager.getDesiredMinimumWidth();
            int screenHeight = wallpaperManager.getDesiredMinimumHeight();
            Bitmap target;
            float scale = screenHeight * 1f / source.getHeight();
            target = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * scale), (int)(source.getHeight() * scale), true);
            wallpaperManager.setBitmap(target);
            DetailViewActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
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

    Target targetFit = new Target() {
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            System.err.println("fail");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            System.err.println("prepare");
        }

        @Override
        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setFitWallpaper(bitmap);
                }
            }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.update();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting wallpaper...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageView = (ImageView) findViewById(R.id.imageView);
        SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
        Picasso.with(this).load(Util.getURL(img, 2048)).into(mImageView, imageLoadedCallback);
        getSupportActionBar().setTitle(img.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setwp_fit) {
            progressDialog.show();
            SDOImage img = (SDOImage) getIntent().getExtras().getSerializable("IMAGE");
            Picasso.with(this).load(Util.getURL(img, 2048)).into(targetFit);
            return true;
        }
        return super.onOptionsItemSelected(item);


    }
}
