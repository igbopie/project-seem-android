package com.seem.android.mockup1.activities;



import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.adapters.SeemAdapter;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */

public class SeemListActivity extends ListActivity {

    private SeemAdapter adapter;


    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        new GetSeemsTask(false).execute();
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
        ActivityFactory.startItemActivity(SeemListActivity.this, seem.getId(), seem.getItemId());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.seem_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_camera:
                //newGame();
                Utils.debug("NEW SEEM!");
                ActivityFactory.startCreateSeemActivity(this);
                return true;
            case R.id.action_refresh:
                //newGame();
                Utils.debug("Refresh Seems!");
                new GetSeemsTask(true).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.RETURN_CODE_CREATE_SEEM && resultCode == Activity.RESULT_OK) {
            Utils.debug("Seem created!");
            new GetSeemsTask(false).execute();
        }
    }



    private class GetSeemsTask extends AsyncTask<Void,Void,List<Seem>> {
        private boolean refresh = false;
        private final ProgressDialog dialog = new ProgressDialog(SeemListActivity.this);

        private GetSeemsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading seems...");
            dialog.show();
        }

        @Override
        protected List<Seem> doInBackground(Void... voids) {
            List<Seem> seems = SeemService.getInstance().findSeems(refresh);
            Utils.debug("This is the seems:" + seems);

            return seems;
        }

        @Override
        protected void onPostExecute(List<Seem> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }

}

