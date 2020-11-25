package com.wasidnp.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.utilities.GDPR;
import com.github.clans.fab.FloatingActionButton;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ActivitySetAsWallpaper extends AppCompatActivity {

    private CropImageView mCropImageView;
    String[] str_image, str_cat_name;
    int position;
    private InterstitialAd interstitialAd;
    FloatingActionButton fab;
    Toolbar toolbar;
    Config config;
    private AdView adView;
    RelativeLayout relativeLayout;
    ViewGroup rootView;
    SharedPreferences sharedPreferences;
    String adsStaus;
    Banner banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_as_wallpaper);
        rootView = (ViewGroup) findViewById(android.R.id.content);
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");
        }
  //      banner = (com.startapp.android.publish.ads.banner.Banner) findViewById(R.id.startAppBanner_set_As_wallpaper);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        adsStaus=sharedPreferences.getString("ads","");
        if(adsStaus.equals("disable"))
        {
            banner.hideBanner();
        }



        config = new Config();
        toolbar = findViewById( R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        //loadInterstitialAd ( );
       // showInterstitialAd ( );


        Intent i = getIntent();
        str_image = i.getStringArrayExtra("WALLPAPER_IMAGE_URL");
        str_cat_name = i.getStringArrayExtra("WALLPAPER_IMAGE_CATEGORY");
        position = i.getIntExtra("POSITION_ID", 0);
        mCropImageView = findViewById( R.id.CropImageView);

        if (JsonUtils.isNetworkAvailable( this)) {
            new ActivitySetAsWallpaper.MyTask().execute( Config.ADMIN_PANEL_URL + "/admob.php?action=action");
        } else {
            Toast.makeText ( this, "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
        }

        fab = findViewById( R.id.setAsWallpaper);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new SetWallpaperTask(ActivitySetAsWallpaper.this)).execute("");
                if (adsStaus.equals("enable") && config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && interstitialAd!=null) {

                    StartAppAd.disableSplash();
                    StartAppAd.disableAutoInterstitial();

                    if (interstitialAd.isLoaded() && adsStaus.equals("enable")) {
                        StartAppAd.disableSplash();
                        StartAppAd.disableAutoInterstitial();
                        interstitialAd.show();
                        loadInterstitialAd();

                    } else {
                      //  loadInterstitialAd();
                       // StartAppAd.showAd(getApplicationContext());


                    }
                }
            }
        });
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageLoader.getInstance().loadImage(Config.ADMIN_PANEL_URL + "/upload/" + str_image[position], new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                // TODO Auto-generated method stub
                mCropImageView.setImageBitmap(arg2);
            }
            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }
        });
    }
    public class SetWallpaperTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog pDialog;
        Bitmap bmImg = null;

        public SetWallpaperTask(Context context) {
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
            return null;
        }
        @Override
        protected void onPostExecute(String args) {
            bmImg = mCropImageView.getCroppedImage();

            WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
            try {
                wpm.setBitmap(bmImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
            Toast.makeText(ActivitySetAsWallpaper.this, getResources().getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();

            finish();
        }
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
    private void loadInterstitialAd() {
       if(config.isENABLE_ADMOB_INTERSTITIAL_ADS () .equals("true")) {
           Log.d ( "TAG", "showAd" );
           StartAppAd.disableSplash ();
           StartAppAd.disableAutoInterstitial ();
           interstitialAd = new InterstitialAd ( ActivitySetAsWallpaper.this );
           interstitialAd.setAdUnitId ( config.getAdmob_interstitial_unit_id ( ) );
           AdRequest adRequest = new AdRequest.Builder ( ).build ( );
           interstitialAd.loadAd ( adRequest );

       }else{
          // Toast.makeText ( getApplicationContext (),"ads disable from backend ",Toast.LENGTH_LONG ).show ();
          // StartAppAd.enableAutoInterstitial();
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
    private void loadAdMobBannerAd() {
        if (config.isENABLE_ADMOB_BANNER_ADS ().equals("true") & config.getAdmob_banner_unit_id () != null) {
            StartAppAd.disableAutoInterstitial();
            StartAppAd.disableSplash();
            String banner_id = config.getAdmob_banner_unit_id ();
            adView = new AdView(getApplicationContext ());
            adView.setAdSize (AdSize.SMART_BANNER);
            adView.setAdUnitId(config.getAdmob_banner_unit_id());
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivitySetAsWallpaper.this)).build();


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
                    if(config.getAdmob_app_id () !=null) {
                        // GDPR.updateConsentStatus ( MainActivity.this );
                        //Toast.makeText ( getApplicationContext (),"app id",Toast.LENGTH_LONG ).show ();

                    }else{
                        //Toast.makeText ( getApplicationContext (),"no app id",Toast.LENGTH_LONG ).show ();
                    }
                    if(config.isENABLE_ADMOB_INTERSTITIAL_ADS().equals("true")) {

                        loadInterstitialAd ();



                    }else{
                        Toast.makeText ( ActivitySetAsWallpaper.this, "ads disable for back end ", Toast.LENGTH_SHORT ).show ( );
                       if(adsStaus.equals("enable")) {
                        //   StartAppAd.showAd(getApplicationContext());
                       }
                    }

                    if(config.ENABLE_ADMOB_BANNER_ADS.equals("true") && adsStaus.equals("enable")){
                        loadAdMobBannerAd();
                    }
                    else {
                       // Toast.makeText(ActivitySetAsWallpaper.this,"Banner ad is disabled from backend",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }


        }

    }



}
