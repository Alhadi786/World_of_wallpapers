package com.wasidnp.fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.adapters.AdapterCategory;
import com.wasidnp.database.DatabaseHandlerCategory;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentCategory extends Fragment {

    RecyclerView recyclerView;
    List<ItemCategory> arrayItemCategory;
    private ArrayList<ItemCategory> arrayListItemCategory;
    AdapterCategory adapterCategory;
    private ItemCategory itemCategory;
    public DatabaseHandlerCategory databaseHandlerCate;

    private ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout = null;


    @Override
    public void onResume() {
      // new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
        super.onResume ( );
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        setHasOptionsMenu(true);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        progressBar=(ProgressBar)rootView.findViewById(R.id.progressBar_category);
      //  circularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.progress_circular_category);
       // int animationDuration = 2500; // 2500ms = 2,5s
       // circularProgressBar.setProgressWithAnimation(65, animationDuration);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemCategory = new ArrayList<ItemCategory>();
        this.arrayListItemCategory = new ArrayList<ItemCategory>();

        databaseHandlerCate = new DatabaseHandlerCategory(getActivity());



        setHasOptionsMenu(true);
        setRetainInstance(true);





        // Using to refresh webpage when user swipes the screen
      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {



                if (JsonUtils.isNetworkAvailable(getActivity ())) {
                    arrayItemCategory.clear();
                    arrayListItemCategory.clear();
                     swipeRefreshLayout.setRefreshing(false);
                    clearData();
                    new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.refresh_alert), Toast.LENGTH_SHORT).show();
                }


            }
        });

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
        } else {
            arrayItemCategory = databaseHandlerCate.getAllData();
            if (arrayItemCategory.size() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_first_load), Toast.LENGTH_SHORT).show();
            }
            setAdapterToListView();
        }
        return rootView;
    }

    private void refereshView() {
        arrayItemCategory.clear();
        arrayListItemCategory.clear();
        if (JsonUtils.isNetworkAvailable(getActivity ())) {
            swipeRefreshLayout.setRefreshing(false);
             clearData();
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), getResources().getString(R.string.refresh_alert), Toast.LENGTH_SHORT).show();
        }


        }


    public void clearData() {
        int size = this.arrayItemCategory.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemCategory.remove(0);
            }
            adapterCategory.notifyItemRangeRemoved(0, size);
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
            progressBar.setVisibility(View.INVISIBLE);


            if (null == result || result.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject  objJson = jsonArray.getJSONObject(i);

                        ItemCategory objItem = new ItemCategory();

                        databaseHandlerCate.AddtoFavoriteCate(new ItemCategory(
                                objJson.getString(JsonConfig.CATEGORY_CID),
                                objJson.getString(JsonConfig.CATEGORY_NAME),
                                objJson.getString(JsonConfig.CATEGORY_IMAGE_URL)));
                                objJson.getString ( JsonConfig.CATEGORY_ads_enable );
                                objJson.getString ( JsonConfig.CATEGORY_ADS_URL );

                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getString(JsonConfig.CATEGORY_CID));
                        objItem.setCategoryImage(objJson.getString(JsonConfig.CATEGORY_IMAGE_URL));
                        objItem.setCategoryAds_enable (objJson.getString(JsonConfig.CATEGORY_ads_enable));
                        objItem.setCategoryAds_url (objJson.getString(JsonConfig.CATEGORY_ADS_URL));
                        arrayItemCategory.add(objItem);
                        arrayListItemCategory.addAll(arrayItemCategory);
                        setAdapterToListView();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

        }
    }

    public void setAdapterToListView() {
        adapterCategory = new AdapterCategory(getActivity(), arrayItemCategory);
        recyclerView.setAdapter(adapterCategory);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText.toLowerCase(Locale.getDefault());
                arrayItemCategory.clear();
                filter(text);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayItemCategory.clear();
        if (charText.length() == 0) {
           arrayItemCategory.addAll(arrayListItemCategory);

        } else {
            for (ItemCategory filter : arrayListItemCategory) {
                if (filter.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayItemCategory.add(filter);
                }
            }
        }
        setAdapterToListView();
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

}
