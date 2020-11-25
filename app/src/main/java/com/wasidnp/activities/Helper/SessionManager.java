package com.wasidnp.activities.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hamy .
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "iqra";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    // private static final String KEY_NAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_MAC = "mac";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

// notification_enable
    public boolean setNotEnable(boolean value) {
        editor.putBoolean ( KEY_TOKEN, value );
        // commit changes
        editor.commit ( );
        return value;
    }
    public boolean getNotEnable() {
        return pref.getBoolean (KEY_TOKEN, false);
    }

// notification_enable_sound
    public boolean setNotSoundEnable(boolean value) {
        editor.putBoolean ( KEY_TOKEN, value );
        // commit changes
        editor.commit ( );

        return value;
    }
    public boolean getNotSoundEnable() {
        return pref.getBoolean (KEY_TOKEN, false);
    }

// notification_enable_vibration
    public boolean setNotVibEnable(boolean value) {
        editor.putBoolean ( KEY_TOKEN, value );
        // commit changes
        editor.commit ( );

        return value;
    }
        public boolean getNotVibEnable() {
            return pref.getBoolean (KEY_TOKEN, false);
    }

// notification_enable_vibration
    public boolean setNotDailyEnable(boolean value) {
        editor.putBoolean ( KEY_TOKEN, value );
        // commit changes
        editor.commit ( );

        return value;
    }
    public boolean getNotDailyEnable() {
        return pref.getBoolean (KEY_TOKEN, false);
    }

    // notification_enable_vibration
    public boolean setNotWeeklyEnable(boolean value) {
        editor.putBoolean ( KEY_TOKEN, value );
        // commit changes
        editor.commit ( );

        return value;
    }
    public boolean getNotWeeklyEnable() {
        return pref.getBoolean (KEY_TOKEN, false);
    }

}
