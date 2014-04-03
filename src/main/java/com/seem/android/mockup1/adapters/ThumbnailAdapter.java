package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jess.ui.TwoWayAbsListView;
import com.seem.android.mockup1.executor.AsyncExecutor;
import com.seem.android.mockup1.executor.MyAsyncTask;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.MediaService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.ItemSelectedListener;
import com.seem.android.mockup1.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 24/03/14.
 */
public class ThumbnailAdapter extends BaseAdapter {

    Context context;
    List<Item> items = new ArrayList<Item>();

    Map<View,FetchThumbs> execViewMap = new HashMap<View, FetchThumbs>();
    ItemSelectedListener repliesListener;
    ItemSelectedListener threadViewListener;
    public ThumbnailAdapter(Context context,ItemSelectedListener repliesListener,ItemSelectedListener threadViewListener) {
        super();
        this.context = context;
        this.repliesListener = repliesListener;
        this.threadViewListener = threadViewListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0){
            if (convertView == null) {
                //FAKE FIRST VIEW
                SpinnerImageView view = new SpinnerImageView(context, null);
                view.setLayoutParams(new TwoWayAbsListView.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));
                return view;
            } else {
                return convertView;
            }
        }else {
            SpinnerImageView view;
            if (convertView == null) {
                view = new SpinnerImageView(context, null);
                view.setLayoutParams(new TwoWayAbsListView.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));

            } else {
                //Reusing views...
                view = (SpinnerImageView) convertView;
                view.setLoading(true);
                view.getImageView().setVisibility(View.INVISIBLE);
                view.setRepliesNumber(0);
            }
            Item item = (Item) getItem(position);

            view.setText(item.getCaption());
            addProcess(view, item);

            return view;
        }
    }


    @Override
    public int getCount() {
        return items.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return items.get(i-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Item item){
        items.add(item);
        this.notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        this.notifyDataSetChanged();
    }

    private void addProcess(SpinnerImageView view,Item item){
        //be careful with reusing views...

        FetchThumbs ft = new FetchThumbs(view, item);
        AsyncExecutor.getInstance().add(ft);


        FetchThumbs oldExec = execViewMap.get(view);
        execViewMap.put(view, ft);

        if (oldExec != null) {
            AsyncExecutor.getInstance().cancelTask(oldExec);
        }

    }



    private class FetchThumbs extends MyAsyncTask {
        private SpinnerImageView imageView;
        private Item item;

        public FetchThumbs(SpinnerImageView imageView,Item item){
            this.imageView = imageView;
            this.item = item;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            MediaService.getInstance().getThumb(item.getMedia());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(this.isCancelled()){
                //well... do not paint...
            }else if(item != null) {
                imageView.getImageView().setImageDrawable(item.getMedia().getImageThumb());
                imageView.setRepliesNumber(item.getReplyCount());
                if(item.getReplyCount() > 0) {
                    imageView.setViewRepliesOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.debug(this.getClass(),"View");
                            repliesListener.itemSelected(item);
                        }
                    });
                }
                /*
                if(item.getDepth() > 0 ){
                    imageView.setDepthNumber(item.getDepth());
                    imageView.setViewThreadOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            threadViewListener.itemSelected(item);
                        }
                    });
                }*/

                imageView.getImageView().setVisibility(View.VISIBLE);
                imageView.setLoading(false);
            }
        }
    }
}
