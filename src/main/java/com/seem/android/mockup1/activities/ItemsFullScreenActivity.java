package com.seem.android.mockup1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.fragments.ItemFullScreenFragment;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 23/03/14.
 */
public class ItemsFullScreenActivity extends ActionBarActivity {

    List<Item> itemList;
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

        itemList = new ArrayList<Item>();

        String currentItemId = this.getCurrentItemId();
        String parentItemId = this.getParentItemId();

        Item currentItem = AppSingleton.getInstance().findItemById(currentItemId);
        Item parentItem = AppSingleton.getInstance().findItemById(parentItemId);
        //Add parent first
        itemList.add(parentItem);
        //Add children
        itemList.addAll(AppSingleton.getInstance().findItemReplies(parentItem.getId()));

        setContentView(R.layout.activity_item_fullscreen_view);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        mViewPager.setCurrentItem(itemList.indexOf(currentItem));

    }


    public class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return itemList.size();
        }

        public Fragment getItem(int position) {
            Utils.debug("ItemPosition:" + position + " ID:" + position);
            ItemFullScreenFragment newFragment = ItemFullScreenFragment.newInstance(ItemsFullScreenActivity.this.getSeemId(),itemList.get(position).getId());
            return newFragment;
        }

        public int getItemPosition(Object fr) {
            ItemFullScreenFragment fragment = (ItemFullScreenFragment)fr;
            Item item = AppSingleton.getInstance().findItemById(fragment.getItemId());
            int position = itemList.indexOf(item);
            Utils.debug("ItemPosition:"+position);
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
}
