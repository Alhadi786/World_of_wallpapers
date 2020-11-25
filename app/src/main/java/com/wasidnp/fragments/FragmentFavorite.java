package com.wasidnp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.activities.ActivitySlideImage;
import com.wasidnp.adapters.AdapterFavorite;
import com.wasidnp.database.DatabaseHandlerFavorite;
import com.wasidnp.database.DatabaseHandlerFavorite.DatabaseManager;
import com.wasidnp.json.JsonUtils;
import com.wasidnp.models.ItemRecent;
import com.wasidnp.utilities.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
       // setRetainInstance(true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.lyt_no_favorite);
        databaseHandlerFavorite = new DatabaseHandlerFavorite(getActivity());
        databaseManager = DatabaseManager.INSTANCE;
        databaseManager.init(getActivity());
        jsonUtils = new JsonUtils(getActivity());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Config.NUM_OF_COLUMNS));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        listItem = databaseHandlerFavorite.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), listItem);
        recyclerView.setAdapter(adapterFavorite);
        if (listItem.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getActivity(), ActivitySlideImage.class);
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

                        Intent intent = new Intent(getActivity(), ActivitySlideImage.class);
                        intent.putExtra("POSITION_ID", position);
                        intent.putExtra("IMAGE_ARRAY", str_list_image);
                        intent.putExtra("IMAGE_CATNAME", str_image_cat_name);

                        startActivity(intent);
                    }
                }, 400);

            }
        }));

        return rootView;
    }

    public void onDestroyView() {
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        listItem = databaseHandlerFavorite.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), listItem);
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
            databaseManager.init(getActivity());
        } else if (databaseManager.isDatabaseClosed()) {
            databaseManager.init(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
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

}
