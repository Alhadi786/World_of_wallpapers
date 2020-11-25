package com.wasidnp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.adapters.AdapterWallpaperByCategory;
import com.wasidnp.common.AppConstant;
import com.wasidnp.common.SaveData;
import com.wasidnp.database.DatabaseHandlerImages;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemWallpaperByCategory;
import com.wasidnp.utilities.GDPR;
import com.wasidnp.utilities.ItemOffsetDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityWallpaperByCategory extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ItemWallpaperByCategory> itemWallpaperByCategories;
    AdapterWallpaperByCategory adapterWallpaperByCategory;
    ArrayList<String> list_image, image_cat_name, image_id;
    String[] str_list_image, str_image_cat_name, str_image_id;
    JsonUtils util;
    AdView adView;
    Config config;
    RelativeLayout relativeLayout;
    public DatabaseHandlerImages databaseHandlerImages;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout = null;
   // private InterstitialAd interstitialAd;
    int counter = 1;

    ViewGroup rootView;

    SharedPreferences sharedPreferences;
    String adsStaus;
    Banner banner;

    SaveData objs;

    String order;

    int swipingCounter;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StartAppSDK.init( this, String.valueOf(R.string.startapp_id), true);
//        StartAppAd.disableAutoInterstitial ();
//        StartAppAd.disableSplash ();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_by_category);
        setTitle(JsonConfig.CATEGORY_TITLE);
        rootView = (ViewGroup) findViewById(android.R.id.content);

        objs  =  new SaveData(ActivityWallpaperByCategory.this);
        order="random";

        swipingCounter=0;


        /*if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("RTL Mode", "Working in Normal Mode, RTL Mode is Disabled");
        }*/

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        adsStaus=sharedPreferences.getString("ads","");
      //  banner = (com.startapp.android.publish.ads.banner.Banner) findViewById(R.id.startAppBanner_wallpaper_by_ctg);

        if(objs.get(AppConstant.IS_SUBSCRIBED).equals("1")){
            banner.hideBanner();
        }

        if(adsStaus.equals("disable"))
        {
            banner.hideBanner();
        }

        if (JsonUtils.isNetworkAvailable( this)) {
            new ActivityWallpaperByCategory.newTask().execute( Config.ADMIN_PANEL_URL + "/admob.php?action=action");
        } else {
            Toast.makeText ( this, "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
        }
        config = new Config ();
        final Toolbar toolbar = findViewById( R.id.toolbar);
        relativeLayout = findViewById ( R.id.relativeLayout );
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.category_asc)
                {
                    order="asc";
                    clearData();
                    new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_ID);
                }
                else if(item.getItemId()== R.id.category_desc)
                {
                    order="desc";
                    clearData();
                    new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_ID);
                    Toast.makeText(ActivityWallpaperByCategory.this, "Descending", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
//      if(config.isENABLE_ADMOB_BANNER_ADS ()==true) {
//          loadAdMobBannerAd ( );
//      }else{
//            StartAppAd.showAd ( getApplicationContext () );
//            Toast.makeText ( getApplicationContext (),"banner disabled from backend",Toast.LENGTH_LONG ).show ();
//        }


        databaseHandlerImages = new DatabaseHandlerImages(ActivityWallpaperByCategory.this);

        progressBar = findViewById( R.id.progressBar_wallpaper_by_category);
        recyclerView = findViewById( R.id.recycler_view);
        swipeRefreshLayout = findViewById( R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), Config.NUM_OF_COLUMNS));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        itemWallpaperByCategories = new ArrayList<ItemWallpaperByCategory>();

        list_image = new ArrayList<String>();
        image_cat_name = new ArrayList<String>();
        image_id = new ArrayList<String>();

        str_list_image = new String[list_image.size()];
        str_image_cat_name = new String[image_cat_name.size()];
        str_image_id = new String[image_id.size()];

        util = new JsonUtils(getApplicationContext());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", str_list_image);
                        intent.putExtra("IMAGE_CATNAME", str_image_cat_name);
                        intent.putExtra("ITEMID", str_image_id);

                        if (JsonUtils.isNetworkAvailable(ActivityWallpaperByCategory.this)) {
                            String image_url = str_list_image[position];
                            new MyTasCountAdded ().execute( Config.ADMIN_PANEL_URL +  "/count_added.php?action=action"+"&"+"image_url="+image_url);
                        } else {
                            Toast.makeText (  getApplicationContext (), "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
                  //          StartAppAd.showAd ( getApplicationContext () );
                        }

                        startActivity(intent);

                        if (position == 0) {
                           // showInterstitialAd();
                        }

                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", str_list_image);
                        intent.putExtra("IMAGE_CATNAME", str_image_cat_name);
                        intent.putExtra("ITEMID", str_image_id);

                        startActivity(intent);

                        if (position == 0) {
                           // showInterstitialAd();
                        }

                    }
                }, 1000);

            }
        }));

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (JsonUtils.isNetworkAvailable(ActivityWallpaperByCategory.this)) {
                            swipingCounter++;
                            if(swipingCounter>=4 && swipingCounter<=5)
                            {
                                if(!interstitialAd.isLoaded())
                                {
                                    loadInterstitialAd();
                                }
                            }
                            if(swipingCounter>=6)
                            {
                                swipingCounter=0;
                              if(interstitialAd.isLoaded())
                              {
                                  StartAppAd.disableSplash();
                                  StartAppAd.disableAutoInterstitial();
                                  interstitialAd.show();

                                  loadInterstitialAd();
                              }
                              else
                              {

                                  loadInterstitialAd();
                              //    StartAppAd.showAd(getApplicationContext());
                              }

                            }
                            order="random";
                            clearData();
                            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_ID);
                            swipeRefreshLayout.setRefreshing(false);

                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.refresh_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 3000);
            }
        });

        if (JsonUtils.isNetworkAvailable(ActivityWallpaperByCategory.this)) {
            order="random";
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_ID);
        } else {
            itemWallpaperByCategories = databaseHandlerImages.getFavRow(JsonConfig.CATEGORY_ID);
            if (itemWallpaperByCategories.size() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_first_load), Toast.LENGTH_SHORT).show();
            }
            setAdapterToListView();
            for (int j = 0; j < itemWallpaperByCategories.size(); j++) {

                ItemWallpaperByCategory objCategoryBean = itemWallpaperByCategories.get(j);

                list_image.add(objCategoryBean.getItemImageurl());
                str_list_image = list_image.toArray(str_list_image);

                image_cat_name.add(objCategoryBean.getItemCategoryName());
                str_image_cat_name = image_cat_name.toArray(str_image_cat_name);

                image_id.add(objCategoryBean.getItemCatId());
                str_image_id = image_id.toArray(str_image_id);

            }
        }
    }



    public void clearData() {
        int size = this.itemWallpaperByCategories.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.itemWallpaperByCategories.remove(0);
            }

            adapterWallpaperByCategory.notifyItemRangeRemoved(0, size);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                ActivityWallpaperByCategory.this.finish();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ITEM_ARRAY);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemWallpaperByCategory objItem = new ItemWallpaperByCategory();

                        databaseHandlerImages.AddtoFavoriteCateList(new ItemWallpaperByCategory(objJson.getString(JsonConfig.CATEGORY_ITEM_CATNAME), objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGEURL), objJson.getString(JsonConfig.CATEGORY_ITEM_CATID)));
                        Log.e("og", "" + objJson.getString(JsonConfig.CATEGORY_ITEM_CATNAME));
                        Log.e("og", "" + objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGEURL));
                        Log.e("og", "" + objJson.getString(JsonConfig.CATEGORY_ITEM_CATID));
                        objItem.setItemCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_CATNAME));
                        objItem.setItemImageurl(objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGEURL));
                        objItem.setItemCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CATID));

                        itemWallpaperByCategories.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < itemWallpaperByCategories.size(); j++) {

                    ItemWallpaperByCategory objCategoryBean = itemWallpaperByCategories.get(j);

                    list_image.add(objCategoryBean.getItemImageurl());
                    str_list_image = list_image.toArray(str_list_image);

                    image_cat_name.add(objCategoryBean.getItemCategoryName());
                    str_image_cat_name = image_cat_name.toArray(str_image_cat_name);

                    image_id.add(objCategoryBean.getItemCatId());
                    str_image_id = image_id.toArray(str_image_id);

                }

                setAdapterToListView();
            }
        }
    }

    public void setAdapterToListView() {


            adapterWallpaperByCategory = new AdapterWallpaperByCategory(this, itemWallpaperByCategories, order);

            recyclerView.setAdapter(adapterWallpaperByCategory);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
    }

    @Override
    public void onStop() {
        super.onStop();
        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
    }

    @Override
    protected void onPause() {
        adViewOnPause();
        super.onPause();
        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adViewOnResume();
        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
    }

    @Override
    protected void onDestroy() {
        adViewOnDestroy();
        super.onDestroy();
        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadAdMobBannerAd() {
        if (config.isENABLE_ADMOB_BANNER_ADS ().equals("true") & config.getAdmob_banner_unit_id () != null) {
            StartAppAd.disableSplash();
            StartAppAd.disableAutoInterstitial();
            String banner_id = config.getAdmob_banner_unit_id ();
            adView = new AdView(getApplicationContext ());
            adView.setAdSize (AdSize.SMART_BANNER);
            adView.setAdUnitId(config.getAdmob_banner_unit_id());
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityWallpaperByCategory.this)).build();


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
       //     StartAppAd.showAd ( getApplicationContext ());

            Log.d("MainActivity", "AdMob Banner is Disabled");
        }
    }
    private void adViewOnPause() {
//        if (config.isENABLE_ADMOB_BANNER_ADS ()) {
//            adView.pause();
//        } else {
//            Log.d("MainActivity", "adView onPause is Disabled");
//        }
    }

    private void adViewOnResume() {
//        if (config.isENABLE_ADMOB_BANNER_ADS ()) {
//            adView.resume();
//        } else {
//            Log.d("MainActivity", "adView onResume is Disabled");
//        }
    }

    private void adViewOnDestroy() {
//        if (config.isENABLE_ADMOB_BANNER_ADS ()) {
//            adView.destroy();
//        } else {
//            Log.d("MainActivity", "adView onDestroy is Disabled");
//        }
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
    public class newTask extends AsyncTask<String, Void, String> {

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

                             loadInterstitialAd ();


                        } else {
                           // Toast.makeText(ActivityWallpaperByCategory.this, "ads disable for back end ", Toast.LENGTH_SHORT).show();
                            if (adsStaus.equals("enable"))
                               {
                              //  StartAppAd.showAd(getApplicationContext());
                        }}
                        if (config.ENABLE_ADMOB_BANNER_ADS.equals("true") && adsStaus.equals("enable")) {
                            loadAdMobBannerAd();
                        } else {
                            //  Toast.makeText(ActivityWallpaperByCategory.this,"Banner ad is disabled from backend",Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }


        }

    }
    private void loadInterstitialAd() {
        if(config.isENABLE_ADMOB_INTERSTITIAL_ADS () .equals("true")) {
            Log.d ( "TAG", "showAd" );
            StartAppAd.disableSplash ();
            StartAppAd.disableAutoInterstitial ();
            interstitialAd = new InterstitialAd( ActivityWallpaperByCategory.this );
            interstitialAd.setAdUnitId ( config.getAdmob_interstitial_unit_id ( ) );
            AdRequest adRequest = new AdRequest.Builder ( ).build ( );
            interstitialAd.loadAd ( adRequest );

        }else{
            // Toast.makeText ( getApplicationContext (),"ads disable from backend ",Toast.LENGTH_LONG ).show ();
         //   StartAppAd.enableAutoInterstitial();
        }
    }


}
