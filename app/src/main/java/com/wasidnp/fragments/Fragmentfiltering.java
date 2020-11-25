package com.wasidnp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.activities.ActivitySlideImage;
import com.wasidnp.adapters.AdapterRecent;
import com.wasidnp.database.DatabaseHandlerDownload;
import com.wasidnp.database.DatabaseHandlerFavorite;
import com.wasidnp.database.DatabaseHandlerMostViewed;
import com.wasidnp.database.DatabaseHandlerRecent;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemRecent;
import com.wasidnp.models.Pojo;
import com.wasidnp.utilities.GDPR;
import com.wasidnp.utilities.ItemOffsetDecoration;
import com.startapp.android.publish.adsCommon.StartAppAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragmentfiltering extends Fragment {

    RecyclerView recyclerView;
    List<ItemRecent> listItemRecent;
    List<Pojo> listItemFav;
    AdapterRecent AdapterRecent;
    ArrayList<String> allListImage, allListImageCatName;
    String[] allArrayImage, allArrayImageCatName;
    private ItemRecent itemRecent;
    private Pojo pojo;
   private ProgressBar progressBar;
    JsonUtils jsonUtils;
    public DatabaseHandlerRecent databaseHandlerRecent;
    public DatabaseHandlerMostViewed databaseHandlerMostViewed;
    public DatabaseHandlerDownload databaseHandlerDownload;
    public DatabaseHandlerFavorite databaseHandlerFavorite;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private InterstitialAd interstitialAd;
    int counter = 1;
    Config config;



    String selected = "today_hits";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_trend_wallpaper, container, false);

        setHasOptionsMenu(true);

        //loadInterstitialAd();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.latest_grid);
       progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_trend);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_trend_wallpaper);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Config.NUM_OF_COLUMNS));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        databaseHandlerRecent = new DatabaseHandlerRecent(getActivity());
        databaseHandlerDownload = new DatabaseHandlerDownload (getActivity());
        databaseHandlerFavorite = new DatabaseHandlerFavorite (getActivity());
        databaseHandlerMostViewed = new DatabaseHandlerMostViewed (getActivity());

        config = new Config ();
        listItemRecent = new ArrayList<ItemRecent>();
        listItemFav = new ArrayList<Pojo>();
        allListImage = new ArrayList<String>();
        allListImageCatName = new ArrayList<String>();

        allArrayImage = new String[allListImage.size()];
        allArrayImageCatName = new String[allListImageCatName.size()];

        jsonUtils = new JsonUtils(getActivity());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", allArrayImage);
                        intent.putExtra("IMAGE_CATNAME", allArrayImageCatName);

                        if (JsonUtils.isNetworkAvailable( getActivity ())) {
                            String image_url = allArrayImage[position];
                           new MyTasCountAdded ().execute( Config.ADMIN_PANEL_URL +  "/count_added.php?action=action"+"&"+"image_url="+image_url);
                        } else {
                            Toast.makeText ( getActivity (), "some thing wrong with ads data", Toast.LENGTH_SHORT ).show ( );
                            StartAppAd.showAd ( getActivity () );
                        }

                        startActivity(intent);
                        //showInterstitialAd();

                    }
                }, 400);
            }

            @Override
            public void onLongClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", allArrayImage);
                        intent.putExtra("IMAGE_CATNAME", allArrayImageCatName);

                        startActivity(intent);

                        //showInterstitialAd();

                    }
                }, 1000);

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refereshView();
            }
        });

        if (JsonUtils.isNetworkAvailable(getActivity())) {

            new MyTask().execute(Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);
        } else {
            listItemRecent = databaseHandlerRecent.getAllData();
            if (listItemRecent.size() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_first_load), Toast.LENGTH_SHORT).show();
            }
            setAdapterToListView();
            for (int j = 0; j < listItemRecent.size(); j++) {

                itemRecent = listItemRecent.get(j);

                allListImage.add(itemRecent.getImageurl());
                allArrayImage = allListImage.toArray(allArrayImage);
                allListImageCatName.add(itemRecent.getCategoryName());
                allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);

            }
        }

        return rootView;
    }

    public void clearData() {
        int size = this.listItemRecent.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.listItemRecent.remove(0);
            }

            AdapterRecent.notifyItemRangeRemoved(0, size);
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
                Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.LATEST_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemRecent objItem = new ItemRecent();

                        databaseHandlerRecent.AddtoFavoriteLatest(new ItemRecent(objJson.getString(JsonConfig.LATEST_IMAGE_CATEGORY_NAME), objJson.getString(JsonConfig.LATEST_IMAGE_URL)));

                        objItem.setCategoryName(objJson.getString(JsonConfig.LATEST_IMAGE_CATEGORY_NAME));
                        objItem.setImageurl(objJson.getString(JsonConfig.LATEST_IMAGE_URL));

                        listItemRecent.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < listItemRecent.size(); j++) {

                    itemRecent = listItemRecent.get(j);
                    allListImage.add(itemRecent.getImageurl());
                    allArrayImage = allListImage.toArray(allArrayImage);

                    allListImageCatName.add(itemRecent.getCategoryName());
                    allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);

                }

                setAdapterToListView();
            }

        }
    }

    public void setAdapterToListView() {

        AdapterRecent = new AdapterRecent(getActivity(), listItemRecent);

        recyclerView.setAdapter(AdapterRecent);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_menu, menu); //is this menu is hwoing?no
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
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId( String.valueOf ( R.string.admob_interstitial_unit_id ) );
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(getActivity())).build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.desc) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();
            listItemRecent = new ArrayList<ItemRecent>();
            allListImage = new ArrayList<String>();
            allListImageCatName = new ArrayList<String>();
            selected = "desc";
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?latest=&orderby="+selected);

