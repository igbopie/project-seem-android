package com.seem.android.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.fragments.ConversationViewFragmentFragment;
import com.seem.android.fragments.EditUserFragment;
import com.seem.android.fragments.HomeFragment;
import com.seem.android.fragments.LoginFragment;
import com.seem.android.fragments.SeemItemsListFragment;
import com.seem.android.fragments.SeemListFragment;
import com.seem.android.fragments.SignUpFragment;
import com.seem.android.fragments.UserProfileFragment;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.model.UserProfile;
import com.seem.android.service.Api;
import com.seem.android.uimodel.NavDrawerItem;
import com.seem.android.uimodel.NavDrawerListAdapter;
import com.seem.android.util.ActionLauncherListener;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 03/04/14.
 */
public class MainActivity extends Activity implements
                                            LoginFragment.OnLoggedInInteractionListener,
                                            EditUserFragment.UserProfileInteractionListener,
                                            SignUpFragment.SignUpInteractionListener,
                                            SeemListFragment.OnSeemClickListener,
                                            UserProfileFragment.OnProfileListener,
                                            ActionLauncherListener{

    private boolean lastDrawerStatus = true;

    //GCM
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String regid;


    //
    private ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
    private NavDrawerItem drawerItemHome = new NavDrawerItem("Home", R.drawable.home);
    private NavDrawerItem drawerItemLogin = new NavDrawerItem("Login", R.drawable.sign_in);
    private NavDrawerItem drawerItemSignUp = new NavDrawerItem("Sign Up", R.drawable.plus_square);
    private NavDrawerItem drawerItemUserProfile = new NavDrawerItem("User Profile", R.drawable.user);


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList ;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;


    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "71b1dd17");

        setContentView(R.layout.activity_main);


        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.


        findViewById(R.id.drawer_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int parentWidth = findViewById(R.id.drawer_layout).getWidth();
                int parentHeight = findViewById(R.id.drawer_layout).getHeight();
                GlobalVars.GRID_SIZE = parentHeight / GlobalVars.GRID_NUMBER_OF_PHOTOS;
                GlobalVars.GRID_SIZE_V2 = parentWidth / GlobalVars.GRID_NUMBER_OF_PHOTOS_V2;

                GlobalVars.SCREEN_HEIGHT = parentHeight;
                GlobalVars.SCREEN_WIDTH = parentWidth;
            }
        });


        mTitle = mDrawerTitle = getTitle();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        buildDrawerMenu();

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);




        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0, drawerItemHome);
        }
        // Check device for Play Services APK.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }else{
            Utils.debug(getClass(),"No google apis");
        }


        //TODO URI check
        Uri data = getIntent().getData();
        if(data != null) {
            String scheme = data.getScheme(); // "seem"
            String host = data.getHost(); // "itemId"
            List<String> params = data.getPathSegments(); //535441ad8346070200e96e2d

            Utils.debug(getClass(), "" + scheme + " " + host + " " + params);
            if(host.equals("itemId")) {
                //ActivityFactory.startThreadedActivity(this, params.get(0));
            }else{
                Utils.debug(getClass(),"Unknown action");
            }


        }else{
            Utils.debug(getClass(), "No data");
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.EXTRA_DRAWER_STATUS)){
            lastDrawerStatus = savedInstanceState.getBoolean(GlobalVars.EXTRA_DRAWER_STATUS);
            mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
        }

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        Utils.debug(getClass(),"BackStackChanged");
                        /*
                        int backStackEntryCount = getFragmentManager().;
                        if(backStackEntryCount > 1){
                            lastDrawerStatus = false;
                            mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
                        }else{
                            lastDrawerStatus = true;
                            mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
                        }*/
                    }
                });

    }

    private void buildDrawerMenu(){
        navDrawerItems.clear();



        if(MyApplication.isLoggedIn()) {
            navDrawerItems.add(drawerItemHome);
            navDrawerItems.add(drawerItemUserProfile);
        }else{
            navDrawerItems.add(drawerItemHome);
            navDrawerItems.add(drawerItemLogin);
            navDrawerItems.add(drawerItemSignUp);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu., menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            //case R.id.action_settings:
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */

    private void displayView(Fragment fragment){
        displayView(-1,null,fragment,true);
    }

    private void displayView(int position, NavDrawerItem navDrawerItem) {
        displayView(position,navDrawerItem,null,false);
    }
    private void displayView(int position, NavDrawerItem navDrawerItem,Fragment fragment,boolean addToBack) {
        // update the main content by replacing fragments
        String menuTitle ="";
        if(fragment == null) {
            lastDrawerStatus = true;
            mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
            menuTitle = navDrawerItem.getTitle();
            if (navDrawerItem == drawerItemHome) {
                fragment = new HomeFragment();
            } else if (navDrawerItem == drawerItemLogin) {
                fragment = LoginFragment.newInstance();
            } else if (navDrawerItem == drawerItemSignUp) {
                fragment = SignUpFragment.newInstance();
            } else if (navDrawerItem == drawerItemUserProfile) {
                fragment = UserProfileFragment.newInstance(MyApplication.getUsername());
            }

        } else {
            lastDrawerStatus = false;
            mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
            position = -1;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(position == -1) {
                transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit);
            }
            transaction.replace(R.id.frame_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(menuTitle);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Utils.debug(getClass(), "Error in creating fragment");
        }

        if (!getActionBar().isShowing()){
            getActionBar().show();
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(getClass(),"onSaveInstanceState");
        outState.putBoolean(GlobalVars.EXTRA_DRAWER_STATUS,lastDrawerStatus);
    }

    @Override
    public void hasLoggedIn() {
        buildDrawerMenu();
        displayView(0,drawerItemHome);
    }

    @Override
    public void editProfile() {

        displayView(EditUserFragment.newInstance());

    }

    @Override
    public void hasLoggedOut() {
        buildDrawerMenu();
        displayView(0,drawerItemHome);
    }
    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
        super.onResume();
        Utils.debug(getClass(), "onResume: " + lastDrawerStatus);
        checkPlayServices();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"On Activity Result "+requestCode+ " "+resultCode);

        mDrawerToggle.setDrawerIndicatorEnabled(lastDrawerStatus);
    }





    @Override
    public void onClick(Seem seem) {
        Fragment newFragment = SeemItemsListFragment.newInstance(seem);
        displayView(newFragment);
    }

    @Override
    public void launchViewProfile(UserProfile userProfile) {
        Fragment fragment = UserProfileFragment.newInstance(userProfile.getUsername());
        displayView(fragment);
    }

    @Override
    public void launchSeeConversation(Item item) {
        Fragment fragment = ConversationViewFragmentFragment.newInstance(item);
        displayView(fragment);
    }


    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // display view for selected nav drawer item
                displayView(position,(NavDrawerItem) adapter.getItem(position));
        }
    }

    /**
     * -------------------------------------------------------------------------------
     *
     * NOTIFICATION STUFF
     *
     */

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Utils.debug(getClass(),"Device not supported");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId() {
        String gcmToken = MyApplication.getGcmToken();
        if (gcmToken.isEmpty()) {
            Utils.debug(getClass(), "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.

        if (MyApplication.getLastAppVersion() != MyApplication.getAppVersion()) {
            Utils.debug(getClass(), "App version changed.");
            return "";
        }
        return gcmToken;
    }



    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(GlobalVars.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    MyApplication.storeGcmToken(regid);

                    if (MyApplication.isLoggedIn()) {
                         if(Api.addGcmToken(regid, MyApplication.getToken())) {
                            Utils.debug(getClass(), "Token registered on the server: " + regid);
                        }

                    }
                }catch(IOException ex){
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.execute();
    }



}
