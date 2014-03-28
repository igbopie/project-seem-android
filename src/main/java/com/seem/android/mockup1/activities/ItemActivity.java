package com.seem.android.mockup1.activities;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.fragments.ItemFragment;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.Utils;
import com.seem.android.mockup1.fragments.ItemFragmentSelectedListener;

import java.util.ArrayList;


public class ItemActivity extends FragmentActivity implements ItemFragmentSelectedListener {



    Seem seem = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);

        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());
        setContentView(R.layout.activity_seem_view);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ItemFragment newFragment = ItemFragment.newInstance(getSeemId(),getItemId(),0);
        transaction.add(R.id.linearLayout, newFragment);
        transaction.commit();

    }

    @Override
    public void itemSelected(String id, int depth) {
        boolean add = false;
        Utils.debug(this.getClass(),"Hey " + id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(),"ItemActivity - onSaveInstanceState");
        outState.putString(GlobalVars.EXTRA_SEEM_ID, getSeemId());
        outState.putString(GlobalVars.EXTRA_ITEM_ID,getItemId());
    }

    protected void recoverFromSavedState(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.EXTRA_SEEM_ID)){
            getIntent().putExtra(GlobalVars.EXTRA_SEEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_SEEM_ID));
            getIntent().putExtra(GlobalVars.EXTRA_ITEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_ITEM_ID));
        }
    }


    public String getSeemId(){
        String seemId = getIntent().getStringExtra(GlobalVars.EXTRA_SEEM_ID);
        return  seemId;
    }
    public String getItemId(){
        String itemId = getIntent().getStringExtra(GlobalVars.EXTRA_ITEM_ID);
        return  itemId;
    }

    public class InitAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            seem = SeemService.getInstance().findSeemById(getSeemId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ItemActivity.this.setTitle(seem.getTitle());
        }
    }
}
