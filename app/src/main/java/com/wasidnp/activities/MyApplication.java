package com.wasidnp.activities;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;


public class MyApplication extends Application {

    private static FirebaseAnalytics mFirebaseAnalytics;
    private static MyApplication instance;
   /* SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;*/

    @Override
    public void onCreate() {
        super.onCreate();

        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        instance = this;
      /*  sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=sharedPreferences.edit();
        editor.putString("ads","enable");
        editor.apply();
*/
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public static MyApplication getInstance() {
        return instance;
    }

}