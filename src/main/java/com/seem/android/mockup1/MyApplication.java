package com.seem.android.mockup1;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

    public static boolean isLoggedIn(){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(GlobalVars.SHARED_PREF_AUTHENTICATED,false);
    }
    public static String getToken(){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(GlobalVars.SHARED_PREF_TOKEN,"");
    }
    public static String getUsername(){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(GlobalVars.SHARED_PREF_USERNAME,"");
    }

    public static String getPassword(){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(GlobalVars.SHARED_PREF_PASSWORD,"");
    }

    public static void login(String username,String password,String token){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GlobalVars.SHARED_PREF_USERNAME,username);
        editor.putString(GlobalVars.SHARED_PREF_PASSWORD,password);
        editor.putString(GlobalVars.SHARED_PREF_TOKEN,token);
        editor.putBoolean(GlobalVars.SHARED_PREF_AUTHENTICATED,true);
        editor.commit();
    }
    public static void logout(){
        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(GlobalVars.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GlobalVars.SHARED_PREF_USERNAME,null);
        editor.putString(GlobalVars.SHARED_PREF_PASSWORD,null);
        editor.putString(GlobalVars.SHARED_PREF_TOKEN,null);
        editor.putBoolean(GlobalVars.SHARED_PREF_AUTHENTICATED,false);
        editor.commit();
    }
}