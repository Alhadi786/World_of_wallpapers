package com.wasidnp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wasidnp.R;
import com.wasidnp.models.ItemRecent;
import com.wasidnp.models.ItemTrend;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterTrend extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_RECENT = 0;
    private static final int ITEM_AD = 1;
    private Context context;
    private List<Object> itemsLatest;
    private int row;
    private AdapterTrend.OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener{
        public void onItemClick(int position);
    }

    public AdapterTrend(Context context, int resource, List<Object> arrayList, AdapterTrend.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.row = resource;
        this.itemsLatest = arrayList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsLatest.get(position) instanceof ItemRecent) {
            return ITEM_RECENT;
        } else if (itemsLatest.get(position) instanceof String) {
            return ITEM_AD;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(row, parent, false);
        return new AdapterTrend.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_RECENT) {
            if (holder instanceof AdapterTrend.ViewHolder) {
                ((AdapterTrend.ViewHolder) holder).bind(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsLatest != null ? itemsLatest.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        ItemTrend object;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item);

        }

        void bind(int position) {
            object = (ItemTrend) itemsLatest.get(position);
            Picasso
                    .with(context)
                    .load(object.getImageurl())
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(imageView);
        }
    }


}
