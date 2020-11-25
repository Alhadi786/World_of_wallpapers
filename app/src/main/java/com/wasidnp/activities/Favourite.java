package com.wasidnp.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.wasidnp.adapters.AdapterFavorite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import android.os.Handler;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;


import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.common.AppConstant;
import com.wasidnp.common.SaveData;
import com.wasidnp.database.DatabaseHandlerFavorite;
import com.wasidnp.database.DatabaseHandlerFavorite.DatabaseManager;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemRecent;
import com.wasidnp.utilities.GDPR;
import com.wasidnp.utilities.ItemOffsetDecoration;
import com.startapp.android.publish.adsCommon.StartAppAd;

import org.json.JSONException;
import org.json.JSONObject;


public class Favourite extends AppCompatActivity{

    RecyclerView recyclerView;
    DatabaseHandlerFavorite databaseHandlerFavorite;
    private DatabaseManager databaseManager;
    AdapterFavorite adapterFavorite;
    ArrayList<String> list_image, image_cat_name;
    String[] str_list_image, str_image_cat_name;
    List<ItemRecent> listItem;
    LinearLayout linearLayout;
    private int columnWidth;
    JsonUtils jsonUtils;
    private Toolbar toolbar;
    Config config;
    private AdView adView;
    ViewGroup rootView;
    String adsStaus;
    SharedPreferences sharedPreferences;
    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


        toolbar=(Toolbar)findViewById(R.id.favourite_toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        linearLayout = (LinearLayout) findViewById(R.id.lyt_no_favorite);
        databaseHandlerFavorite = new DatabaseHandlerFavorite(Favourite.this);
        databaseManager = DatabaseManager.INSTANCE;
        databaseManager.init(Favourite.this);
        jsonUtils = new JsonUtils(Favourite.this);
        rootView = (ViewGroup) findViewById(android.R.id.content);
        setupToolbar();
        config=new Config();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        adsStaus=sharedPreferences.getString("ads","");

        recyclerView.setLayoutManager(new GridLayoutManager(Favourite.this, Config.NUM_OF_COLUMNS));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(Favourite.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        if (JsonUtils.isNetworkAvailable( this)) {
            new MyTask ().execute( Config.ADMIN_PANEL_URL + "/admob.php?action=action");

        }

        listItem = databaseHandlerFavorite.getAllData();
        adapterFavorite = new AdapterFavorite(Favourite.this, listItem);
        recyclerView.setAdapter(adapterFavorite);
        if (listItem.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(Favourite.this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(Favourite.this, ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", str_list_image);
                        intent.putExtra("IMAGE_CATNAME", str_image_cat_name);

                        startActivity(intent);
                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(Favourite.this, ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", str_list_image);
                        intent.putExtra("IMAGE_CATNAME", str_image_cat_name);

                        startActivity(intent);
                    }
                }, 400);

            }
        }));
    }

    private void setupToolbar() {
        toolbar.setTitle("Favourite");
        toolbar.setBackgroundColor ( Color.BLACK );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    protected void onDestroy() {
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        listItem = databaseHandlerFavorite.getAllData();
        adapterFavorite = new AdapterFavorite(Favourite.this, listItem);
        recyclerView.setAdapter(adapterFavorite);
        if (listItem.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }
        list_image = new ArrayList<String>();
        image_cat_name = new ArrayList<String>();

        str_list_image = new String[list_image.size()];
        str_image_cat_name = new String[image_cat_name.size()];

        for (int j = 0; j < listItem.size(); j++) {

            ItemRecent objAllBean = listItem.get(j);

            list_image.add(objAllBean.getImageurl());
            str_list_image = list_image.toArray(str_list_image);

            image_cat_name.add(objAllBean.getCategoryName());
            str_image_cat_name = image_cat_name.toArray(str_image_cat_name);

        }
        if (databaseManager == null) {
            databaseManager = DatabaseManager.INSTANCE;
            databaseManager.init(Favourite.this);
        } else if (databaseManager.isDatabaseClosed()) {
            databaseManager.init(Favourite.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
    }




    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Favourite.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Favourite.ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadAdMobBannerAd() {
        if (config.isENABLE_ADMOB_BANNER_ADS ().equals("true") & config.getAdmob_banner_unit_id () != null) {
            String banner_id = config.getAdmob_banner_unit_id ();
            adView = new AdView(getApplicationContext ());
            adView.setAdSize (AdSize.SMART_BANNER);
            adView.setAdUnitId(config.getAdmob_banner_unit_id());
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(Favourite.this)).build();


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
            StartAppAd.showAd ( getApplicationContext ());

            Log.d("MainActivity", "AdMob Banner is Disabled");
        }
    }

    private void loadInterstitialAd() {

        Log.d("TAG", "showAd");
        MobileAds.initialize(this, config.getAdmob_app_id());
        interstitialAd = new InterstitialAd( this);
        interstitialAd.setAdUnitId( config.getAdmob_interstitial_unit_id());
        if(config.ENABLE_ADMOB_INTERSTITIAL_ADS.equals("true") && config.admob_interstitial_unit_id!=null){

            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
            if(!interstitialAd.isLoaded())
                interstitialAd.loadAd(new AdRequest.Builder().build());

        }


    }


    public interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
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
                    SaveData objs   =  new SaveData(Favourite.this);
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
                            StartAppAd.showAd(getApplicationContext());
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
}
