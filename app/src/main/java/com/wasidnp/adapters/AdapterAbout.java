package com.wasidnp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasidnp.R;
import com.wasidnp.activities.AboutUs;

import java.util.List;

public class AdapterAbout extends RecyclerView.Adapter<AdapterAbout.UserViewHolder> {

    private List<AboutUs.Data> dataList;
    private Context context;

    public AdapterAbout(List <AboutUs.Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lsv_item_about, null);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {

        AboutUs.Data data = dataList.get(position);

        holder.image.setImageResource(data.getImage());
        holder.title.setText(data.getTitle());
        holder.sub_title.setText(data.getSub_title());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 4) {
                    final String appName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                } else if (position == 5) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_more_apps))));
                } else if (position == 6) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.privacy_policy_url))));
                } else {
                    Log.d("Log", "Do Nothing!");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView sub_title;
        RelativeLayout relativeLayout;

        public UserViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            sub_title = (TextView) itemView.findViewById(R.id.sub_title);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.lyt_parent);
        }

    }

}