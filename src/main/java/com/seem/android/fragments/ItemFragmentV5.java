package com.seem.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.service.ItemService;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.ItemSelectedListener;
import com.seem.android.util.Utils;
import com.squareup.picasso.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ItemFragmentV5 extends Fragment implements View.OnClickListener{

    public static ItemFragmentV5 newInstance(String seemId,String itemId) {
        ItemFragmentV5 f = new ItemFragmentV5();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        f.setArguments(args);
        return f;
    }


    private boolean refresh = true;

    private Item item;
    private Item parentItem;

    private List<Item> replies = new ArrayList<Item>();


    Seem seem = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);



    }

    public View findViewById(int id){
        return getActivity().findViewById(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_view_v5, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetItems().execute();


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);



        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());



    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(), "ItemActivity - onSaveInstanceState");
        outState.putString(GlobalVars.EXTRA_SEEM_ID, getSeemId());
        outState.putString(GlobalVars.EXTRA_ITEM_ID,getItemId());
    }

    protected void recoverFromSavedState(Bundle savedInstanceState) {

        Utils.debug(this.getClass(), "ItemActivity - recoverFromSavedState");
        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.EXTRA_SEEM_ID)){
            getArguments().putString(GlobalVars.EXTRA_SEEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_SEEM_ID));
            getArguments().putString(GlobalVars.EXTRA_ITEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_ITEM_ID));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.debug(getClass(),"onDestroyView");
        cleanMe();
    }




    @Override
    public void onResume() {
        super.onResume();
        Utils.debug(this.getClass(),"Item Fragment - On resume");

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }



    public String getSeemId(){
        String seemId = getArguments().getString(GlobalVars.EXTRA_SEEM_ID);
        return  seemId;
    }
    public String getItemId(){
        String itemId =getArguments().getString(GlobalVars.EXTRA_ITEM_ID);
        return  itemId;
    }

    @Override
    public void onClick(View view) {

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
            getActivity().setTitle(seem.getTitle());
        }
    }


    private class GetItems extends AsyncTask<Void,Void,Void> {

        List<Item> items = new ArrayList<Item>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... id) {
            item = ItemService.getInstance().findItemById(getItemId(),refresh,true);
            Utils.debug(this.getClass(),"This is the item:" + item);
            items.add(item);
            while(item.getReplyTo() != null){
                item = ItemService.getInstance().findItemById(item.getReplyTo());
                items.add(item);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /*for(Item item:items){
                adapter.addItem(item);
            }*/


        }
    }






    private OnItemClickListener onItemClickListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemClickListener = (OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onItemClickListener = null;
    }

    public interface OnItemClickListener {
        public void onClick(String seemId, String itemId);

        public void onFinish();

        public void onProfileClick(String username);
    }




    private void cleanMe(){
        for(Field field:this.getClass().getFields()){
            try {
                if(!field.getType().isPrimitive()){
                    field.set(this,null);
                }
            } catch (IllegalAccessException e) {
                Utils.debug(getClass(),e.getMessage());
            }
        }
    }


}
