package com.wasidnp.activities;

import android.app.AlarmManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wasidnp.R;
import com.wasidnp.activities.Helper.SessionManager;
import com.wasidnp.adapters.AdapterAbout;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;

import me.rishabhkhanna.customtogglebutton.CustomToggleButton;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    TextView txt_cache,txt_clear;
    private Toolbar toolbar;
    AlarmManager alarmManager;
    LinearLayout linearLayout_not_daily,linearLayout_not_weekly;

    AdapterAbout adapterAbout;
    private MainActivity mainActivity;
    SessionManager mSessionManager;
    File cacheDirectory;
    CustomToggleButton notification,not_daily,not_weekly,not_sound,not_vibration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSessionManager = new SessionManager ( Setting.this );
        initViews();
        setupToolbar();
        checktoogle();
        cacheCalc();
        clickListener();

    }


    private void initViews() {
        toolbar = findViewById( R.id.toolbar);
        linearLayout_not_daily = findViewById ( R.id.linearLayout_not_daily );
        linearLayout_not_weekly =findViewById ( R.id.linearLayout_not_weekly );
        txt_cache = findViewById (R.id.txt_cache);
        txt_clear = findViewById (R.id.txt_clear);
        notification = findViewById (R.id.togle_btn_not);
        not_daily = findViewById (R.id.togle_btn_daily);
        not_weekly = findViewById (R.id.togle_btn_weekly);
        not_sound = findViewById (R.id.togle_btn_sound);
        not_vibration = findViewById (R.id.togle_btn_vibration);
    }
    private void setupToolbar() {
        toolbar.setTitle("Setting");
        toolbar.setBackgroundColor ( Color.BLACK );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void checktoogle() {
        if(mSessionManager.getNotEnable () == true){
            notification.setChecked ( true );
            linearLayout_not_weekly.setVisibility (View.VISIBLE );
            linearLayout_not_daily.setVisibility (View.VISIBLE );
        }else{
            notification.setChecked ( false );
            linearLayout_not_weekly.setVisibility (View.GONE );
            linearLayout_not_daily.setVisibility (View.GONE );

        }
        if(mSessionManager.getNotDailyEnable () == true){
            not_daily.setChecked ( true );
        }if(mSessionManager.getNotWeeklyEnable () == true){
            not_weekly.setChecked ( true );
        }if(mSessionManager.getNotVibEnable () == true){
            not_vibration.setChecked ( true );
        }if(mSessionManager.getNotSoundEnable () == true){
            not_sound.setChecked ( true );
        }
    }




    private void clickListener() {
            not_daily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    notification.setChecked(true);
                }
            });
            txt_clear.setOnClickListener(this);
            notification.setOnClickListener(this);
            not_vibration.setOnClickListener(this);
            not_sound.setOnClickListener(this);
            not_weekly.setOnClickListener(this);
            not_daily.setOnClickListener(this);
        }

    private void cacheCalc() {
        final int cacheSize = 4 * 1024 * 1024; // 4MiB
        final LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>( cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                txt_cache.setText ( value.getByteCount());
                return value.getByteCount();

            }
        };
    }

        @Override
        public void onClick(View view){
        int id = view.getId ();
        switch (id){
            case  R.id.txt_clear:
                txt_cache.setText ("0.0MB");
                break;
            case  R.id.togle_btn_not:
                notification.setChecked (true);
                if(mSessionManager.getNotEnable () == false) {
                    mSessionManager.setNotEnable ( true );
                    linearLayout_not_daily.setVisibility ( View.VISIBLE );
                    linearLayout_not_weekly.setVisibility ( View.VISIBLE );
                    notification.setChecked( false );
                    not_daily.setChecked ( true );
                    mSessionManager.setNotDailyEnable ( true );
                    mSessionManager.setNotWeeklyEnable ( true );
                    not_weekly.setChecked ( true );
                    break;
                }else if(mSessionManager.getNotEnable () == true){

                    new LovelyStandardDialog( this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            //  .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColorRes(R.color.colorPrimary)
                            //  .setIcon(R.drawable.reward)
                            .setTitle("Wait! Disable Completly? ")
                            .setMessage(R.string.notification)
                            .setPositiveButtonColorRes ( R.color.btn_alert )
                            .setNegativeButtonColorRes ( R.color.btn_alert )
                            .setPositiveButton("TURN OFF ENTIRELY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    notification.setChecked ( false );
                                    not_daily.setChecked ( false );
                                    not_weekly.setChecked ( false );
                                    linearLayout_not_daily.setVisibility ( View.GONE );
                                    linearLayout_not_weekly.setVisibility ( View.GONE );
                                    mSessionManager.setNotEnable ( false );
                                    mSessionManager.setNotWeeklyEnable ( false );
                                    mSessionManager.setNotDailyEnable ( false );
                                    notification.setChecked ( false );
                                }

                            })

                            .setNegativeButton ( "KEEP WEEKLY NOTIFICATION", new View.OnClickListener ( ) {
                                @Override
                                public void onClick(View view) {
                                    not_weekly.setChecked ( true );
                                    notification.setChecked ( true );
                                    mSessionManager.setNotEnable ( true );
                                    mSessionManager.setNotWeeklyEnable ( true );
                                    if(mSessionManager.getNotDailyEnable () == true){
                                        not_daily.setChecked ( false );
                                    }
                                }
                            } )
                            .show();
                }
                break;
            case  R.id.togle_btn_daily:
                if(mSessionManager.getNotDailyEnable () == false) {
                    mSessionManager.setNotDailyEnable ( true );
                    not_daily.setChecked ( true );
                    notification.setChecked ( true );
//                    Intent intent = new Intent( getActivity (), MainActivity.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast( getActivity (), 2020, intent, 0);
//                    alarmManager.setRepeating(alarmManager.RTC_WAKEUP, System.currentTimeMillis (), alarmManager.INTERVAL_DAY*7, pendingIntent);
                }else if(mSessionManager.getNotDailyEnable () == true){
                    mSessionManager.setNotDailyEnable ( false );
                    not_daily.setChecked ( false );

                }
                break;

            case  R.id.togle_btn_weekly:
                if(mSessionManager.getNotWeeklyEnable () == false ) {
                    mSessionManager.setNotWeeklyEnable ( true );
                    not_weekly.setChecked ( true );


                }else if(mSessionManager.getNotWeeklyEnable () == true){
                    mSessionManager.setNotWeeklyEnable ( false );
                    not_weekly.setChecked ( false );
                    if(mSessionManager.getNotDailyEnable () == false){
                        notification.setChecked ( false );
                    }else if(mSessionManager.getNotDailyEnable () == true){
                        notification.setChecked ( true );

                    }
                }
                break;
            case  R.id.togle_btn_sound:
                if(mSessionManager.getNotSoundEnable () == false) {
                    mSessionManager.setNotSoundEnable ( true );
                    not_sound.setChecked ( true );
                }else if(mSessionManager.getNotEnable () == true){
                    mSessionManager.setNotSoundEnable ( false );
                    not_sound.setChecked ( false );
                }
                break;
            case  R.id.togle_btn_vibration:
                if(mSessionManager.getNotVibEnable () == false) {
                    mSessionManager.setNotVibEnable ( true );
                    not_vibration.setChecked ( true );
                }else if(mSessionManager.getNotVibEnable () == true){
                    mSessionManager.setNotVibEnable ( false );
                    not_vibration.setChecked ( false );
                }
                break;

        }
    }

}


