package com.seem.android.mockup1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.fragments.ItemFullScreenFragment;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 23/03/14.
 */
public class ItemsFullScreenActivity extends ActionBarActivity {

    List<Item> itemList = new ArrayList<Item>();
    ViewPager mViewPager;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;


    public String getSeemId(){
        return getIntent().getStringExtra(GlobalVars.EXTRA_SEEM_ID);
    }
    public String getCurrentItemId(){
        return getIntent().getStringExtra(GlobalVars.EXTRA_CURRENT_ITEM_ID);
    }
    public String getParentItemId(){
        return getIntent().getStringExtra(GlobalVars.EXTRA_PARENT_ITEM_ID);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_fullscreen_view);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        itemList = new ArrayList<Item>();
        mAppSectionsPagerAdapter.notifyDataSetChanged();

        if (getActionBar() != null  && getActionBar().isShowing()){
            getActionBar().hide();
        }

        if(savedInstanceState  != null && savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CURRENT_ITEM_ID)){
            getIntent().putExtra(GlobalVars.EXTRA_CURRENT_ITEM_ID,
                    savedInstanceState.getString(GlobalVars.SAVED_BUNDLE_CURRENT_ITEM_ID));
        }

        new InitAsyncTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Item currentItem = itemList.get(mViewPager.getCurrentItem());
        outState.putString(GlobalVars.SAVED_BUNDLE_CURRENT_ITEM_ID,currentItem.getId());

        Utils.debug(this.getClass(),"Items Fullscreen Activity-Saved Instance currentItem "+currentItem.getId());
    }

    public class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return itemList.size();
        }

        public Fragment getItem(int position) {
            Utils.debug(this.getClass(),"ItemPosition:" + position + " ID:" + position);
            Item currentItem = itemList.get(position);
            ItemFullScreenFragment newFragment = ItemFullScreenFragment.newInstance(ItemsFullScreenActivity.this.getSeemId(),currentItem.getId(),currentItem.getId().equals(getParentItemId()));
            return newFragment;
        }

        public int getItemPosition(Object fr) {
            ItemFullScreenFragment fragment = (ItemFullScreenFragment)fr;
            Item item =  ItemService.getInstance().findItemById(fragment.getItemId());
            int position = itemList.indexOf(item);
            Utils.debug(this.getClass(),"ItemPosition:"+position);
            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }
        public CharSequence getPageTitle(int position) {
            return "Image "+position;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"ItemFullScreen - onActivityResult");

        if (requestCode == GlobalVars.RETURN_CODE_THREADED_VIEW && resultCode == Activity.RESULT_OK) {

            Map<String,String> dataMap = new HashMap<String, String>();
            dataMap.put(GlobalVars.EXTRA_SEEM_ID,data.getStringExtra(GlobalVars.EXTRA_SEEM_ID));
            dataMap.put(GlobalVars.EXTRA_ITEM_ID,data.getStringExtra(GlobalVars.EXTRA_ITEM_ID));
            ActivityFactory.finishActivityWithData(this, dataMap, Activity.RESULT_OK);
        }

    }

    public class InitAsyncTask extends AsyncTask<Void,Void,List<Item>>{
        String currentItemId = ItemsFullScreenActivity.this.getCurrentItemId();
        String parentItemId = ItemsFullScreenActivity.this.getParentItemId();
        Item currentItem;

        @Override
        protected List<Item> doInBackground(Void... voids) {
            List<Item> items = new ArrayList<Item>();
            //this can be a potentially slow operation. Should be loaded in the background
            currentItem = ItemService.getInstance().findItemById(currentItemId);
            Item parentItem = ItemService.getInstance().findItemById(parentItemId);
            //Add parent first
            items.add(parentItem);
            //Add children
            items.addAll(ItemService.getInstance().findItemReplies(parentItem.getId()));

            return items;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            itemList.clear();
            itemList.addAll(items);
            mAppSectionsPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(itemList.indexOf(currentItem));
        }
    }
}
