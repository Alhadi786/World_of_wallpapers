package com.wasidnp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.common.AppConstant;
import com.wasidnp.common.SaveData;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.tab.FragmentTabCategory;
import com.wasidnp.tab.FragmentTabRecent;
import com.wasidnp.tab.ViewPagerAdapter;
import com.wasidnp.util.IabHelper;
import com.wasidnp.util.IabResult;
import com.wasidnp.util.Purchase;
import com.wasidnp.utilities.GDPR;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,RewardedVideoAdListener,DrawerLayout.DrawerListener {
    private RewardedVideoAd rewardedVideoAd;
    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String CATEGORY_FRAGMENT_TAG = "category";
    private final static String FAVORITE_FRAGMENT_TAG = "favorite";
    private final static String ABOUT_FRAGMENT_TAG = "about";
    private final static String SELECTED_TAG = "selected_index";
    private final static int COLLAPSING_TOOLBAR = 0;
    private final static int CATEGORY = 1;
    private final static int FAVORITE = 2;
    private final static int RATE = 3;
    private final static int MORE = 4;
    private final static int SHARE = 5;
    private final static int ABOUT = 6;
    RelativeLayout relativeLayout;
    ViewGroup rootView;
    int counter = 1;
    private static int selectedIndex;
    private InterstitialAd interstitialAd;

    static final String TAG = "MainActivity";
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private AdView adView;
   // Handler runHandler;
    Runnable showAds,stopads;
    Config config;

    String refreshedToken;

    private boolean handlerFlag;

    private static final int REQUEST = 112;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String adsStaus;
    Banner banner;
    boolean stopAdCheck;


    //Hit and trail
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    Toolbar toolbar;
   //  AppBarLayout appBarLayout;



    //=================//
    String base64EncodedPublicKey = "";
    IabHelper mHelper;
    private String TAG1 = "IN APP";
    private String SKU_GAS = "";
    private String SKU_MONTH = "monthly_pack";
    private String SKU_THREE_MONTHS = "three_months";
    private String SKU_SIX_MONTHS = "six_months";
    private String SKU_YEARLY = "yearly";


    static final int RC_REQUEST = 10001;


    private SaveData objs;

    //===============//

    MenuItem subMenuItem;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StartAppSDK.init( this, String.valueOf(R.string.startapp_id), true);
//        StartAppAd.disableAutoInterstitial ();
//        StartAppAd.disableSplash ();
        handlerFlag=true;


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        objs  =  new SaveData(MainActivity.this);

        Log.e("abc","======= objs.get(AppConstant.IS_SUBSCRIBED) ====   " + objs.get(AppConstant.IS_SUBSCRIBED));

        //Hit trial
        // appBarLayout=(AppBarLayout)findViewById(R.id.tab_appbar_layout);
        navigationView = findViewById( R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById( R.id.drawer_layout_main);
       // runHandler=new Handler();
        config = new Config ();
        if (savedInstanceState != null) {
            navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }
 //       banner=(com.startapp.android.publish.ads.banner.Banner)findViewById(R.id.startAppBanner_mainativity);

        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")) {
            banner.hideBanner();
        }else{

        }

            stopAdCheck=false;

        selectedIndex = COLLAPSING_TOOLBAR;
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setupNavigationDrawer(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        viewPager=(ViewPager)findViewById(R.id.viewpager_main);
        viewPager.setOffscreenPageLimit(4);
        tabLayout=(TabLayout)findViewById(R.id.tabs_main);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt ( 0 ).setIcon(R.drawable.latest).setText ( R.string.tab_category);
        tabLayout.getTabAt ( 1 ).setIcon(R.drawable.wallpaper).setText ( R.string.tab_recent ).select();
        tabLayout.getTabAt ( 2 ).setIcon(R.drawable.rate).setText ( R.string.tab_popular);
        tabLayout.getTabAt ( 3 ).setIcon(R.drawable.popular).setText ( R.string.tab_trending);

        //Hit trail end

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        editor=sharedPreferences.edit();
        editor.putString("ads","enable");
        editor.apply();
        adsStaus="enable";

       // adsStaus=sharedPreferences.getString("ads","");
       // relativeLayout = findViewById ( R.id.main_content);
        rootView = (ViewGroup) findViewById(android.R.id.content);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
      //  Log.d ( "Token:",refreshedToken );
        //banner = (com.startapp.android.publish.ads.banner.Banner) findViewById(R.id.startAppBanner);







 //store fcm token data base
        if (JsonUtils.isNetworkAvailable( this)) {
            new MyTaskFcm ().execute( Config.ADMIN_PANEL_URL +  "/fcm.php?action=action"+"&"+"token="+refreshedToken);

            checkAds();
        } else {
           // Toast.makeText ( this, "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
        }






        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");
        }










      /*  getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new FragmentTabRecent(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
*/

        requestStoragePermission();

        purchaseFunctionalityStarts();
    }






    private void showIntersitialAdEvery2Minutes() {
        if(stopAdCheck) {
          //  runHandler.removeCallbacks(stopads);
            stopAdCheck=false;
        }
       //  loadInterstitialAd();
            /*if (interstitialAd.isLoaded() && adsStaus.equals("enable")) {
                StartAppAd.disableAutoInterstitial();
                StartAppAd.disableSplash();


                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        //  Toast.makeText(MainActivity.this,"ads showing",Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,"interstial runable",Toast.LENGTH_SHORT).show();
                        interstitialAd.show();
                        showIntersitialAdEvery2Minutes();

                    }

                },1000);
                //runHandler.postDelayed(showAds,1000);
                // Toast.makeText(MainActivity.this,"Ad is loaded",Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,"program is runing",Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (adsStaus.equals("enable")) {
                        loadAdMobBannerAd();
                    }

                }
            }
            else {
                loadInterstitialAd();
               // showIntersitialAdEvery2Minutes();
            }



      //  Toast.makeText(MainActivity.this,"interstial is showing",Toast.LENGTH_SHORT).show();

*/


    }

    private void RunInterstialADs() {


    }




    @TargetApi(Build.VERSION_CODES.M)
    private void checkAds() {
        if (JsonUtils.isNetworkAvailable( this)) {
            new MyTask ().execute( Config.ADMIN_PANEL_URL + "/admob.php?action=action");
           /* if(config.isENABLE_ADMOB_BANNER_ADS () & config.getAdmob_banner_unit_id () != null){
                loadAdMobBannerAd ();
            }*//*else{
                Toast.makeText ( MainActivity.this, "ads disable for back end ", Toast.LENGTH_SHORT ).show ( );
            }*/
        }
    }
    private void loadInterstitialAd() {
        Log.e("inter", " =======intter========= ");

        Log.d("TAG", "showAd");
        MobileAds.initialize(this, config.getAdmob_app_id());
        interstitialAd = new InterstitialAd ( this);
        interstitialAd.setAdUnitId( config.getAdmob_interstitial_unit_id());
        if(config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && config.admob_interstitial_unit_id!=null && adsStaus.equals("enable")){

            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
             if(!interstitialAd.isLoaded()) {
                 interstitialAd.loadAd(new AdRequest.Builder().build());
             }
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {


            case R.id.drawer_recent:
                if (!menuItem.isChecked()) {
                    selectedIndex = COLLAPSING_TOOLBAR;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentTabRecent(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
           case R.id.subscribe:
                subDialog();
                return true;

            case R.id.add_free:

                if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")) {
                    menuItem.setVisible(false);

                }else{
                    menuItem.setVisible(true);
                    loadrewardVideoAd();
                    loadInterstitialAd();

                    new LovelyStandardDialog ( this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.reward)
                            .setTitle("Rewarded Ad")
                            .setMessage(R.string.rate_message)
                            .setPositiveButtonColorRes ( R.color.btn_alert )
                            .setNegativeButtonColorRes ( R.color.btn_alert )
                            .setPositiveButton("WATCH A VIDEO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!rewardedVideoAd.isLoaded()) {
                                        if(adsStaus.equals("enable")) {
                                            //  Toast.makeText(getApplicationContext(), "Ad is not loaded wrong id", Toast.LENGTH_LONG).show();
                                            if (interstitialAd.isLoaded()) {
                                                interstitialAd.show();
                                            } else {
                                           //     StartAppAd.showAd(getApplicationContext());
                                             //   loadInterstitialAd();
                                            }
                                        }
                                        //   loadrewardVideoAd();

                                        //  Toast.makeText ( getApplicationContext (),"Ad is loaded",Toast.LENGTH_LONG ).show ();

                                        //  rewardedVideoAd.show();
                                    }
                                    else if(rewardedVideoAd.isLoaded()){
                                        StartAppAd.disableAutoInterstitial();
                                        StartAppAd.disableSplash();
                                        //  runHandler.removeCallbacks(showAds);

                                        rewardedVideoAd.show();
                                        //    runHandler.postDelayed(showAds,1000);
                                        // loadrewardVideoAd();
                                        // Toast.makeText ( getApplicationContext (),"Ad is loaded",Toast.LENGTH_LONG ).show ();

                                    }
                                    else {
                                        //  StartAppAd.showAd ( getApplicationContext () );
                                        // Toast.makeText ( getApplicationContext ( ), "Video Not Avaialble now", Toast.LENGTH_LONG ).show ( );

                                    }
                                }
                            })
                            .setNegativeButton ( "I LIKE ADS", new View.OnClickListener ( ) {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                }
              /*pk commented  loadrewardVideoAd ();**/
              // loadVideoRewardAd();
                /* PK  loadInterstitialAd();

                 new LovelyStandardDialog ( this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.reward)
                        .setTitle("Rewarded Ad")
                        .setMessage(R.string.rate_message)
                        .setPositiveButtonColorRes ( R.color.btn_alert )
                        .setNegativeButtonColorRes ( R.color.btn_alert )
                        .setPositiveButton("WATCH A VIDEO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                               if (!rewardedVideoAd.isLoaded()) {
                                        if(adsStaus.equals("enable")) {
                                          //  Toast.makeText(getApplicationContext(), "Ad is not loaded wrong id", Toast.LENGTH_LONG).show();
                                            if (interstitialAd.isLoaded()) {
                                                interstitialAd.show();
                                            } else {
                                                StartAppAd.showAd(getApplicationContext());
                                                loadInterstitialAd();
                                            }
                                        }
                                      //   loadrewardVideoAd();

                                      //  Toast.makeText ( getApplicationContext (),"Ad is loaded",Toast.LENGTH_LONG ).show ();

                                      //  rewardedVideoAd.show();
                               }
                                else if(rewardedVideoAd.isLoaded()){
                                   StartAppAd.disableAutoInterstitial();
                                   StartAppAd.disableSplash();
                                 //  runHandler.removeCallbacks(showAds);

                                    rewardedVideoAd.show();
                                //    runHandler.postDelayed(showAds,1000);
                                   // loadrewardVideoAd();
                                   // Toast.makeText ( getApplicationContext (),"Ad is loaded",Toast.LENGTH_LONG ).show ();

                                }
                                else {
                                  //  StartAppAd.showAd ( getApplicationContext () );
                                  // Toast.makeText ( getApplicationContext ( ), "Video Not Avaialble now", Toast.LENGTH_LONG ).show ( );

                                }
                            }
                        })
                        .setNegativeButton ( "I LIKE ADS", new View.OnClickListener ( ) {
                            @Override
                            public void onClick(View view) {

                                }
                        })
                        .show(); */
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.drawer_category:
                if (!menuItem.isChecked()) {
                    selectedIndex = COLLAPSING_TOOLBAR;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentTabCategory(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_favorite:
          /*      if (!menuItem.isChecked()) {
                    selectedIndex = FAVORITE;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentTabFavorite(), FAVORITE_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);*/

                Intent fav=new Intent(MainActivity.this,Favourite.class);
                startActivity(fav);
                return true;

            case R.id.drawer_rate:
                if (!menuItem.isChecked()) {
                    selectedIndex = RATE;
                    menuItem.setChecked(true);

                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_more:
                if (!menuItem.isChecked()) {
                    selectedIndex = MORE;
                    menuItem.setChecked(true);

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
           /*     if (!menuItem.isChecked()) {
                    selectedIndex = FAVORITE;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                                                                            new FragmentSetting (), FAVORITE_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;*/

           Intent intent=new Intent(MainActivity.this,Setting.class);
           startActivity(intent);
           return true;

            case R.id.drawer_share:
                if (!menuItem.isChecked()) {
                    selectedIndex = SHARE;
                    menuItem.setChecked(true);

                    Intent sendInt = new Intent(Intent.ACTION_SEND);
                    sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                    sendInt.setType("text/plain");
                    startActivity(Intent.createChooser(sendInt, "Share"));
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_about:
               /* if (!menuItem.isChecked()) {
                    selectedIndex = ABOUT;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentAbout(), ABOUT_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);*/

                Intent aboutUsIntent=new Intent(MainActivity.this,AboutUs.class);
                startActivity(aboutUsIntent);
                return true;

        }
        return false;
    }
// reward_video_ads
    private void loadrewardVideoAd() {

        MobileAds.initialize ( this, config.getAdmob_app_id());
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance ( this );
        rewardedVideoAd.setRewardedVideoAdListener ( this );



      if(config.isENABLE_ADMOB_VIDEO_ADS ().equals("true")){
          StartAppAd.disableSplash();
          StartAppAd.disableAutoInterstitial();


            if(!rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.loadAd(config.getAdmob_reward_video_unit_id(),
                        new AdRequest.Builder().build());
                Log.d("rewardedevide","ad is loa");
            }

        }
        else{
     //     StartAppAd.showAd ( getApplicationContext () );
          Toast.makeText ( getApplicationContext (),"Videos Not Available Cureently ",Toast.LENGTH_LONG ).show ();
      }

    }
    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (Config.ENABLE_EXIT_DIALOG) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(R.string.dialog_close_msg);
                dialog.setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                });

                dialog.setNegativeButton(R.string.dialog_option_rate_us, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String appName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                        }
                        MainActivity.this.finish();
                    }
                });
//                dialog.setNeutralButton(R.string.dialog_option_more, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
//
//                        MainActivity.this.finish();
//                    }
//                });
                dialog.show();

            } else {
                super.onBackPressed();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadAdMobBannerAd() {
        if (config.isENABLE_ADMOB_BANNER_ADS ().equals("true") & config.getAdmob_banner_unit_id () != null) {
            String banner_id = config.getAdmob_banner_unit_id ();
            adView = new AdView(getApplicationContext ());
            adView.setAdSize (AdSize.SMART_BANNER);
            adView.setAdUnitId(config.getAdmob_banner_unit_id());
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(MainActivity.this)).build();


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
         //   StartAppAd.showAd ( getApplicationContext ());

            Log.d("MainActivity", "AdMob Banner is Disabled");
        }
    }

    @TargetApi(16)
    private void requestStoragePermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.d("Log", "permission granted");
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.permisson_title);
        builder.setMessage(R.string.permisson_message);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
      //  Toast.makeText ( this, "video loaded", Toast.LENGTH_SHORT ).show ( );

    }
    @Override
    public void onRewardedVideoAdOpened() {
      //  Toast.makeText ( this, "video open", Toast.LENGTH_SHORT ).show ( );


    }
    @Override
    public void onRewardedVideoStarted() {
       // Toast.makeText ( this, "video started", Toast.LENGTH_SHORT ).show ( );


    }
    @Override
    public void onRewardedVideoAdClosed() {
      //  Toast.makeText ( this, "video closed", Toast.LENGTH_SHORT ).show ( );

        //loadrewardVideoAd ();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
      //  Toast.makeText ( this, "video rewarded item", Toast.LENGTH_SHORT ).show ( );


    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
       // Toast.makeText ( this, "video  left application", Toast.LENGTH_SHORT ).show ( );
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
     //   Toast.makeText ( this, "Video Not Available Now", Toast.LENGTH_SHORT ).show ( );
        if(adsStaus.equals("enable")) {
            if (adsStaus.equals("enable") && config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true")) {

                if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

                }else {

                    Log.e("inter", " =======intter==22======= ");

                    if (interstitialAd.isLoaded() && interstitialAd != null) {
                        interstitialAd.show();
                    }
                    loadrewardVideoAd();
                }

            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // runHandler.removeCallbacks(showAds);
        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

        }else {
            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
        }

    }


    @Override
    public void onRewardedVideoCompleted() {
       // Toast.makeText ( this, "video completed", Toast.LENGTH_SHORT ).show ( );


        Toast.makeText(MainActivity.this,"All ads are now dissapear",Toast.LENGTH_SHORT).show();


        editor.putString("ads","disable");
        editor.apply();
        adsStaus="disable";
        //runHandler.removeCallbacks(showAds);
        adView.setVisibility(View.GONE);
        if(adView!=null)
        {
            adView.destroy();

            // Toast.makeText(MainActivity.this,"adview is destry now",Toast.LENGTH_SHORT).show();
        }
        if(banner!=null) {
            banner.hideBanner();
        }
        StartAppAd.disableSplash();
        StartAppAd.disableAutoInterstitial();
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"All ads are now dissapear for 10 minutes",Toast.LENGTH_SHORT).show();
                adsStaus="enable";
                editor.putString("ads","enable");
                editor.apply();
                loadAdMobBannerAd();
                loadInterstitialAd ();
                StartAppAd.disableAutoInterstitial();
                StartAppAd.disableSplash();
                showIntersitialAdEvery2Minutes();
                stopAdCheck=true;
                Log.d("serviceahowads","runningasasa");

            }
        },600000);




        loadrewardVideoAd();


       // runHandler.postDelayed(stopads,600000);



    }

    @Override
    protected void onResume() {
       /*  if(config.isENABLE_ADMOB_VIDEO_ADS ().equals("true")) {
            rewardedVideoAd.resume ( getApplicationContext ( ) );
       }*/
        super.onResume ( );


//        runHandler.postDelayed(showAds,1000);

    /*    if(!handlerFlag)
        {
            if(adsStaus.equals("enable")) {
                showIntersitialAdEvery2Minutes();
            }
            handlerFlag=true;
        }*/

    if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

    }else {
        StartAppAd.disableSplash();
        StartAppAd.disableAutoInterstitial();
    }
      /*  if(adsStaus.equals("enable")) {
            showIntersitialAdEvery2Minutes();

        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

        }else {
            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
        }

     //   runHandler.postDelayed(showAds,120000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){

        }else {
            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
        }

        //runHandler.removeCallbacks(showAds);
    }

    @Override
    public boolean isDestroyed() {

       rewardedVideoAd.destroy ( getApplicationContext () );
       return super.isDestroyed ( );
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

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

                        Log.e("inter", " =======intter========= ");

                        if (config.getAdmob_app_id() != null) {
                            // GDPR.updateConsentStatus ( MainActivity.this );
                            //Toast.makeText ( getApplicationContext (),"app id",Toast.LENGTH_LONG ).show ();

                        } else {
                            //Toast.makeText ( getApplicationContext (),"no app id",Toast.LENGTH_LONG ).show ();
                        }
                        if (config.isENABLE_ADMOB_INTERSTITIAL_ADS().equals("true")) {

                            loadInterstitialAd();
                            if (adsStaus.equals("enable") && interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }

                        } else {
                            //Toast.makeText ( MainActivity.this, "ads disable for back end ", Toast.LENGTH_SHORT ).show ( );
                            if (adsStaus.equals("enable")) {
                             //   StartAppAd.showAd(getApplicationContext());
                            }
                        }
                        if (config.isENABLE_ADMOB_VIDEO_ADS().equals("true")) {
                            loadrewardVideoAd();
                        } else {
                            // Toast.makeText ( getApplicationContext (),"Video ads disbale from back end ",Toast.LENGTH_LONG ).show ();
                        }
                        if (config.ENABLE_ADMOB_BANNER_ADS.equals("true") && adsStaus.equals("enable")) {
                            loadAdMobBannerAd();
                        } else {
                            //   Toast.makeText(MainActivity.this,"Banner ad is disabled from backend",Toast.LENGTH_SHORT).show();
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


    private void purchaseFunctionalityStarts(){

        base64EncodedPublicKey  =  getResources().getString(R.string.subscription_key);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {

                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.e(TAG1, "Setup successful. Querying inventory.");

                Toast.makeText(MainActivity.this, "==== OnCreate======" ,  Toast.LENGTH_LONG);
                //mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    // User clicked the "Buy Gas" button
    private void onBuyPackButtonClicked() {

        String payload = "";

        mHelper.launchSubscriptionPurchaseFlow(this, SKU_GAS, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }



    /*IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.e(TAG1, "Query inventory finished.");

            if (mHelper == null) return;
            if (result.isFailure()) {
                return;
            }

            Log.e(TAG1, "Query inventory was successful.");

            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.e(TAG1, "We have gas. Consuming it.");
                return;
            }


            Log.e(TAG1, "Initial inventory query finished; enabling main UI.");
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {

            Toast.makeText(MainActivity.this, " onActivityResult handled by IABUtil.",  Toast.LENGTH_LONG);
            Log.e(TAG1, "======onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();


        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.e(TAG1, "Purchase finished: " + result + ", purchase: " + purchase);

            if (result.isFailure()) {
                Log.e("abc", " =======purchase failed======= " );
                return;
            }


            Log.e("abc", " =======Purchase DONE Sucessfully======= " );

            // Toast.makeText(Activity_Pay.this, "subscription about to do", Toast.LENGTH_LONG).show();
            objs.save(AppConstant.IS_SUBSCRIBED, "1");

            Toast.makeText(MainActivity.this, objs.get(AppConstant.IS_SUBSCRIBED), Toast.LENGTH_LONG).show();


            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();

            Toast.makeText(MainActivity.this, " ***Purchase successful***",  Toast.LENGTH_LONG);

            Log.e(TAG1, "***Purchase successful***");

        }
    };


    private void subDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.subscription_alert);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        ImageView ivSub = (ImageView) dialog.findViewById(R.id.ivSub);
        Button btnOne = (Button) dialog.findViewById(R.id.btnOne);
        Button btnThree = (Button) dialog.findViewById(R.id.btnThree);
        Button btnSix = (Button) dialog.findViewById(R.id.btnSix);
        Button btnLeftTime = (Button) dialog.findViewById(R.id.btnLeftTime);


        ivSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKU_GAS =  SKU_MONTH;

                onBuyPackButtonClicked();
                dialog.dismiss();
            }
        });


        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKU_GAS =  SKU_THREE_MONTHS;
                onBuyPackButtonClicked();
                dialog.dismiss();
            }
        });

        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKU_GAS =  SKU_SIX_MONTHS;
                onBuyPackButtonClicked();
                dialog.dismiss();
            }
        });

        btnLeftTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKU_GAS =  SKU_YEARLY;
                onBuyPackButtonClicked();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

}


