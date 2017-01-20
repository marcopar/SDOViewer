package eu.flatworld.android.sdoviewer;


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
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDetailFragment extends Fragment {

    Picasso picasso;
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
        Picasso.Builder picassoBuilder = new Picasso.Builder(getActivity());
        picasso = picassoBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Util.firebaseLog(getActivity(), "Picasso error ImageDetailFragment", exception);
            }
        }).build();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int resolution = Integer.parseInt(pref.getString("resolution", "2048"));
        mImageView = (ImageViewTouch) view.findViewById(R.id.imageView);
        if (resolution > 2048) {
            //if bigger than 2048 performances are very bad with hardware acceleration so we disable it
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        SDOImageType imageType = (SDOImageType) getArguments().getSerializable("imageType");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(imageType.toString());
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(null);
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
        final SDOImageType imageType = (SDOImageType) getArguments().getSerializable("imageType");
        final String pfssUrl = (String) getArguments().getSerializable("pfssUrl");
        final String imageUrl = (String) getArguments().getSerializable("imageUrl");
        final String description = (String) getArguments().getSerializable("description");
        int id = item.getItemId();
        if (id == R.id.action_set_wallpaper) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.set_wallpaper)
                    .setMessage(getString(R.string.do_you_want_to_set))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            progressDialog.setMessage(getString(R.string.setting_wallpaper_));
                            progressDialog.show();
                            if (pfssVisible) {
                                picasso.load(pfssUrl).into(targetSetWallpaper);
                            } else {
                                picasso.load(imageUrl).into(targetSetWallpaper);
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, null).show();
            return true;
        }
        if (id == R.id.action_about_this_image) {
            new AlertDialog.Builder(getActivity())
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
            progressDialog.setMessage(getString(R.string.preparing_the_image_));
            progressDialog.show();
            if (pfssVisible) {
                Picasso.with(getActivity()).load(pfssUrl).into(targetShare);
            } else {
                Picasso.with(getActivity()).load(imageUrl).into(targetShare);
            }
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
        mImageView.setImageResource(R.drawable.ic_sun);
        final SDOImageType imageType = (SDOImageType) getArguments().getSerializable("imageType");
        final String pfssUrl = (String) getArguments().getSerializable("pfssUrl");
        final String imageUrl = (String) getArguments().getSerializable("imageUrl");
        if (invalidateCache) {
            Picasso.with(getActivity()).invalidate(imageUrl);
            if (pfssUrl != null) {
                Picasso.with(getActivity()).invalidate(pfssUrl);
            }
        }
        if (pfssVisible) {
            picasso.load(pfssUrl).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView);
        } else {
            picasso.load(imageUrl).placeholder(R.drawable.ic_sun).error(R.drawable.ic_broken_sun).into(mImageView);
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
