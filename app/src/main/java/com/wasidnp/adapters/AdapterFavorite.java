package com.wasidnp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.models.ItemRecent;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.ViewHolder> {

    private Context context;
    private List<ItemRecent> arrayPojo;
    ItemRecent pojo;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.item);

        }

    }

    public AdapterFavorite(Context context, List<ItemRecent> arrayPojo) {
        this.context = context;
        this.arrayPojo = arrayPojo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(
                MaterialRippleLayout.on(inflater.inflate(R.layout.lsv_item_grid_wallpaper, parent, false))
                        .rippleOverlay(true)
                        .rippleAlpha(0.2f)
                        .rippleColor(0xFF585858)
                        .rippleHover(true)
                        .create()
        );

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        pojo = arrayPojo.get(position);

        Picasso
                .with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/" + pojo.getImageurl().replace(" ", "%20"))
                .placeholder(R.drawable.ic_thumbnail)
                .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return arrayPojo.size();
    }

}