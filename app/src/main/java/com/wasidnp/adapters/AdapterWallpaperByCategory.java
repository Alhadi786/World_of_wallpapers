package com.wasidnp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.models.ItemWallpaperByCategory;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class AdapterWallpaperByCategory extends RecyclerView.Adapter<AdapterWallpaperByCategory.ViewHolder> {

    private Context context;
    private List<ItemWallpaperByCategory> arrayItemWallpaperByCategory;
    ItemWallpaperByCategory ItemWallpaperByCategory;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.item);
        }
    }
    public AdapterWallpaperByCategory(Context context, List<ItemWallpaperByCategory> arrayItemWallpaperByCategory,String order) {
        this.context = context;
        this.arrayItemWallpaperByCategory = arrayItemWallpaperByCategory;
       // this.order=order;
        if(order.equals("random"))
        {
            Collections.shuffle(this.arrayItemWallpaperByCategory);

        }
        else if(order.equals("asc"))
        {
            Collections.reverse(this.arrayItemWallpaperByCategory);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_grid_wallpaper, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ItemWallpaperByCategory = arrayItemWallpaperByCategory.get(position);

        Picasso
                .with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/" + ItemWallpaperByCategory.getItemImageurl().replace(" ", "%20"))
                .placeholder(R.drawable.ic_thumbnail)
                .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return arrayItemWallpaperByCategory.size();
    }

}