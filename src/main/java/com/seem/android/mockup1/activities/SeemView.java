package com.seem.android.mockup1.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.fragments.ItemFragment;
import com.seem.android.mockup1.fragments.ItemFullScreenFragment;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.util.Utils;
import com.seem.android.mockup1.fragments.ItemFragmentSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class SeemView extends FragmentActivity implements ItemFragmentSelectedListener {


    Seem seem = null;

    List<String> depthRepliesIds = new ArrayList<String>();

    final Context context = this;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seem_view);

        String seemId = getIntent().getStringExtra(GlobalVars.EXTRA_SEEM_ID);
        seem = AppSingleton.getInstance().findSeemById(seemId);
        this.setTitle(seem.getTitle());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ItemFragment newFragment = ItemFragment.newInstance(seem.getItemId(),0);
        transaction.add(R.id.linearLayout, newFragment);
        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }



    @Override
    public void itemSelected(String id, int depth) {
        boolean add = false;
        Utils.debug("Hey " + id);



        // Create fragment and give it an argument for the selected article
        ItemFullScreenFragment newFragment = ItemFullScreenFragment.newInstance(id);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.linearLayout, newFragment);
        transaction.addToBackStack("Back");

        // Commit the transaction
        transaction.commit();

    }

}
