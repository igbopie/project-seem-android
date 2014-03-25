package com.seem.android.mockup1.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jess.ui.TwoWayAbsListView;
import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.model.Item;
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
    LinkedList<FetchThumbs> fetchQeue = new LinkedList<FetchThumbs>();
    ItemSelectedListener repliesListener;

    public ThumbnailAdapter(Context context,ItemSelectedListener repliesListener) {
        super();
        this.context = context;
        this.repliesListener = repliesListener;
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
        synchronized (fetchQeue) {
            FetchThumbs ft = new FetchThumbs(view, item);
            fetchQeue.add(ft);

            FetchThumbs oldExec = execViewMap.get(view);
            execViewMap.put(view, ft);

            if (oldExec != null) {
                oldExec.cancel(true);
            }
        }
        checkProcessing();
    }

    private  void checkProcessing(){
        synchronized (fetchQeue) {
            Utils.debug("Check Processing");
            if (fetchQeue.size() > 0 &&
                fetchQeue.getFirst().getStatus() == AsyncTask.Status.PENDING) {
                Utils.debug("No one is executing so I execute the first one");
                fetchQeue.pop().execute();
            }
        }
    }

    private class FetchThumbs extends AsyncTask<Void,Void,Void> {
        private SpinnerImageView imageView;
        private Item item;

        public FetchThumbs(SpinnerImageView imageView,Item item){
            this.imageView = imageView;
            this.item = item;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if(item.getImageThumb() == null) {
                    Api.downloadThumbImage(item);
                }
                return null;
            } catch (IOException e) {
                Utils.debug("Pete al bajar la imagen", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(this.isCancelled()){
                //well... do not paint...
            }else if(item != null) {

                imageView.getImageView().setImageDrawable(item.getImageThumb());

                if(item.getReplyCount() > 0) {
                    imageView.setHasReplies(true);
                    imageView.setViewRepliesOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.debug("View");
                            repliesListener.itemSelected(item);
                        }
                    });
                }
                imageView.getImageView().setVisibility(View.VISIBLE);
                imageView.setLoading(false);


            }
            checkProcessing();
        }
    }
}
