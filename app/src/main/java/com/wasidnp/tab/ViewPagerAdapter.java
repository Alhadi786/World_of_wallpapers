package com.wasidnp.tab;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wasidnp.R;
import com.wasidnp.fragments.FragmentCategory;
import com.wasidnp.fragments.FragmentPopular;
import com.wasidnp.fragments.FragmentRecent;
import com.wasidnp.fragments.Fragmentfiltering;

import static xdroid.core.Global.getResources;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentCategory();

            case 1:
                return  new FragmentRecent();
            case 2:
                return new FragmentPopular();
            case 3:
                return new Fragmentfiltering();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return getResources().getString(R.string.tab_recent);
            case 1:
                return getResources().getString(R.string.tab_category);
            case 2:
                return getResources().getString(R.string.tab_popular);
            case 3:
                return getResources().getString(R.string.tab_trending);

        }
        return super.getPageTitle(position);
    }
}
