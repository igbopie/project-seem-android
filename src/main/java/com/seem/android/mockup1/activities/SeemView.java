package com.seem.android.mockup1.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.Utils;
import com.seem.android.mockup1.fragments.ReplyFragment;
import com.seem.android.mockup1.fragments.ReplyFragmentSelectedListener;
import com.seem.android.mockup1.model.Reply;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeemView extends ActionBarActivity implements ReplyFragmentSelectedListener,ActionBar.TabListener {

    ViewPager mViewPager;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    int depth = 0;

    List<Integer> depthRepliesIds = new ArrayList<Integer>();

    final Context context = this;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_seem_view);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                //getActionBar().setSelectedNavigationItem(position);
            }
        });

        /*
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }*/
        Reply baseReply = new Reply();
        baseReply.setId(AppSingleton.getInstance().getNewImageId());
        AppSingleton.getInstance().saveReply(baseReply);

        depthRepliesIds.add(baseReply.getId());
        mAppSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seem_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void replySelected(int id, int depth) {
        boolean add = false;
        Utils.debug("Hey "+id);
        if(depth < depthRepliesIds.size()){
            int lastId = depthRepliesIds.get(depth);
            if(lastId == id){
                //Already exist, just navigate
            } else {
                for(int i = depthRepliesIds.size()-1;depth < depthRepliesIds.size();i--){
                    depthRepliesIds.remove(i);
                }
                add = true;
            }
        } else {
            add = true;
        }
        if(add){
            depthRepliesIds.add(id);
            mAppSectionsPagerAdapter.notifyDataSetChanged();
        }

        mViewPager.setCurrentItem(depth);
        /*ReplyFragment replyFragment = (ReplyFragment)
                getSupportFragmentManager().findFragmentById(R.id.reply_fragment);


        // Create fragment and give it an argument for the selected article
        ReplyFragment newFragment = ReplyFragment.newInstance(id,depth);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.linear_layout, newFragment);
        transaction.addToBackStack("Back");

        // Commit the transaction
        transaction.commit();*/

    }

    private void setupActionBar() {
        ActionBar ab = getActionBar();

        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(false);
        //        ab.setIcon(R.drawable.seem_logo);
        ab.setDisplayUseLogoEnabled(false);
        // Specify that we will be displaying tabs in the action bar.
        //ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(R.layout.action_bar_title, null);


        ab.setCustomView(v);

        //ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }
    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return depthRepliesIds.size();
        }

        public Fragment getItem(int position) {
            Utils.debug("ItemPosition:"+position+" ID:"+depthRepliesIds.get(position));
            ReplyFragment newFragment = ReplyFragment.newInstance(depthRepliesIds.get(position),position);
            return newFragment;
        }

        public int getItemPosition(Object item) {
            ReplyFragment fragment = (ReplyFragment)item;
            int position = depthRepliesIds.indexOf(fragment.getReplyId());
            Utils.debug("ItemPosition:"+position);
            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }
        public CharSequence getPageTitle(int position) {
            return "Depth "+position;
        }
    }
}
