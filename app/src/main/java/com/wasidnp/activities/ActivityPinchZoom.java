package com.wasidnp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.utilities.ZoomableImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ActivityPinchZoom extends AppCompatActivity {

    String[] mZoomImages, mZoomCatName;
    int position;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_zoom);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_thumbnail)
                .showImageOnFail(R.drawable.ic_thumbnail)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();


        ZoomableImageView zoom = (ZoomableImageView) findViewById(R.id.imageView);

        Intent i = getIntent();
        mZoomImages = i.getStringArrayExtra("ZOOM_IMAGE_URL");
        mZoomCatName = i.getStringArrayExtra("ZOOM_IMAGE_CATEGORY");
        position = i.getIntExtra("POSITION_ID", 0);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageLoader.getInstance().displayImage(Config.ADMIN_PANEL_URL + "/upload/" + mZoomImages[position], zoom, options, new SimpleImageLoadingListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

}
