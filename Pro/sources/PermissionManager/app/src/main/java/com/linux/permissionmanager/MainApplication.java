package com.linux.permissionmanager;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppSettings.init(this);
    }
}
