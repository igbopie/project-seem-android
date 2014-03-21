package com.seem.android.mockup1.activities;



import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.adapters.SeemAdapter;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */

public class SeemsListView extends ListActivity {

    private SeemAdapter adapter;

    private Seem seemClicked = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        new GetSeemsTask().execute();
        adapter = new SeemAdapter(new ArrayList<Seem>(),this);
        setListAdapter(adapter);

        getListView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int parentWidth = getListView().getWidth();
                int parentHeight = getListView().getHeight();
                GlobalVars.GRID_SIZE = parentHeight / GlobalVars.GRID_NUMBER_OF_PHOTOS;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Seem seem = adapter.getItem(position);
        Utils.debug("Item Clicked! seem "+seem);
        seemClicked = seem;
        //Create Seem activity
        Intent intent = new Intent(SeemsListView.this, SeemView.class);
        intent.putExtra(GlobalVars.EXTRA_SEEM_ID,seemClicked.getId());
        startActivity(intent);

    }

    private class GetSeemsTask extends AsyncTask<Void,Void,List<Seem>> {
        private final ProgressDialog dialog = new ProgressDialog(SeemsListView.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading seems...");
            dialog.show();
        }

        @Override
        protected List<Seem> doInBackground(Void... voids) {
            List<Seem> seems = Api.getSeems();
            Utils.debug("This is the seems:" + seems);

            return seems;
        }

        @Override
        protected void onPostExecute(List<Seem> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();
            dialog.dismiss();

            for(Seem seem:result){
                AppSingleton.getInstance().saveSeem(seem);
            }

        }
    }



}

