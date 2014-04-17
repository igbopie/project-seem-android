package com.seem.android.mockup1.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.adapters.ThreadedAdapter;

import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 28/03/14.
 */
public class ThreadedViewActivity extends ListActivity {

    String bottomItemId;

    ThreadedAdapter adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        adapter = new ThreadedAdapter(new ArrayList<Item>(),this);
        setListAdapter(adapter);
        bottomItemId = this.getIntent().getStringExtra(GlobalVars.EXTRA_ITEM_ID);
        new GetThreadsView(false).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Item item = adapter.getItem(position);
        Utils.debug(this.getClass(),"Item Clicked! seem "+item);
        ActivityFactory.startItemActivity(this,item.getSeemId(), item.getId());
    }

    private class GetThreadsView extends AsyncTask<Void,Void,List<Item>> {
        private boolean refresh = false;
        private final ProgressDialog dialog = new ProgressDialog(ThreadedViewActivity.this);

        private GetThreadsView(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading seems...");
            dialog.show();
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {

            List<Item> stack = new ArrayList<Item>();
            Item next = ItemService.getInstance().findItemById(bottomItemId);

            stack.add(0, next);

            while(next.getReplyTo() != null){
                next = ItemService.getInstance().findItemById(next.getReplyTo());
                stack.add(0, next);
            }

            return stack;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();

            dialog.dismiss();

            ThreadedViewActivity.this.getListView().setSelection(result.size()-1);

        }
    }


}
