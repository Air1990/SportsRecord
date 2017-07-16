package com.examp.airhome.gesturelock;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by airhome on 2015/12/6.
 */
public class Preferences {
    private static final String SF_NAME = "com.airhome.gesturelock";
    private static final String IS_LOCKED = "isLocked";
    private static final String PASSWORD = "password";

    private static SharedPreferences preferences;

    private static SharedPreferences getInstance(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static GestureLockView.LockState getLockState(Context context) {
        SharedPreferences sf = getInstance(context);
        return sf.getBoolean(IS_LOCKED, false) ? GestureLockView.LockState.UNLOCK :
                GestureLockView.LockState.SET_LOCK;
    }

    public static void setLockState(Context context, boolean isLocked) {
        SharedPreferences sf = getInstance(context);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(IS_LOCKED, isLocked);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences sf = getInstance(context);
        return sf.getString(PASSWORD, "");
    }

    public static void savePassword(Context context, String password) {
        SharedPreferences sf = getInstance(context);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString(PASSWORD, password);
        editor.apply();
    }
}
