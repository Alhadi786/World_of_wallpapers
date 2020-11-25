package com.wasidnp.adapters;

import android.app.Activity;
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

import com.wasidnp.Config;
import com.wasidnp.R;
import com.wasidnp.activities.ActivityWallpaperByCategory;
import com.wasidnp.json.JsonConfig;
import com.wasidnp.models.ItemCategory;
import com.wasidnp.utilities.GDPR;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    private Context context;
    Config config =  new Config ();
    private List<ItemCategory> arrayItemCategory;
    ItemCategory itemCategory;
    private int row;
    private InterstitialAd interstitialAd;
    int counter = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt;
        public ImageView img_cat;
        public RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            txt = (TextView) view.findViewById(R.id.category_name);
            img_cat = (ImageView) view.findViewById(R.id.category_image);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        }

    }

    public AdapterCategory(Context context, List<ItemCategory> arrayItemCategory) {
        this.context = context;
        this.arrayItemCategory = arrayItemCategory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_category, parent, false);
        loadInterstitialAd();
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemCategory = arrayItemCategory.get(position);

        holder.txt.setText(itemCategory.getCategoryName());

        Picasso
                .with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/category/" + itemCategory.getCategoryImage().replace(" ", "%20"))
                .placeholder(R.drawable.ic_thumbnail)
                .resizeDimen(R.dimen.category_width, R.dimen.category_height)
                .centerCrop()
                .into(holder.img_cat);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemCategory = arrayItemCategory.get(position);

                String Catid = itemCategory.getCategoryId();

                JsonConfig.CATEGORY_ID = itemCategory.getCategoryId();
                Log.e("cat_id", "" + Catid);
// custom ads link
                JsonConfig.CATEGORY_TITLE = itemCategory.getCategoryName();
                JsonConfig.CATEGORY_ads_enable = itemCategory.getCategoryAds_enable ();
                JsonConfig.CATEGORY_ADS_URL = itemCategory.getCategoryAds_url ();
               // for(int i =0; arrayItemCategory.size (); )
                if( JsonConfig.CATEGORY_ads_enable.equals ( "true" ) ){
                    final String appName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(JsonConfig.CATEGORY_ADS_URL)));

                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=" + appName)));
                    }
                }else  {
                    Intent intent = new Intent ( context, ActivityWallpaperByCategory.class );
                    context.startActivity ( intent );

                    //showInterstitialAd ( );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemCategory.size();
    }

    private void loadInterstitialAd() {

        Log.d("TAG", "showAd");
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(config.getAdmob_interstitial_unit_id ());
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd((Activity) context)).build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void showInterstitialAd() {
        if (config.isENABLE_ADMOB_INTERSTITIAL_ADS ().equals("true")) {
            if (interstitialAd.isLoaded()) {
                if (counter == config.INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
    }

}