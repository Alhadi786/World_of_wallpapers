package com.wasidnp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.wasidnp.R;
import com.wasidnp.adapters.AdapterAbout;

import java.util.ArrayList;
import java.util.List;

public class AboutUs extends AppCompatActivity {
    RecyclerView recyclerView;
    private Toolbar toolbar;
    AdapterAbout adapterAbout;
    private MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setupToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.rvAllUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterAbout = new AdapterAbout(getDataInformation(), getApplicationContext());
        recyclerView.setAdapter(adapterAbout);
    }



    private List<Data> getDataInformation() {

        List<Data> data = new ArrayList<>();

        data.add(new Data(
                R.drawable.ic_other_appname,
                getResources().getString(R.string.about_app_name),
                getResources().getString(R.string.app_name)
        ));

        data.add(new Data(
                R.drawable.ic_other_build,
                getResources().getString(R.string.about_app_version),
                getResources().getString(R.string.sub_about_app_version)
        ));

        data.add( new Data(
                R.drawable.ic_other_email,
                getResources().getString(R.string.about_app_email),
                getResources().getString(R.string.sub_about_app_email)
        ));

        data.add(new Data(
                R.drawable.ic_other_copyright,
                getResources().getString(R.string.about_app_copyright),
                getResources().getString(R.string.sub_about_app_copyright)
        ));

//        data.add(new Data(
//                R.drawable.ic_other_rate,
//                getResources().getString(R.string.about_app_rate),
//                getResources().getString(R.string.sub_about_app_rate)
//        ));
//
//        data.add(new Data(
//                R.drawable.ic_other_more,
//                getResources().getString(R.string.about_app_more),
//                getResources().getString(R.string.sub_about_app_more)
//        ));
//
//        data.add(new Data(
//                R.drawable.ic_other_privacy,
//                getResources().getString(R.string.about_app_privacy_policy),
//                getResources().getString(R.string.sub_about_app_privacy_policy)
//        ));

        return data;
    }

    public class Data {
        private int image;
        private String title;
        private String sub_title;

        public int getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getSub_title() {
            return sub_title;
        }

        public Data(int image, String title, String sub_title) {
            this.image = image;
            this.title = title;
            this.sub_title = sub_title;
        }
    }



    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.drawer_about));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
