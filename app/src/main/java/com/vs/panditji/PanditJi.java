package com.vs.panditji;

import android.app.Application;
import android.content.SharedPreferences;

public class PanditJi extends Application {

    private static PanditJi instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public SharedPreferences getSharedPrefs(){
        return getSharedPreferences (Constants.SHARED_PREFS, MODE_PRIVATE);
    }

    public static PanditJi getInstance(){
        return instance;
    }

}
