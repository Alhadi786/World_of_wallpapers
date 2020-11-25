package com.wasidnp.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.common.AppConstant;
import com.wasidnp.common.SaveData;
import com.wasidnp.database.DatabaseHandlerDownload;
import com.wasidnp.database.DatabaseHandlerFavorite;
import com.wasidnp.database.DatabaseHandlerFavorite.DatabaseManager;
import com.wasidnp.database.DatabaseHandlerMostViewed;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemRecent;
import com.wasidnp.models.Pojo;
import com.wasidnp.utilities.GDPR;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ActivitySlideImage extends AppCompatActivity implements SensorEventListener {

    int position;
    String[] str_image, str_image_cat_name, str_image_id;
    public DatabaseHandlerFavorite databaseHandlerFavorite;
    public DatabaseHandlerMostViewed databaseHandlerMostVied;
    public DatabaseHandlerDownload databaseHandlerDownload;
    ViewPager viewPager;
    int total_images;
    private SensorManager sensorManager;
    private boolean checkImage = false;
    private long lastUpdate;
    Handler handler;
    private ProgressBar progressBar;
    Runnable runnable;
    RelativeLayout relativeLayout;
    boolean Play_Flag = false;
    private Menu menu;
    private DatabaseManager databaseManager;
    String image_cat_name, image_url;
    DisplayImageOptions options;
    private AdView adView;
    private InterstitialAd interstitialAd;
    FloatingActionButton set_as_wallpaper, share, save;
    Toolbar toolbar;
    Config config;
    ViewGroup rootView;
    Banner banner;
     //GestureDetector gestureDetector;
   // private GestureDetectorCompat gestureDetector;
    int clickCounter=0;
    int showTheButtons=0;
    FloatingActionMenu floatingActionMenu;

    SharedPreferences sharedPreferences;
    String adsStaus;

    private SaveData objs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        StartAppSDK.init( this, String.valueOf(R.string.startapp_id), true);
//        StartAppAd.disableAutoInterstitial ();
//        StartAppAd.disableSplash ();
      //  gestureDetector = new GestureDetectorCompat(this, new SingleTapConfirm());
        setContentView(R.layout.activity_slider_image);
        relativeLayout = findViewById ( R.id.relativeLayout );
        progressBar = findViewById( R.id.progressBar_wallpaper_by_category);
        rootView = (ViewGroup) findViewById(android.R.id.content);
        config=new Config();
        floatingActionMenu=(FloatingActionMenu)findViewById(R.id.menu3);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        adsStaus=sharedPreferences.getString("ads","");
        banner = (com.startapp.android.publish.ads.banner.Banner) findViewById(R.id.startAppBanner_slide_image);


        objs   = new SaveData(ActivitySlideImage.this);

        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){
            banner.setVisibility(View.GONE);
            banner.hideBanner();
        }else{
            banner.setVisibility(View.VISIBLE);
            banner.hideBanner();
        }

        if(adsStaus.equals("disable"))
        {
            banner.hideBanner();
        }





        if (JsonUtils.isNetworkAvailable( this)) {

            new ActivitySlideImage.MyTask().execute( Config.ADMIN_PANEL_URL + "/admob.php?action=action");
        } else {
            Toast.makeText ( this, "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
        }


       // loadAdMobBannerAd ();
        //loadInterstitialAd ();

        str_image = new String[0];
        str_image_cat_name =new String[0];
        str_image_id =new String[0];



        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");

        }
            //showInterstitialAd ( );
        toolbar = findViewById( R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        databaseHandlerFavorite = new DatabaseHandlerFavorite(this);
        databaseHandlerMostVied = new DatabaseHandlerMostViewed (this);
        databaseHandlerDownload = new DatabaseHandlerDownload (this);
        databaseManager = DatabaseManager.INSTANCE;
        databaseManager.init(getApplicationContext());
       // showInterstitialAd ( );



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
        //Firebase LogEvent
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getResources().getString(R.string.analytics_item_id_2));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getResources().getString(R.string.analytics_item_name_2));
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");

        //Logs an app event.
        MyApplication.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        MyApplication.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 5 seconds
        MyApplication.getFirebaseAnalytics().setMinimumSessionDuration(5000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes). Let’s make it 10.
        MyApplication.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

        //setTitle(JsonConfig.CATEGORY_TITLE);


        set_as_wallpaper = findViewById( R.id.fab_set_as_wallpaper);
        share = findViewById( R.id.fab_share);
        save = findViewById( R.id.fab_save);

        set_as_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     if(adsStaus.equals("enable") && config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true")) {
                         new Handler().postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 if (!interstitialAd.isLoaded()) {
                                     //StartAppAd.enableAutoInterstitial();
                                     if (adsStaus.equals("enable")) {
                                     //    StartAppAd.showAd(getApplicationContext());
                                         loadInterstitialAd();
                                      //   Toast.makeText(ActivitySlideImage.this, "Not loaded", Toast.LENGTH_SHORT).show();
                                     }
                                 } else if (interstitialAd.isLoaded() && adsStaus.equals("enable")) {
                                   //  Toast.makeText(ActivitySlideImage.this, "loaded", Toast.LENGTH_SHORT).show();
                                     StartAppAd.disableAutoInterstitial();
                                     StartAppAd.disableSplash();
                                     interstitialAd.show();
                                     loadInterstitialAd();
                                 }
                             }
                         }, 0);
                     }

                position = viewPager.getCurrentItem();
                Intent intent = new Intent(getApplicationContext(), ActivitySetAsWallpaper.class);
                intent.putExtra("WALLPAPER_IMAGE_URL", str_image);
                intent.putExtra("WALLPAPER_IMAGE_CATEGORY", str_image_cat_name);
                intent.putExtra("POSITION_ID", position);

                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewPager.getCurrentItem();
                (new ShareTask(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadAdMobBannerAd ( );

                if(adsStaus.equals("enable") && config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && interstitialAd!=null) {
                    if (interstitialAd.isLoaded() && adsStaus.equals("enable")) {
                        StartAppAd.disableAutoInterstitial();
                        StartAppAd.disableSplash();
                        interstitialAd.show();
                        loadInterstitialAd();
                    } else {
                        loadInterstitialAd();
                        if (adsStaus.equals("enable")) {
                          //  StartAppAd.showAd(getApplicationContext());
                        }
                    }
                }

                image_cat_name = str_image_cat_name[position];
                image_url = str_image[position];

                databaseHandlerDownload.Addtodownload (new ItemRecent (image_cat_name, image_url));

                position = viewPager.getCurrentItem();


                if (str_image[position].endsWith(".png")) {
                    (new SaveImagePNG(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);
                } else {
                    (new SaveImageJPG(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);
                }

            }
        });
        Intent i = getIntent();
        position = i.getIntExtra("POSITION_ID", 0);
        str_image = i.getStringArrayExtra("IMAGE_ARRAY");
        str_image_cat_name = i.getStringArrayExtra("IMAGE_CATNAME");
        str_image_id = i.getStringArrayExtra("ITEMID");


        total_images = str_image.length - 1;
        viewPager = findViewById( R.id.image_slider);
        handler = new Handler();

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                loadInterstitialAd();
                if(position%100==0) {
                    if(adsStaus.equals("enable") && config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && interstitialAd!=null) {
                        if (interstitialAd.isLoaded() && adsStaus.equals("enable")) {
                            StartAppAd.disableAutoInterstitial();
                            StartAppAd.disableSplash();
                            interstitialAd.show();
                           loadInterstitialAd();
                        } else {
                            loadInterstitialAd();
                            if (adsStaus.equals("enable")) {
                          //      StartAppAd.showAd(getApplicationContext());
                            }
                        }
                    }

                }

                position = viewPager.getCurrentItem();
                image_url = str_image[position];
                image_cat_name = str_image_cat_name[position];
                image_url = str_image[position];
                databaseHandlerMostVied.AddtoMostViewed(new Pojo(image_cat_name, image_url));

                if (JsonUtils.isNetworkAvailable( ActivitySlideImage.this)) {
                    new MyTasCountAdded ().execute( Config.ADMIN_PANEL_URL +  "/count_added.php?action=action"+"&"+"image_url="+image_url);
                } else {
                    Toast.makeText ( ActivitySlideImage.this, "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
                   // StartAppAd.showAd ( getApplicationContext () );
                }


                List<Pojo> list = databaseHandlerFavorite.getFavRow(image_url);
                if (list.size() == 0) {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_outline));
                } else {
                    if (list.get(0).getImageurl().equals(image_url)) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_white));
                    }
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_images, menu);
        this.menu = menu;
        //for when 1st item of view pager is favorite mode
        FirstFav();
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_fav:

                position = viewPager.getCurrentItem();

                image_url = str_image[position];

                List<Pojo> list = databaseHandlerFavorite.getFavRow(image_url);
                if (list.size() == 0) {
                    addtoFav(position);
                } else {
                    if (list.get(0).getImageurl().equals(image_url)) {
                        RemoveFav(position);
                    }
                }
                return true;

            case R.id.menu_share:

                position = viewPager.getCurrentItem();
                (new ShareTask(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);

                return true;

            case R.id.menu_save:
                image_cat_name = str_image_cat_name[position];
                image_url = str_image[position];

                databaseHandlerFavorite.AddtoFavorite(new ItemRecent (image_cat_name, image_url));
                databaseHandlerDownload.Addtodownload (new ItemRecent (image_cat_name, image_url));

                position = viewPager.getCurrentItem();
                if (str_image[position].endsWith(".png")) {
                    (new SaveImagePNG(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);
                } else {
                    (new SaveImageJPG(ActivitySlideImage.this)).execute(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position]);
                }

                return true;

            case R.id.menu_set_as_wallpaper:

                position = viewPager.getCurrentItem();
                Intent intent = new Intent(getApplicationContext(), ActivitySetAsWallpaper.class);
                intent.putExtra("WALLPAPER_IMAGE_URL", str_image);
                intent.putExtra("WALLPAPER_IMAGE_CATEGORY", str_image_cat_name);
                intent.putExtra("POSITION_ID", position);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    //add to favorite
    public void addtoFav(int position) {

        image_cat_name = str_image_cat_name[position];
        image_url = str_image[position];

        databaseHandlerFavorite.AddtoFavorite(new ItemRecent ( image_cat_name, image_url));
        Toast.makeText(getApplicationContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
        menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_white));

    }

    //remove from favorite
    public void RemoveFav(int position) {
        image_url = str_image[position];
        databaseHandlerFavorite.RemoveFav(new Pojo(image_url));
        Toast.makeText(getApplicationContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();
        menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_outline));

    }

    public void FirstFav() {
        int first = viewPager.getCurrentItem();
        String Image_id = str_image[first];

        List<Pojo> pojolist = databaseHandlerFavorite.getFavRow(Image_id);
        if (pojolist.size() == 0) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_outline));

        } else {
            if (pojolist.get(0).getImageurl().equals(Image_id)) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_white));

            }

        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {

            inflater = getLayoutInflater();
        }
        @Override
        public int getCount() {
            return str_image.length;

        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if (Config.ENABLE_CENTER_CROP_IN_DETAIL_WALLPAPER) {

                View imageLayout = inflater.inflate(R.layout.view_pager_item_crop, container, false);
                assert imageLayout != null;
                final ImageView imageView = imageLayout.findViewById( R.id.image);

             //   imageView.setOnTouchListener(new ImageMatrixTouchHandler(ActivitySlideImage.this));

                final ProgressBar spinner = imageLayout.findViewById( R.id.loading);

                Picasso.with(ActivitySlideImage.this)
                        .load(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position])
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                spinner.setVisibility(View.GONE);
                            }
                        });

                container.addView(imageLayout, 0);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                     //   if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

                            clickCounter++;
                            Log.e("abc", "====clickCounter====" + clickCounter);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("abc", "===run==clickCounter====" + clickCounter);

