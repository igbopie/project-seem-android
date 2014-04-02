package com.seem.android.mockup1;

import android.app.Application;
import android.content.Context;

/**
 * Created by igbopie on 02/04/14.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}