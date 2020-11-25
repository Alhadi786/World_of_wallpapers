package com.wasidnp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.activities.ActivitySlideImage;
import com.wasidnp.adapters.AdapterTrend;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemTrend;
import com.wasidnp.database.DatabaseHandlerTrend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentTrend extends Fragment {

    RecyclerView gridView;
    List<Object> listItemRecent;
    SwipeRefreshLayout swipeRefreshLayout;
    AdapterTrend adapterRecent;
    ArrayList<String> allListImage, allListImageCatName;
    String[] allArrayImage,images, allArrayImageCatName;

    private int columnWidth;
    private ItemTrend itemRecent;
    JsonUtils jsonUtils;
    private ProgressBar progressBar;

    public DatabaseHandlerTrend databaseHandlerTrend;
    List<ItemTrend> listItemTrend;
    private String selected="random";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_trend_wallpaper, container, false);

        setHasOptionsMenu(true);
           swipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout_trend_wallpaper);
         swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);
        gridView = rootView.findViewById(R.id.latest_grid);


        progressBar=(ProgressBar)rootView.findViewById(R.id.progressBar_trend);
        databaseHandlerTrend = new DatabaseHandlerTrend(getActivity());

        listItemRecent = new ArrayList<Object>();

        listItemTrend = new ArrayList<ItemTrend>();
        allListImage = new ArrayList<String>();
        allListImageCatName = new ArrayList<String>();

        allArrayImage = new String[allListImage.size()];
        images = new String[allListImage.size()];
        allArrayImageCatName = new String[allListImageCatName.size()];


        jsonUtils = new JsonUtils(getActivity());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });



        setAdapterToListView();
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new FragmentTrend.MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
            //new MyTask().execute(Config.ADMIN_PANEL_URL +  "/count_added.php?action=action"+"&"+"image_url="+allArrayImage);
        } else {
            listItemTrend = databaseHandlerTrend.getAllData();
            if (listItemTrend.size() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_first_load), Toast.LENGTH_SHORT).show();
            }
            adapterRecent.notifyDataSetChanged();

            for (int j = 0; j < listItemTrend.size(); j++) {

                itemRecent = listItemTrend.get(j);

                allListImage.add(itemRecent.getImageurl());
                allArrayImage = allListImage.toArray(allArrayImage);
            }
        }



        return rootView;
    }

    private void refreshView() {
        listItemRecent.clear();
        listItemTrend.clear();
        allListImage.clear();
        allListImageCatName.clear();

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            clearData();
            new FragmentTrend.MyTask().execute(Config.ADMIN_PANEL_URL +  "/count_added.php?action=action"+"&"+"image_url="+allArrayImage);
        } else {
            listItemTrend = databaseHandlerTrend.getAllData();
            if (listItemTrend.size() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.network_first_load), Toast.LENGTH_SHORT).show();
            }
            adapterRecent.notifyDataSetChanged();

            for (int j = 0; j < listItemTrend.size(); j++) {

                itemRecent = listItemTrend.get(j);

                allListImage.add(itemRecent.getImageurl());
                allArrayImage = allListImage.toArray(allArrayImage);
            }
        }

    }
    public void clearData() {
        int size = this.listItemRecent.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.listItemRecent.remove(0);
            }

            adapterRecent.notifyItemRangeRemoved(0, size);
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

                Toast.makeText(getActivity(),"Data is Retrieve",Toast.LENGTH_SHORT).show();

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.TREND_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i != 0 && i % 16 == 0) {
                            listItemRecent.add("Ad");
                        }
                        objJson = jsonArray.getJSONObject(i);

                        ItemTrend objItem = new ItemTrend();


                        databaseHandlerTrend.AddtoFavoriteLatest(new ItemTrend(objJson.getString(JsonConfig.LATEST_IMAGE_URL)));

                        objItem.setImageurl(objJson.getString(JsonConfig.LATEST_IMAGE_URL));
                        listItemRecent.add(objItem);
                       // adapterRecent.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < listItemRecent.size(); j++) {
                    if ((listItemRecent.get(j)) instanceof ItemTrend) {
                        itemRecent = (ItemTrend) listItemRecent.get(j);

                        allListImage.add(itemRecent.getImageurl());
                        allArrayImage = allListImage.toArray(allArrayImage);
                        images = allListImage.toArray(images);
                    } else{


                        allListImage.add("");
                        allArrayImage = allListImage.toArray(allArrayImage);

                        allListImageCatName.add("");
                        allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);

                    }
                }
                adapterRecent.notifyDataSetChanged();

            }

        }
        }



    public void setAdapterToListView() {

        gridView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int resId;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resId = R.layout.lsv_item_grid_wallpaper;
        } else {
            resId = R.layout.lsv_item_grid_wallpaper;
        }
        adapterRecent = new AdapterTrend(getActivity(), resId, listItemRecent, new AdapterTrend.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), ActivitySlideImage.class);
                intent.putExtra("POSITION_ID", position);
                intent.putExtra("IMAGE_ARRAY", allArrayImage);
                intent.putExtra("IMAGE_CATNAME", allArrayImageCatName);
                intent.putExtra("IMAGES",images);

                startActivity(intent);
            }
        });


        gridView.setAdapter(adapterRecent);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

}