//                               //     if (objs.get(AppConstant.IS_SUBSCRIBED).equals("1")) {
//
//                                    } else {
//
//                                    }

                                    if (clickCounter == 1) {
                                        if (showTheButtons == 0) {
                                            View decorView = getWindow().getDecorView();
                                            decorView.setSystemUiVisibility(
                                                    View.SYSTEM_UI_FLAG_IMMERSIVE
                                                            // Set the content to appear under the system bars so that the
                                                            // content doesn't resize when the system bars hide and show.
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                            // Hide the nav bar and status bar
                                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
                                            toolbar.setVisibility(View.INVISIBLE);
                                            floatingActionMenu.setVisibility(View.INVISIBLE);
                                            showTheButtons = 1;
                                        } else if (showTheButtons == 1) {
                                            View decorView = getWindow().getDecorView();
                                            decorView.setSystemUiVisibility(
                                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                                            toolbar.setVisibility(View.VISIBLE);
                                            floatingActionMenu.setVisibility(View.VISIBLE);
                                            showTheButtons = 0;

                                        }
                                        clickCounter = 0;
                                    } else if (clickCounter == 2) {
                                        imageView.setOnTouchListener(new ImageMatrixTouchHandler(ActivitySlideImage.this));
                                        // Toast.makeText(ActivitySlideImage.this,"double Click",Toast.LENGTH_SHORT).show();


                                        toolbar.setVisibility(View.VISIBLE);
                                        floatingActionMenu.setVisibility(View.VISIBLE);
                                        clickCounter=0;


                                    }
                                    clickCounter=0;

                                }
                            }, 500);

