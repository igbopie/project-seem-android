package com.seem.android.mockup1.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.adapters.SeemAdapter;
import com.seem.android.mockup1.adapters.ThreadedAdapter;
import com.seem.android.mockup1.model.Seem;

import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
            if(next.getImageThumb() == null){
                try {
                    Api.downloadThumbImage(next);
                } catch (IOException e) {
                    Utils.debug(this.getClass(),"Error al bajarse la imagen");
                }
            }
            stack.add(0, next);

            while(next.getReplyTo() != null){
                next = ItemService.getInstance().findItemById(next.getReplyTo());
                if(next.getImageThumb() == null){
                    try {
                        Api.downloadThumbImage(next);
                    } catch (IOException e) {
                        Utils.debug(this.getClass(),"Error al bajarse la imagen");
                    }
                }
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
