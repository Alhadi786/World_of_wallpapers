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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.ViewHolder> {

    private Context context;
    private List<ItemRecent> arrayItemRecent;
    ItemRecent itemRecent;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.item);
        }

    }

    public AdapterRecent(Context context, List<ItemRecent> arrayItemRecent) {
        this.context = context;
        this.arrayItemRecent = arrayItemRecent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_grid_wallpaper, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemRecent = arrayItemRecent.get(position);

        Picasso
                .with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/" + itemRecent.getImageurl().replace(" ", "%20"))
                .placeholder(R.drawable.ic_thumbnail)
                .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return arrayItemRecent.size();
    }

}