//                        }else {
//                            imageView.setOnTouchListener(new ImageMatrixTouchHandler(ActivitySlideImage.this));
//                            // Toast.makeText(ActivitySlideImage.this,"double Click",Toast.LENGTH_SHORT).show();
//
//
//                            toolbar.setVisibility(View.VISIBLE);
//                            floatingActionMenu.setVisibility(View.VISIBLE);
//                        }
                    }
                });
                return imageLayout;

            } else {
                View imageLayout = inflater.inflate(R.layout.view_pager_item, container, false);
                assert imageLayout != null;
                ImageView imageView = imageLayout.findViewById( R.id.image);
              // imageView.setOnTouchListener(new ImageMatrixTouchHandler(ActivitySlideImage.this));
               imageView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       Log.e("abc", "====22====");
                    clickCounter++;

                   new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(showTheButtons==0) {
                                View decorView = getWindow().getDecorView();
                                decorView.setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                                // Set the content to appear under the system bars so that the
                                                // content doesn't resize when the system bars hide and show.
                                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                // Hide the nav bar and status bar
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
                                toolbar.setVisibility(View.INVISIBLE);
                                floatingActionMenu.setVisibility(View.INVISIBLE);

                                showTheButtons=1;
                            }
                            else if(showTheButtons==1){
                                View decorView = getWindow().getDecorView();
                                decorView.setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                                toolbar.setVisibility(View.VISIBLE);
                                floatingActionMenu.setVisibility(View.VISIBLE);
                                showTheButtons=0;

                            }
                            clickCounter=0;
                        }
                    },500);

                   }
               });

               /* imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageMatrixTouchHandler(ActivitySlideImage.this);

                    }
                });*/


                final ProgressBar spinner = imageLayout.findViewById( R.id.loading);

                Picasso.with(ActivitySlideImage.this)
                        .load(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position].replace(" ", "%20"))
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                spinner.setVisibility(View.GONE);
                            }
                        });

                container.addView(imageLayout, 0);

                return imageLayout;

            }

        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView( (View) object);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            if (checkImage) {

                position = viewPager.getCurrentItem();
                viewPager.setCurrentItem(position);

              } else {

                position = viewPager.getCurrentItem();
                position++;
                if (position == total_images) {
                    position = total_images;
                }
                viewPager.setCurrentItem(position);
            }
            checkImage = !checkImage;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (databaseManager == null) {
            databaseManager = DatabaseManager.INSTANCE;
            databaseManager.init(getApplicationContext());
        } else if (databaseManager.isDatabaseClosed()) {
            databaseManager.init(getApplicationContext());
        }
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        sensorManager.unregisterListener(this);
        if (databaseManager != null) databaseManager.closeDatabase();

    }

    public class SaveImageJPG extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public SaveImageJPG(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.downloading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String as[] = null;
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.saved_folder_name) + "/");
                dir.mkdirs();
                String fileName = "Image_" + "_" + idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                as = new String[1];
                as[0] = file.toString();

                MediaScannerConnection.scanFile(ActivitySlideImage.this, as, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String s1, Uri uri) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

    public class SaveImagePNG extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public SaveImagePNG(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.downloading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String as[] = null;
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.saved_folder_name) + "/");
                dir.mkdirs();
                String fileName = "Image_" + "_" + idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                as = new String[1];
                as[0] = file.toString();

                MediaScannerConnection.scanFile(ActivitySlideImage.this, as, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String s1, Uri uri) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

    public class ShareTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog pDialog;
        String image_url;
        URL myFileUrl;
        String myFileUrl1;
        Bitmap bmImg = null;
        File file;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            try {

                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.saved_folder_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            startActivity(Intent.createChooser(share, "Share Image"));
            pDialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        str_image = new String[0];
//        image_cat_name = new String ( "" );
//        str_image_id = new String[0];
    }

    private void loadInterstitialAd() {

        Log.d("TAG", "showAd");
        MobileAds.initialize(this, config.getAdmob_app_id());
        interstitialAd = new InterstitialAd ( this);
        interstitialAd.setAdUnitId( config.getAdmob_interstitial_unit_id());
        if(config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && config.admob_interstitial_unit_id!=null){
            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
            interstitialAd.loadAd(new AdRequest.Builder().build());

        }
        else {
      //      StartAppAd.enableAutoInterstitial();
        //    StartAppAd.showAd ( getApplicationContext () );
        }

    }


    private void loadAdMobBannerAd() {
        if (config.isENABLE_ADMOB_BANNER_ADS ().equals("true") & config.getAdmob_banner_unit_id () != null) {
            StartAppAd.disableAutoInterstitial();
            StartAppAd.disableSplash();
            String banner_id = config.getAdmob_banner_unit_id ();
            adView = new AdView(getApplicationContext ());
            adView.setAdSize (AdSize.SMART_BANNER);
            adView.setAdUnitId(config.getAdmob_banner_unit_id());
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivitySlideImage.this)).build();


            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM|Gravity.CENTER;
            rootView.addView(adView,params);


            //relativeLayout.addView ( adView,1 );
            adView.loadAd(adRequest);
            adView.isShown ();
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //  Toast.makeText ( getApplicationContext (),"close",Toast.LENGTH_LONG ).show ();
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    //  Toast.makeText ( getApplicationContext (),"load fail",Toast.LENGTH_LONG ).show ();

                    adView.setVisibility(View.GONE);
                    // StartAppAd.showAd(MainActivity.this);
                }

                @Override
                public void onAdLeftApplication() {
                    //  Toast.makeText ( getApplicationContext (),"left app",Toast.LENGTH_LONG ).show ();

                }

                @Override
                public void onAdOpened() {
                    // Toast.makeText ( getApplicationContext (),"open",Toast.LENGTH_LONG ).show ();

                }

                @Override
                public void onAdLoaded() {
                    //  Toast.makeText ( getApplicationContext (),"load",Toast.LENGTH_LONG ).show ();

                    adView.setVisibility(View.VISIBLE);
                }
            });
            Log.d("MainActivity_banner", "AdMob Banner is Enabled");

        } else {
           //  StartAppAd.showAd ( getApplicationContext ());

            Log.d("MainActivity", "AdMob Banner is Disabled");
        }
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    class MyTasCountAdded extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext (), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {
                try {
                    JSONObject mainJson = new JSONObject( result);
                    Log.d ( String.valueOf ( mainJson ), "counts added " );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }
    public class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext (), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {
                try {
                    JSONObject mainJson = new JSONObject( result);
                    JSONObject objJson = mainJson.getJSONObject ( JsonConfig.admob_app_data );
                    String id = objJson.getString ( "id" );
                    String admob_app_id = objJson.getString ( "admob_app_id" );
                    String admob_banner_unit_id = objJson.getString ( "admob_banner_unit_id" );
                    Log.d("jsonadmob_banne",admob_banner_unit_id);
                    String admob_interstitial_unit_id = objJson.getString ( "admob_interstitial_unit_id" );
                    String admob_reward_video_unit_id = objJson.getString ( "admob_reward_video_unit_id" );
                    String ENABLE_ADMOB_BANNER_ADS = objJson.getString ( "ENABLE_ADMOB_BANNER_ADS" );
                    String ENABLE_ADMOB_VIDEO_ADS = objJson.getString ( "ENABLE_ADMOB_VIDEO_ADS" );
                    String ENABLE_ADMOB_INTERSTITIAL_ADS = objJson.getString ( "ENABLE_ADMOB_INTERSTITIAL_ADS" );

                    config.setAdmob_app_id ( admob_app_id );
                    config.setAdmob_banner_unit_id (admob_banner_unit_id);
                    config.setAdmob_interstitial_unit_id (admob_interstitial_unit_id);
                    config.setAdmob_reward_video_unit_id ( admob_reward_video_unit_id);
                    config.setEnableAdmobBannerAds ( ENABLE_ADMOB_BANNER_ADS);
                    config.setEnableAdmobInterstitialAds ( ENABLE_ADMOB_INTERSTITIAL_ADS);
                    config.setEnableAdmobVideoAds ( ENABLE_ADMOB_VIDEO_ADS);

                    Log.d("jsonadmob_banner_id",config.getAdmob_app_id());
                    Log.d("jsonadmob_banner_id",config.getAdmob_banner_unit_id());
                    Log.d("jsonadmob_banner_id",config.getAdmob_interstitial_unit_id());
                    Log.d("jsonadmob_banner_id",config.getAdmob_reward_video_unit_id());
                    Log.d("jsonadmob_banner_id",config.isENABLE_ADMOB_BANNER_ADS());
                    Log.d("jsonadmob_banner_id",config.isENABLE_ADMOB_INTERSTITIAL_ADS());
                    Log.d("jsonadmob_banner_id",config.isENABLE_ADMOB_VIDEO_ADS());

// ads_config


                    if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")) {

                    }else {
                        if (config.getAdmob_app_id() != null) {
                            // GDPR.updateConsentStatus ( MainActivity.this );
                            //Toast.makeText ( getApplicationContext (),"app id",Toast.LENGTH_LONG ).show ();

                        } else {
                            //Toast.makeText ( getApplicationContext (),"no app id",Toast.LENGTH_LONG ).show ();
                        }
                        if (config.isENABLE_ADMOB_INTERSTITIAL_ADS().equals("true")) {

                            loadInterstitialAd();
                            // showIntersitialAdEvery2Minutes();


                        } else if (adsStaus.equals("enable")) {
                            //  Toast.makeText ( ActivitySlideImage.this, "ads disable for back end ", Toast.LENGTH_SHORT ).show ( );
                  //          StartAppAd.showAd(getApplicationContext());
                        }

                        if (config.ENABLE_ADMOB_BANNER_ADS.equals("true") && adsStaus.equals("enable")) {
                            loadAdMobBannerAd();
                        } else {
                            //   Toast.makeText(ActivitySlideImage.this,"Banner ad is disabled from backend",Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }


        }

    }

    class MyTaskFcm extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext (), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {
                try {
                    JSONObject mainJson = new JSONObject( result);
                    Log.d ( String.valueOf ( mainJson ), "data added " );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            /*it needs to return true if we don't want
            to ignore rest of the gestures*/
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return true;
        }
    }

}