//            refereshView();
            return true;
        }
        else if (id == R.id.asc) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();
            listItemRecent = new ArrayList<ItemRecent>();
            allListImage = new ArrayList<String>();
            allListImageCatName = new ArrayList<String>();
            selected = "asc";
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php?latest=&orderby="+selected);
//            refereshView();
            return true;
        }
//        else if (id == R.id.random) {
//            selected = "random";
//            refereshView();
//            return true;
//        }
        else if (id == R.id.today) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();
            listItemRecent = new ArrayList<ItemRecent>();
            allListImage = new ArrayList<String>();
            allListImageCatName = new ArrayList<String>();
            selected = "today_hits";
            new MyTaskFiltering ().execute( Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);
            return true;
        }else if (id == R.id.weekly) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();
            selected = "weekly_hits";
            new MyTaskFiltering ().execute( Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);
            return true;
        }else if (id == R.id.monthly) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();

            selected = "monthly_hits";
            new MyTaskFiltering ( ).execute ( Config.ADMIN_PANEL_URL + "/filtering.php?action=action" + "&" + "filtering=" + selected );

            return true;
        }else if (id == R.id.yearly) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();

            selected = "yearly_hits";
            new MyTaskFiltering ().execute( Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);

            return true;
       }else if (id == R.id.mostviewed) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();

            selected = "mostviewed";
            new MyTaskFiltering ().execute( Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);

            /*selected = "mostview";
            listItemRecent.clear ();
            new Handler (  ).postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    listItemRecent = databaseHandlerMostViewed.getAllMostViewedData ();
                    for (int j = 0; j < listItemRecent.size(); j++) {

                        itemRecent = listItemRecent.get ( j );

                        allListImage.add ( itemRecent.getImageurl ( ) );
                        allArrayImage = allListImage.toArray ( allArrayImage );
                        allListImageCatName.add ( itemRecent.getCategoryName ( ) );
                        allArrayImageCatName = allListImageCatName.toArray ( allArrayImageCatName );
                        AdapterRecent = new AdapterRecent ( getActivity ( ), listItemRecent );

                        recyclerView.setAdapter ( AdapterRecent );
                    }
                }
            },3000 );*/
            return true;
        }else if (id == R.id.download) {
            selected = "download";
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();

            new Handler (  ).postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    listItemRecent = databaseHandlerDownload.getAllDownloadData ();
                    for (int j = 0; j < listItemRecent.size(); j++) {

                        itemRecent = listItemRecent.get ( j );

                        allListImage.add ( itemRecent.getImageurl ( ) );
                        allArrayImage = allListImage.toArray ( allArrayImage );
                        allListImageCatName.add ( itemRecent.getCategoryName ( ) );
                        allArrayImageCatName = allListImageCatName.toArray ( allArrayImageCatName );
                        AdapterRecent = new AdapterRecent ( getActivity ( ), listItemRecent );

                        recyclerView.setAdapter ( AdapterRecent );
                    }
                }
            },3000 );
            return true;
        }else if (id == R.id.favrite) {
            listItemRecent.clear();
            allListImage.clear();
            allListImageCatName.clear();
          new Handler (  ).postDelayed ( new Runnable ( ) {
              @Override
              public void run() {
                  listItemRecent.clear ();
                  listItemRecent = databaseHandlerFavorite.getAllData();
                  AdapterRecent = new AdapterRecent ( getActivity(), listItemRecent);
                  listItemRecent = databaseHandlerFavorite.getAllData ();
                           AdapterRecent = new AdapterRecent(getActivity(), listItemRecent);
                           recyclerView.setAdapter(AdapterRecent);

              }
          },3000 );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    void refereshView(){
        listItemRecent.clear();
        allListImage.clear();
        allListImageCatName.clear();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    swipeRefreshLayout.setRefreshing(false);
                    clearData();
                    selected = "today_hits";
                    new MyTask ( ).execute ( Config.ADMIN_PANEL_URL +  "/filtering.php?action=action"+"&"+"filtering="+selected);

                 } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), getResources().getString(R.string.refresh_alert), Toast.LENGTH_SHORT).show();
              }

            }
        }, 3000);
    }

    private class MyTaskFiltering extends AsyncTask<String, Void, String> {

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
                Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    allArrayImageCatName = new String[0];
                    allArrayImage = new String[0];
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.LATEST_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemRecent objItem = new ItemRecent();
                        databaseHandlerRecent.AddtoFavoriteLatest(new ItemRecent(objJson.getString(JsonConfig.LATEST_IMAGE_CATEGORY_NAME), objJson.getString(JsonConfig.LATEST_IMAGE_URL)));
                        objItem.setCategoryName(objJson.getString(JsonConfig.LATEST_IMAGE_CATEGORY_NAME));
                        objItem.setImageurl(objJson.getString(JsonConfig.LATEST_IMAGE_URL));

                        listItemRecent.add(objItem);
                        allListImage.add(objItem.getImageurl());
                        allArrayImage = allListImage.toArray(allArrayImage);
                        allListImageCatName.add(objItem.getCategoryName());
                        allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                setAdapterToListView();
            }
        }
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
                Toast.makeText(getActivity (), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

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

}
