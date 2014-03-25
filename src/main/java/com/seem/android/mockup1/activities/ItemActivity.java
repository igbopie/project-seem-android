package com.seem.android.mockup1.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jess.ui.TwoWayGridView;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.adapters.ThumbnailAdapter;
import com.seem.android.mockup1.fragments.ItemFragment;
import com.seem.android.mockup1.fragments.ItemFullScreenFragment;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.util.Utils;
import com.seem.android.mockup1.fragments.ItemFragmentSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class ItemActivity extends FragmentActivity implements ItemFragmentSelectedListener {



    Seem seem = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.debug("ItemActivity OnCreate");
        setContentView(R.layout.activity_seem_view);

        String seemId = getIntent().getStringExtra(GlobalVars.EXTRA_SEEM_ID);
        String itemId = getIntent().getStringExtra(GlobalVars.EXTRA_ITEM_ID);
        seem = AppSingleton.getInstance().findSeemById(seemId);
        this.setTitle(seem.getTitle());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ItemFragment newFragment = ItemFragment.newInstance(seemId,itemId,0);
        transaction.add(R.id.linearLayout, newFragment);
        transaction.commit();

    }

    @Override
    public void itemSelected(String id, int depth) {
        boolean add = false;
        Utils.debug("Hey " + id);
    }


}
